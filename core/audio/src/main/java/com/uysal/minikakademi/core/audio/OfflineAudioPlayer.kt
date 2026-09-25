package com.uysal.minikakademi.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams

class OfflineAudioPlayer(
    private val context: Context
) : AutoCloseable {

    private var mediaPlayer: MediaPlayer? = null

    fun playSpeech(audioId: String, speed: Float = 1.0f): Boolean =
        playAsset("speech/$audioId.ogg", speed)

    fun playSfx(sfxId: String): Boolean =
        playAsset("sfx/$sfxId.ogg", 1.0f)

    fun stop() {
        runCatching { mediaPlayer?.stop() }
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
    }

    override fun close() {
        stop()
    }

    private fun playAsset(assetPath: String, speed: Float): Boolean {
        stop()
        return runCatching {
            val afd = context.assets.openFd(assetPath)
            val player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            player.prepare()

            if (speed != 1.0f) {
                player.playbackParams = PlaybackParams().setSpeed(speed.coerceIn(0.8f, 1.0f))
            }

            player.setOnCompletionListener {
                runCatching { it.release() }
                if (mediaPlayer === it) mediaPlayer = null
            }
            mediaPlayer = player
            player.start()
            true
        }.getOrElse {
            mediaPlayer = null
            false
        }
    }
}
