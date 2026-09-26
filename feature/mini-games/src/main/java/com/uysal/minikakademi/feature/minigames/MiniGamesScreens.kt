package com.uysal.minikakademi.feature.minigames

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uysal.minikakademi.core.audio.OfflineAudioPlayer
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.designsystem.LearningObjectArt
import kotlinx.coroutines.delay
import org.json.JSONObject

data class MiniGameOption(
    val id: String,
    val label: String,
    val correct: Boolean
)

data class MiniGameConfig(
    val id: String,
    val title: String,
    val activityType: String,
    val instruction: String,
    val instructionAudioId: String,
    val successAudioId: String,
    val retryAudioId: String,
    val successSfx: String,
    val retrySfx: String,
    val targetSymbol: String,
    val options: List<MiniGameOption>
)

private object MiniGameLoader {
    fun load(context: Context): List<MiniGameConfig> =
        context.assets.list("mini_games")
            .orEmpty()
            .filter { it.endsWith(".json") }
            .sorted()
            .mapNotNull { fileName ->
                runCatching {
                    val text = context.assets.open("mini_games/$fileName")
                        .bufferedReader()
                        .use { it.readText() }
                    parse(JSONObject(text))
                }.getOrNull()
            }

    private fun parse(json: JSONObject): MiniGameConfig {
        val interaction = json.getJSONObject("interaction")
        val options = buildList {
            val array = interaction.optJSONArray("options")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(
                        MiniGameOption(
                            id = item.getString("id"),
                            label = item.optString("label", item.optString("value", "")),
                            correct = item.optBoolean("correct", false)
                        )
                    )
                }
            }
        }

        val instruction = json.getJSONObject("instruction")
        val feedback = json.getJSONObject("feedback")
        return MiniGameConfig(
            id = json.getString("id"),
            title = json.getString("title"),
            activityType = json.getString("activityType"),
            instruction = instruction.getString("text"),
            instructionAudioId = instruction.getString("audioId"),
            successAudioId = feedback.getString("successAudioId"),
            retryAudioId = feedback.getString("retryAudioId"),
            successSfx = feedback.getString("successSfx").lowercase(),
            retrySfx = feedback.getString("retrySfx").lowercase(),
            targetSymbol = json.getJSONObject("learningTarget").optString("targetSymbol", ""),
            options = options
        )
    }
}

private fun gameUnlocked(
    id: String,
    completedTracing: Set<String>,
    completedLiteracy: Set<String>,
    completedMath: Set<String>
): Boolean = when (id) {
    "GAME-LIT-HUNT-001" -> completedLiteracy.isNotEmpty()
    "GAME-MATH-COUNT-001" -> "ACT-MAT-NUM-02" in completedMath
    "GAME-MATH-MATCH-001" -> "ACT-MAT-EQ-02" in completedMath
    "GAME-PRE-MAZE-001" -> completedTracing.isNotEmpty()
    "GAME-MATH-GROUP-001" -> "ACT-MAT-TENS-01" in completedMath
    else -> false
}

@Composable
fun MiniGamesHomeScreen(
    avatarId: String,
    completedMiniGames: Set<String>,
    completedTracingActivities: Set<String>,
    completedLiteracyActivities: Set<String>,
    completedMathematicsActivities: Set<String>,
    onOpenGame: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var games by remember { mutableStateOf(emptyList<MiniGameConfig>()) }

    LaunchedEffect(Unit) {
        games = MiniGameLoader.load(context)
    }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(avatarId = avatarId, size = 64.dp)
                Column {
                    Text("Oyun Zamanı", style = MaterialTheme.typography.headlineMedium)
                    Text("Öğrendiklerini kısa oyunlarla tekrar et.")
                }
            }

            if (games.isEmpty()) {
                KidCard { Text("Mini oyunlar hazırlanıyor.") }
            } else {
                games.forEach { game ->
                    val unlocked = gameUnlocked(
                        id = game.id,
                        completedTracing = completedTracingActivities,
                        completedLiteracy = completedLiteracyActivities,
                        completedMath = completedMathematicsActivities
                    )
                    val completed = game.id in completedMiniGames

                    Card(
                        onClick = { if (unlocked) onOpenGame(game.id) },
                        enabled = unlocked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                completed -> MaterialTheme.colorScheme.primaryContainer
                                unlocked -> MaterialTheme.colorScheme.surface
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                game.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                when {
                                    completed -> "✓ Tamamlandı — tekrar oynayabilirsin"
                                    unlocked -> game.instruction
                                    else -> "Biraz daha çalışınca açılacak."
                                }
                            )
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Ana Sayfa", onClick = onBack)
        }
    }
}

@Composable
fun MiniGameScreen(
    gameId: String,
    avatarId: String,
    narrationEnabled: Boolean,
    sfxEnabled: Boolean,
    speechRate: Float,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val config = remember(gameId) {
        MiniGameLoader.load(context).firstOrNull { it.id == gameId }
    }

    if (config == null) {
        KidScreen {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Oyun bulunamadı.")
                KidPrimaryButton(text = "Geri Dön", onClick = onBack)
            }
        }
        return
    }

    var completed by remember(gameId) { mutableStateOf(false) }
    val audioPlayer = remember(context) { OfflineAudioPlayer(context) }

    DisposableEffect(audioPlayer) {
        onDispose { audioPlayer.close() }
    }

    LaunchedEffect(config.id, narrationEnabled, sfxEnabled, speechRate) {
        if (sfxEnabled) {
            audioPlayer.playSfx("sfx_soft_pop")
            delay(220)
        }
        if (narrationEnabled) {
            audioPlayer.playSpeech(config.instructionAudioId, speechRate)
        }
    }

    LaunchedEffect(completed, sfxEnabled) {
        if (completed && sfxEnabled) {
            delay(900)
            audioPlayer.playSfx("sfx_complete")
        }
    }

    fun playRetryAudio() {
        if (narrationEnabled) audioPlayer.playSpeech(config.retryAudioId, speechRate)
        if (sfxEnabled) audioPlayer.playSfx(config.retrySfx)
    }

    fun completeOnce() {
        if (!completed) {
            completed = true
            if (narrationEnabled) audioPlayer.playSpeech(config.successAudioId, speechRate)
            if (sfxEnabled) audioPlayer.playSfx(config.successSfx)
            onComplete()
        }
    }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(avatarId = avatarId, size = 58.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(config.title, style = MaterialTheme.typography.headlineMedium)
                    Text(config.instruction)
                }
                if (narrationEnabled) {
                    Button(
                        onClick = {
                            if (sfxEnabled) audioPlayer.playSfx("sfx_gentle_tap")
                            audioPlayer.playSpeech(config.instructionAudioId, speechRate)
                        }
                    ) {
                        Text("🔊 Dinle")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (config.id) {
                    "GAME-LIT-HUNT-001" -> MultiFindGame(config, ::playRetryAudio, ::completeOnce)
                    "GAME-MATH-COUNT-001" -> CountAndChooseGame(config, ::playRetryAudio, ::completeOnce)
                    "GAME-MATH-MATCH-001" -> PairFindGame(config, ::playRetryAudio, ::completeOnce)
                    "GAME-PRE-MAZE-001" -> PathChoiceGame(config, ::playRetryAudio, ::completeOnce)
                    "GAME-MATH-GROUP-001" -> GroupTenGame(config, ::completeOnce)
                    else -> SingleChoiceGame(config, ::playRetryAudio, ::completeOnce)
                }
            }

            if (completed) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "Harika! Oyunu tamamladın.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            KidPrimaryButton(text = "Oyun Listesine Dön", onClick = onBack)
        }
    }
}

@Composable
private fun MultiFindGame(
    config: MiniGameConfig,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val correctIds = remember(config.id) {
        config.options.filter { it.correct }.map { it.id }.toSet()
    }
    var selected by remember(config.id) { mutableStateOf(emptySet<String>()) }
    var message by remember(config.id) { mutableStateOf("") }
    var finished by remember(config.id) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            config.options.forEach { option ->
                Button(
                    onClick = {
                        if (option.correct) {
                            val next = selected + option.id
                            selected = next
                            message = ""
                            if (!finished && correctIds.all { it in next }) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            message = "Bir daha bakalım."
                            onRetry()
                        }
                    }
                ) {
                    Text(
                        if (option.id in selected) "✓ ${option.label}" else option.label,
                        fontSize = 28.sp
                    )
                }
            }
        }
        Text("Bulunan: ${selected.size} / ${correctIds.size}")
        if (message.isNotBlank()) Text(message)
    }
}

@Composable
private fun CountAndChooseGame(
    config: MiniGameConfig,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val count = config.targetSymbol.toIntOrNull() ?: 0
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(count.coerceIn(0, 20)) {
                LearningObjectArt(label = "elma", size = 44.dp)
            }
        }
        SingleChoiceGame(config, onRetry, onComplete)
    }
}

@Composable
private fun PairFindGame(
    config: MiniGameConfig,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val correctIds = config.options.filter { it.correct }.map { it.id }.toSet()
    var selected by remember(config.id) { mutableStateOf(emptySet<String>()) }
    var message by remember(config.id) { mutableStateOf("") }
    var finished by remember(config.id) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Aynı olan iki kartı seç.", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            config.options.forEach { option ->
                Button(
                    onClick = {
                        if (option.correct) {
                            val next = selected + option.id
                            selected = next
                            if (!finished && correctIds.all { it in next }) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            message = "Bir daha bakalım."
                            onRetry()
                        }
                    }
                ) {
                    Text(if (option.id in selected) "✓ ${option.label}" else option.label, fontSize = 30.sp)
                }
            }
        }
        if (message.isNotBlank()) Text(message)
    }
}

@Composable
private fun PathChoiceGame(
    config: MiniGameConfig,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Avatar  →  🌼", fontSize = 34.sp)
        SingleChoiceGame(config, onRetry, onComplete)
    }
}

@Composable
private fun GroupTenGame(
    config: MiniGameConfig,
    onComplete: () -> Unit
) {
    var selected by remember(config.id) { mutableStateOf(emptySet<String>()) }
    var finished by remember(config.id) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Bir onluk oluştur.", style = MaterialTheme.typography.titleLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            config.options.forEach { option ->
                Button(
                    onClick = {
                        val next = selected + option.id
                        selected = next
                        if (!finished && next.size == config.options.size) {
                            finished = true
                            onComplete()
                        }
                    },
                    modifier = Modifier.size(58.dp)
                ) {
                    if (option.id in selected) {
                        Text("✓")
                    } else {
                        LearningObjectArt(label = "armut", size = 34.dp)
                    }
                }
            }
        }
        Text("${selected.size} / 10 nesne grupta")
    }
}

@Composable
private fun SingleChoiceGame(
    config: MiniGameConfig,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    var message by remember(config.id) { mutableStateOf("") }
    var finished by remember(config.id) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (config.targetSymbol.isNotBlank()) {
            Text(
                config.targetSymbol,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            config.options.forEach { option ->
                Button(
                    onClick = {
                        if (option.correct) {
                            message = "Doğru!"
                            if (!finished) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            message = "Bir daha bakalım."
                            onRetry()
                        }
                    }
                ) {
                    Text(option.label, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        if (message.isNotBlank()) Text(message)
    }
}
