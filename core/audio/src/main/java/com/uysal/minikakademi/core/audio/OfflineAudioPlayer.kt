package com.uysal.minikakademi.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams

class OfflineAudioPlayer(
    private val context: Context
) : AutoCloseable {

    private var speechPlayer: MediaPlayer? = null
    private var sfxPlayer: MediaPlayer? = null

    fun playSpeech(audioId: String, speed: Float = 1.0f): Boolean =
        playAsset(
            assetPath = "speech/$audioId.ogg",
            speed = speed.coerceIn(0.80f, 1.00f),
            speech = true
        )

    fun playSfx(sfxId: String): Boolean =
        playAsset(
            assetPath = "sfx/$sfxId.ogg",
            speed = 1.0f,
            speech = false
        )

    fun hasSpeech(audioId: String): Boolean = assetExists("speech/$audioId.ogg")
    fun hasSfx(sfxId: String): Boolean = assetExists("sfx/$sfxId.ogg")

    fun stopSpeech() {
        speechPlayer = release(speechPlayer)
    }

    fun stopSfx() {
        sfxPlayer = release(sfxPlayer)
    }

    fun stopAll() {
        stopSpeech()
        stopSfx()
    }

    override fun close() {
        stopAll()
    }

    private fun playAsset(
        assetPath: String,
        speed: Float,
        speech: Boolean
    ): Boolean {
        if (speech) stopSpeech() else stopSfx()

        return runCatching {
            val afd = context.assets.openFd(assetPath)
            val player = MediaPlayer()

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(
                        if (speech) AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY
                        else AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                    )
                    .setContentType(
                        if (speech) AudioAttributes.CONTENT_TYPE_SPEECH
                        else AudioAttributes.CONTENT_TYPE_SONIFICATION
                    )
                    .build()
            )
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            player.prepare()

            if (speech && speed != 1.0f) {
                player.playbackParams = PlaybackParams()
                    .setSpeed(speed)
                    .setPitch(1.0f)
            }

            player.setOnCompletionListener {
                runCatching { it.release() }
                if (speech && speechPlayer === it) speechPlayer = null
                if (!speech && sfxPlayer === it) sfxPlayer = null
            }

            if (speech) speechPlayer = player else sfxPlayer = player
            player.start()
            true
        }.getOrElse {
            if (speech) speechPlayer = null else sfxPlayer = null
            false
        }
    }

    private fun assetExists(assetPath: String): Boolean =
        runCatching {
            context.assets.openFd(assetPath).use { true }
        }.getOrDefault(false)

    private fun release(player: MediaPlayer?): MediaPlayer? {
        runCatching { player?.stop() }
        runCatching { player?.release() }
        return null
    }
}
