package com.uysal.minikakademi.feature.literacy

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uysal.minikakademi.core.audio.OfflineAudioPlayer
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.designsystem.LearningObjectArt
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import org.json.JSONObject

data class LiteracyOption(
    val id: String,
    val label: String,
    val correct: Boolean
)

data class LiteracyActivityConfig(
    val id: String,
    val curriculumId: String,
    val title: String,
    val activityType: String,
    val instruction: String,
    val instructionAudioId: String,
    val successAudioId: String,
    val retryAudioId: String,
    val successSfx: String,
    val retrySfx: String,
    val targetSymbol: String,
    val options: List<LiteracyOption>
)

data class LiteracyNode(
    val id: String,
    val title: String,
    val order: Int,
    val sequenceOrder: Int,
    val prerequisites: List<String>
)

private object LiteracyContentLoader {
    private val directories = listOf(
        "literacy/group_01_anet",
        "literacy/group_02_iloku"
    )

    fun loadActivities(context: Context): List<LiteracyActivityConfig> =
        directories.flatMap { directory ->
            context.assets.list(directory)
                .orEmpty()
                .filter { it.endsWith(".json") }
                .sorted()
                .mapNotNull { fileName ->
                    runCatching {
                        val text = context.assets.open("$directory/$fileName")
                            .bufferedReader()
                            .use { it.readText() }
                        parseActivity(JSONObject(text))
                    }.getOrNull()
                }
        }

    fun loadNodes(context: Context, activities: List<LiteracyActivityConfig>): List<LiteracyNode> {
        val activityNodes = activities.map { it.curriculumId }.toSet()
        val text = context.assets.open("curriculum_manifest.json")
            .bufferedReader()
            .use { it.readText() }
        val root = JSONObject(text)
        val sequences = root.getJSONArray("sequences")
        val result = mutableListOf<LiteracyNode>()

        for (sequenceIndex in 0 until sequences.length()) {
            val sequence = sequences.getJSONObject(sequenceIndex)
            if (sequence.getString("domain") != "LITERACY") continue
            val nodes = sequence.getJSONArray("nodes")
            for (nodeIndex in 0 until nodes.length()) {
                val node = nodes.getJSONObject(nodeIndex)
                val id = node.getString("id")
                if (id !in activityNodes) continue
                val prerequisites = buildList {
                    val array = node.optJSONArray("prerequisites")
                    if (array != null) {
                        for (i in 0 until array.length()) add(array.getString(i))
                    }
                }
                result.add(
                    LiteracyNode(
                        id = id,
                        title = node.getString("title"),
                        order = node.getInt("order"),
                        sequenceOrder = sequenceIndex,
                        prerequisites = prerequisites
                    )
                )
            }
        }

        return result.sortedWith(compareBy<LiteracyNode> { it.sequenceOrder }.thenBy { it.order })
    }

    private fun parseActivity(json: JSONObject): LiteracyActivityConfig {
        val interaction = json.getJSONObject("interaction")
        val options = buildList {
            val array = interaction.optJSONArray("options")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(
                        LiteracyOption(
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
        return LiteracyActivityConfig(
            id = json.getString("id"),
            curriculumId = json.getString("curriculumId"),
            title = json.getString("title"),
            activityType = json.getString("activityType"),
            instruction = instruction.getString("text"),
            instructionAudioId = instruction.getString("audioId"),
            successAudioId = feedback.getString("successAudioId"),
            retryAudioId = feedback.getString("retryAudioId"),
            successSfx = feedback.getString("successSfx").lowercase(),
            retrySfx = feedback.getString("retrySfx").lowercase(),
            targetSymbol = json.getJSONObject("learningTarget").getString("targetSymbol"),
            options = options
        )
    }
}

@Composable
fun LiteracyHomeScreen(
    avatarId: String,
    completedActivityIds: Set<String>,
    onOpenLetter: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activities by remember { mutableStateOf(emptyList<LiteracyActivityConfig>()) }
    var nodes by remember { mutableStateOf(emptyList<LiteracyNode>()) }

    LaunchedEffect(Unit) {
        val loaded = LiteracyContentLoader.loadActivities(context)
        activities = loaded
        nodes = LiteracyContentLoader.loadNodes(context, loaded)
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
                    Text("Harfleri Öğreniyorum", style = MaterialTheme.typography.headlineMedium)
                    Text("Sesleri dinle, harfleri bul, yaz ve kelimeler oluştur.")
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (nodes.isEmpty()) {
                    KidCard { Text("Türkçe etkinlikleri hazırlanıyor.") }
                } else {
                    nodes.forEach { node ->
                        val nodeActivities = activities.filter { it.curriculumId == node.id }
                        val complete = nodeActivities.isNotEmpty() &&
                            nodeActivities.all { it.id in completedActivityIds }
                        val unlocked = node.prerequisites.all { prerequisite ->
                            val prerequisiteActivities = activities.filter { it.curriculumId == prerequisite }
                            prerequisiteActivities.isEmpty() ||
                                prerequisiteActivities.all { it.id in completedActivityIds }
                        }

                        Card(
                            onClick = { if (unlocked) onOpenLetter(node.id) },
                            enabled = unlocked,
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    complete -> MaterialTheme.colorScheme.primaryContainer
                                    unlocked -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                val letter = nodeLetter(node.id)
                                Text(
                                    text = letterUpper(letter) + " " + letter,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(node.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        when {
                                            complete -> "✓ Tamamlandı"
                                            unlocked -> "Çalışmaya hazır"
                                            else -> "Biraz daha çalışınca açılacak"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Ana Sayfa", onClick = onBack)
        }
    }
}

@Composable
fun LetterLessonScreen(
    curriculumId: String,
    avatarId: String,
    completedActivityIds: Set<String>,
    onOpenActivity: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activities = remember(curriculumId) {
        LiteracyContentLoader.loadActivities(context)
            .filter { it.curriculumId == curriculumId }
            .sortedBy { activityOrder(it.id) }
    }
    val letter = nodeLetter(curriculumId)

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
                AvatarPlaceholder(avatarId = avatarId, size = 60.dp)
                Column {
                    Text(
                        text = letterUpper(letter) + " - " + letter,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text("Etkinlikleri sırayla tamamla.")
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activities.forEachIndexed { index, activity ->
                    val previousComplete = index == 0 || activities[index - 1].id in completedActivityIds
                    val complete = activity.id in completedActivityIds
                    Card(
                        onClick = { if (previousComplete) onOpenActivity(activity.id) },
                        enabled = previousComplete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                complete -> MaterialTheme.colorScheme.primaryContainer
                                previousComplete -> MaterialTheme.colorScheme.surface
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = (index + 1).toString() + ". " + activity.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                when {
                                    complete -> "✓ Tamamlandı"
                                    previousComplete -> activity.instruction
                                    else -> "Önce bir önceki çalışmayı tamamla."
                                }
                            )
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Harf Listesine Dön", onClick = onBack)
        }
    }
}

@Composable
fun LiteracyActivityScreen(
    activityId: String,
    avatarId: String,
    narrationEnabled: Boolean,
    sfxEnabled: Boolean,
    speechRate: Float,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val config = remember(activityId) {
        LiteracyContentLoader.loadActivities(context).firstOrNull { it.id == activityId }
    }

    if (config == null) {
        KidScreen {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Etkinlik bulunamadı.")
                KidPrimaryButton(text = "Geri Dön", onClick = onBack)
            }
        }
        return
    }

    var completed by remember(activityId) { mutableStateOf(false) }
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
            audioPlayer.playSfx("sfx_star_sparkle")
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
                AvatarPlaceholder(avatarId = avatarId, size = 56.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(config.title, style = MaterialTheme.typography.headlineMedium)
                    Text(config.instruction, style = MaterialTheme.typography.bodyLarge)
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
                when (config.activityType) {
                    "LETTER_INTRO" -> LetterIntroGame(
                        symbol = config.targetSymbol,
                        onPlayPhoneme = {
                            if (narrationEnabled) {
                                audioPlayer.playSpeech("aud_phoneme_${config.targetSymbol.lowercase()}", speechRate)
                            }
                            if (sfxEnabled) audioPlayer.playSfx("sfx_gentle_tap")
                        },
                        onComplete = ::completeOnce
                    )
                    "FIND_LETTER", "FIND_SOUND_OBJECT" -> MultiSelectGame(
                        options = config.options,
                        showObjectArt = config.activityType == "FIND_SOUND_OBJECT",
                        onRetry = ::playRetryAudio,
                        onComplete = ::completeOnce
                    )
                    "TRACE_LETTER" -> LetterTraceGame(
                        symbol = config.targetSymbol,
                        onComplete = ::completeOnce
                    )
                    "BUILD_SYLLABLE", "BUILD_WORD" -> BuildTextGame(
                        target = config.targetSymbol,
                        onRetry = ::playRetryAudio,
                        onComplete = ::completeOnce
                    )
                    else -> KidCard {
                        Text("Bu etkinlik türü sonraki içerik motorunda açılacak.")
                    }
                }
            }

            if (completed) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "Harika! Bu çalışmayı tamamladın.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            KidPrimaryButton(text = "Etkinlik Listesine Dön", onClick = onBack)
        }
    }
}

@Composable
private fun LetterIntroGame(
    symbol: String,
    onPlayPhoneme: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(
            text = letterUpper(symbol),
            fontSize = 150.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = symbol,
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Button(
            onClick = {
                onPlayPhoneme()
                onComplete()
            }
        ) {
            Text("🔊 Harfi Dinle")
        }
        KidPrimaryButton(text = "Hazırım", onClick = onComplete)
    }
}

@Composable
private fun MultiSelectGame(
    options: List<LiteracyOption>,
    showObjectArt: Boolean,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val correctIds = remember(options) { options.filter { it.correct }.map { it.id }.toSet() }
    var selected by remember(options) { mutableStateOf(emptySet<String>()) }
    var retryMessage by remember { mutableStateOf("") }
    var finished by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { option ->
                val isSelected = option.id in selected
                Card(
                    onClick = {
                        if (option.correct) {
                            selected = selected + option.id
                            retryMessage = ""
                            if (!finished && selected.size + 1 >= correctIds.size &&
                                correctIds.all { it == option.id || it in selected }) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            retryMessage = "Bir daha bakalım."
                            onRetry()
                        }
                    },
                    modifier = Modifier.size(
                        width = 118.dp,
                        height = if (showObjectArt) 132.dp else 78.dp
                    ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (showObjectArt) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                LearningObjectArt(label = option.label, size = 70.dp)
                                Text(
                                    text = if (isSelected) "✓ " + option.label else option.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = if (isSelected) "✓ " + option.label else option.label,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        if (retryMessage.isNotBlank()) {
            Text(retryMessage, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = "Bulunan: " + selected.size + " / " + correctIds.size,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun LetterTraceGame(
    symbol: String,
    onComplete: () -> Unit
) {
    val guide = remember(symbol) { letterGuide(symbol) }
    var activeStrokeIndex by remember(symbol) { mutableStateOf(0) }
    var activeProgressIndex by remember(symbol) { mutableStateOf(0) }
    var acceptedStrokes by remember(symbol) { mutableStateOf(emptyList<List<Offset>>()) }
    var currentStroke by remember(symbol) { mutableStateOf(emptyList<Offset>()) }
    var currentStrokeAccepted by remember(symbol) { mutableStateOf(false) }
    var boardSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var finished by remember(symbol) { mutableStateOf(false) }
    var hint by remember(symbol) { mutableStateOf("1. hareketin büyük başlangıç noktasından başla.") }

    val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
    val futureGuideColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
    val completedGuideColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.44f)
    val strokeColor = MaterialTheme.colorScheme.primary
    val visitedColor = MaterialTheme.colorScheme.secondary
    val guideLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
    val activeArrowColor = MaterialTheme.colorScheme.primary

    fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    fun toCanvasPoint(normalized: Offset): Offset =
        Offset(normalized.x * boardSize.width, normalized.y * boardSize.height)

    fun tolerance(): Float =
        (boardSize.width.coerceAtMost(boardSize.height) * 0.07f).coerceIn(20f, 48f)

    fun startTolerance(): Float = tolerance() * 1.30f

    fun markForwardProgress(point: Offset) {
        if (!currentStrokeAccepted || activeStrokeIndex !in guide.indices) return
        val active = guide[activeStrokeIndex]
        if (active.isEmpty()) return

        val from = activeProgressIndex.coerceIn(0, active.lastIndex)
        val to = (from + 10).coerceAtMost(active.lastIndex)
        var bestIndex = from
        var bestDistance = Float.MAX_VALUE
        for (index in from..to) {
            val d = distance(toCanvasPoint(active[index]), point)
            if (d < bestDistance) {
                bestDistance = d
                bestIndex = index
            }
        }
        if (bestDistance <= tolerance() && bestIndex >= activeProgressIndex) {
            activeProgressIndex = bestIndex
        }
    }

    fun finishStroke() {
        if (!currentStrokeAccepted || activeStrokeIndex !in guide.indices) {
            currentStroke = emptyList()
            currentStrokeAccepted = false
            return
        }

        val active = guide[activeStrokeIndex]
        val ratio = if (active.isEmpty()) 0f else
            (activeProgressIndex + 1).toFloat() / active.size.toFloat()
        val reachedEnd = activeProgressIndex >= (active.lastIndex - 2).coerceAtLeast(0)

        if (ratio >= 0.90f && reachedEnd) {
            if (currentStroke.isNotEmpty()) {
                acceptedStrokes = acceptedStrokes + listOf(currentStroke)
            }
            val nextStroke = activeStrokeIndex + 1
            if (nextStroke >= guide.size) {
                finished = true
                hint = "Harika! Harfin bütün hareketlerini doğru sırayla tamamladın."
                onComplete()
            } else {
                activeStrokeIndex = nextStroke
                hint = "${nextStroke + 1}. hareketin büyük başlangıç noktasından başla."
            }
        } else {
            hint = "Hareket bitiş noktasına ulaşmadı. Büyük başlangıç noktasından tekrar dene."
        }

        activeProgressIndex = 0
        currentStroke = emptyList()
        currentStrokeAccepted = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LetterModelPreview(symbol = symbol)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(symbol, boardSize, activeStrokeIndex, finished) {
                    detectDragGestures(
                        onDragStart = { point ->
                            if (!finished && activeStrokeIndex in guide.indices) {
                                val active = guide[activeStrokeIndex]
                                if (active.isNotEmpty()) {
                                    val expectedStart = toCanvasPoint(active.first())
                                    if (distance(expectedStart, point) <= startTolerance()) {
                                        currentStrokeAccepted = true
                                        activeProgressIndex = 0
                                        currentStroke = listOf(point)
                                        hint = "${activeStrokeIndex + 1}. hareket: ok yönünde ilerle."
                                        markForwardProgress(point)
                                    } else {
                                        currentStrokeAccepted = false
                                        currentStroke = emptyList()
                                        hint = "Önce ${activeStrokeIndex + 1}. hareketin büyük noktasına dokun."
                                    }
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            if (currentStrokeAccepted) {
                                val point = change.position
                                currentStroke = currentStroke + point
                                markForwardProgress(point)
                            }
                            change.consume()
                        },
                        onDragEnd = { finishStroke() },
                        onDragCancel = { finishStroke() }
                    )
                }
        ) {
            val left = size.width * 0.18f
            val right = size.width * 0.82f
            val topY = size.height * 0.20f
            val middleY = size.height * 0.52f
            val baselineY = size.height * 0.84f

            drawLine(guideLineColor, Offset(left, topY), Offset(right, topY), 2.5f)
            drawLine(guideLineColor, Offset(left, baselineY), Offset(right, baselineY), 2.5f)
            repeat(12) { dash ->
                val segmentWidth = (right - left) / 12f
                val dashStart = left + dash * segmentWidth
                drawLine(
                    guideLineColor,
                    Offset(dashStart, middleY),
                    Offset(dashStart + segmentWidth * 0.55f, middleY),
                    2f
                )
            }

            guide.forEachIndexed { strokeIndex, stroke ->
                val isCompleted = strokeIndex < activeStrokeIndex || finished
                val isActive = strokeIndex == activeStrokeIndex && !finished
                val pointColor = when {
                    isCompleted -> completedGuideColor
                    isActive -> guideColor
                    else -> futureGuideColor
                }

                stroke.forEachIndexed { pointIndex, normalized ->
                    val progressed = isActive &&
                        currentStrokeAccepted &&
                        pointIndex <= activeProgressIndex
                    drawCircle(
                        color = if (progressed) visitedColor else pointColor,
                        radius = when {
                            pointIndex == 0 && isActive -> 10f
                            pointIndex == 0 -> 7f
                            pointIndex == stroke.lastIndex && isActive -> 7f
                            else -> 4.5f
                        },
                        center = Offset(normalized.x * size.width, normalized.y * size.height)
                    )
                }

                if (stroke.size >= 7) {
                    val fromN = stroke[2]
                    val toN = stroke[6]
                    val from = Offset(fromN.x * size.width, fromN.y * size.height)
                    val to = Offset(toN.x * size.width, toN.y * size.height)
                    val arrowColor = if (isActive) activeArrowColor else pointColor
                    drawLine(arrowColor, from, to, if (isActive) 4f else 2.5f, cap = StrokeCap.Round)

                    val angle = Math.atan2((to.y - from.y).toDouble(), (to.x - from.x).toDouble())
                    val head = if (isActive) 16f else 11f
                    val spread = Math.toRadians(28.0)
                    val p1 = Offset(
                        to.x - (kotlin.math.cos(angle - spread) * head).toFloat(),
                        to.y - (kotlin.math.sin(angle - spread) * head).toFloat()
                    )
                    val p2 = Offset(
                        to.x - (kotlin.math.cos(angle + spread) * head).toFloat(),
                        to.y - (kotlin.math.sin(angle + spread) * head).toFloat()
                    )
                    drawLine(arrowColor, to, p1, if (isActive) 4f else 2.5f, cap = StrokeCap.Round)
                    drawLine(arrowColor, to, p2, if (isActive) 4f else 2.5f, cap = StrokeCap.Round)
                }
            }

            (acceptedStrokes + if (currentStrokeAccepted) listOf(currentStroke) else emptyList()).forEach { childStroke ->
                childStroke.zipWithNext().forEach { pair ->
                    drawLine(
                        color = strokeColor,
                        start = pair.first,
                        end = pair.second,
                        strokeWidth = 13f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Text(
            if (finished) hint else "${activeStrokeIndex + 1}/${guide.size} hareket • $hint",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = {
                activeStrokeIndex = 0
                activeProgressIndex = 0
                acceptedStrokes = emptyList()
                currentStroke = emptyList()
                currentStrokeAccepted = false
                finished = false
                hint = "1. hareketin büyük başlangıç noktasından başla."
            }
        ) {
            Text("Tekrar Çiz")
        }
    }
}

@Composable
private fun LetterModelPreview(symbol: String) {
    val guide = remember(symbol) { letterGuide(symbol) }
    val color = MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Canvas(modifier = Modifier.size(width = 96.dp, height = 112.dp)) {
            guide.forEach { stroke ->
                stroke.zipWithNext().forEach { pair ->
                    drawLine(
                        color = color,
                        start = Offset(pair.first.x * size.width, pair.first.y * size.height),
                        end = Offset(pair.second.x * size.width, pair.second.y * size.height),
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        Text(
            text = "${letterUpper(symbol)} / $symbol",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun letterGuide(symbol: String): List<List<Offset>> {
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, count: Int = 24): List<Offset> =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
        }

    fun arc(
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        startDegrees: Float,
        endDegrees: Float,
        count: Int = 42
    ): List<Offset> =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            val degrees = startDegrees + (endDegrees - startDegrees) * t
            val radians = Math.toRadians(degrees.toDouble())
            Offset(
                x = cx + kotlin.math.cos(radians).toFloat() * rx,
                y = cy + kotlin.math.sin(radians).toFloat() * ry
            )
        }

    fun cubic(
        p0: Offset,
        p1: Offset,
        p2: Offset,
        p3: Offset,
        count: Int = 28
    ): List<Offset> =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            val u = 1f - t
            Offset(
                x = u * u * u * p0.x +
                    3f * u * u * t * p1.x +
                    3f * u * t * t * p2.x +
                    t * t * t * p3.x,
                y = u * u * u * p0.y +
                    3f * u * u * t * p1.y +
                    3f * u * t * t * p2.y +
                    t * t * t * p3.y
            )
        }

    fun join(vararg parts: List<Offset>): List<Offset> = buildList {
        parts.forEach { part ->
            if (part.isEmpty()) return@forEach
            if (isEmpty()) addAll(part) else addAll(part.drop(1))
        }
    }

    return when (symbol) {
        // MEB Şekil 2: dairesel gövde saat 2 civarından ters yönde, sonra sağ dik çizgi.
        "a" -> listOf(
            arc(0.46f, 0.61f, 0.18f, 0.17f, -30f, -390f, 46),
            line(0.64f, 0.43f, 0.64f, 0.78f, 28)
        )

        // Soldan sağa / yukarıdan aşağı: sol dik, ardından üstten sağ alta inen kavis.
        "n" -> listOf(
            line(0.31f, 0.42f, 0.31f, 0.78f, 28),
            cubic(
                Offset(0.31f, 0.51f),
                Offset(0.39f, 0.38f),
                Offset(0.67f, 0.40f),
                Offset(0.67f, 0.78f),
                38
            )
        )

        // Orta çizgiden sağa çıkıp saat yönünün tersine kıvrılan tek hareket.
        "e" -> listOf(
            join(
                line(0.31f, 0.60f, 0.66f, 0.60f, 18),
                cubic(
                    Offset(0.66f, 0.60f),
                    Offset(0.65f, 0.43f),
                    Offset(0.35f, 0.42f),
                    Offset(0.31f, 0.61f),
                    24
                ),
                cubic(
                    Offset(0.31f, 0.61f),
                    Offset(0.31f, 0.78f),
                    Offset(0.55f, 0.82f),
                    Offset(0.68f, 0.72f),
                    24
                )
            )
        )

        // Önce dik çizgi yukarıdan aşağı, sonra yatay çizgi soldan sağa.
        "t" -> listOf(
            line(0.50f, 0.29f, 0.50f, 0.79f, 36),
            line(0.35f, 0.48f, 0.65f, 0.48f, 22)
        )

        // Gövde ve nokta ayrı hareketlerdir.
        "i" -> listOf(
            line(0.50f, 0.44f, 0.50f, 0.79f, 28),
            line(0.50f, 0.30f, 0.50f, 0.31f, 4)
        )

        "l" -> listOf(
            line(0.50f, 0.26f, 0.50f, 0.79f, 38)
        )

        // Dairesel çizgi saat 2 yönünden başlayıp saat yönünün tersine kapanır.
        "o" -> listOf(
            arc(0.50f, 0.61f, 0.20f, 0.18f, -30f, -390f, 50)
        )

        // Önce sol dik çizgi; sonra üst sağdan birleşime ve alt sağa tek eğik hareket.
        "k" -> listOf(
            line(0.32f, 0.29f, 0.32f, 0.79f, 36),
            join(
                line(0.68f, 0.40f, 0.32f, 0.59f, 24),
                line(0.32f, 0.59f, 0.70f, 0.79f, 24)
            )
        )

        // Sol üstten aşağı, alt kavis ve sağ yukarı tek kesintisiz hareket.
        "u" -> listOf(
            join(
                line(0.31f, 0.43f, 0.31f, 0.64f, 18),
                arc(0.49f, 0.64f, 0.18f, 0.15f, 180f, 0f, 28),
                line(0.67f, 0.64f, 0.67f, 0.43f, 18)
            )
        )

        else -> listOf(line(0.25f, 0.55f, 0.75f, 0.55f))
    }
}

@Composable
private fun BuildTextGame(
    target: String,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    data class Tile(val id: Int, val char: Char)

    val tiles = remember(target) {
        target.toList()
            .mapIndexed { index, char -> Tile(index, char) }
            .sortedWith(compareByDescending<Tile> { it.char.code }.thenByDescending { it.id })
    }
    var usedIds by remember(target) { mutableStateOf(emptySet<Int>()) }
    var built by remember(target) { mutableStateOf("") }
    var retry by remember { mutableStateOf("") }
    var finished by remember(target) { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = if (built.isBlank()) "_".repeat(target.length) else built,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tiles.forEach { tile ->
                Button(
                    onClick = {
                        if (tile.id in usedIds || finished) return@Button
                        val expectedIndex = built.length
                        if (expectedIndex < target.length && tile.char == target[expectedIndex]) {
                            usedIds = usedIds + tile.id
                            built += tile.char
                            retry = ""
                            if (built.length == target.length) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            retry = "Bir daha bakalım."
                            onRetry()
                        }
                    },
                    enabled = tile.id !in usedIds
                ) {
                    Text(
                        text = tile.char.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        if (retry.isNotBlank()) Text(retry)
        Text("Harfleri doğru sırayla seç.")
    }
}

private fun nodeLetter(curriculumId: String): String =
    curriculumId.substringAfterLast("-").lowercase()

private fun letterUpper(letter: String): String =
    if (letter == "i") "İ" else letter.uppercase()

private fun activityOrder(id: String): Int =
    when {
        "-INTRO-" in id -> 1
        "-FIND-" in id -> 2
        "-SOUND-" in id -> 3
        "-TRACE-" in id -> 4
        "-SYLLABLE-" in id -> 5
        "-WORD-" in id -> 6
        else -> 99
    }
