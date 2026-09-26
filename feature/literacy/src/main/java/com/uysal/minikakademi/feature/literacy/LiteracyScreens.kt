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
import kotlin.math.sqrt
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

    LaunchedEffect(config.id, narrationEnabled, speechRate) {
        if (narrationEnabled) {
            audioPlayer.playSpeech(config.instructionAudioId, speechRate)
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
                        onClick = { audioPlayer.playSpeech(config.instructionAudioId, speechRate) }
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
                        onComplete = ::completeOnce
                    )
                    "FIND_LETTER", "FIND_SOUND_OBJECT" -> MultiSelectGame(
                        options = config.options,
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
        KidPrimaryButton(text = "Hazırım", onClick = onComplete)
    }
}

@Composable
private fun MultiSelectGame(
    options: List<LiteracyOption>,
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
                    modifier = Modifier.size(width = 118.dp, height = 78.dp),
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
                        Text(
                            text = if (isSelected) "✓ " + option.label else option.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
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
    var visited by remember(symbol) { mutableStateOf(emptySet<Int>()) }
    var strokes by remember(symbol) { mutableStateOf(emptyList<List<Offset>>()) }
    var currentStroke by remember(symbol) { mutableStateOf(emptyList<Offset>()) }
    var boardSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var finished by remember(symbol) { mutableStateOf(false) }

    val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    val strokeColor = MaterialTheme.colorScheme.primary
    val visitedColor = MaterialTheme.colorScheme.secondary

    fun markNearby(point: Offset) {
        if (boardSize.width <= 0 || boardSize.height <= 0) return
        val tolerance = (boardSize.width.coerceAtMost(boardSize.height) * 0.07f)
            .coerceIn(20f, 48f)
        val additions = buildSet {
            guide.forEachIndexed { index, normalized ->
                val guidePoint = Offset(
                    normalized.x * boardSize.width,
                    normalized.y * boardSize.height
                )
                val dx = guidePoint.x - point.x
                val dy = guidePoint.y - point.y
                if (sqrt(dx * dx + dy * dy) <= tolerance) add(index)
            }
        }
        if (additions.isNotEmpty()) visited = visited + additions
    }

    fun finishStroke() {
        if (currentStroke.isNotEmpty()) {
            strokes = strokes + listOf(currentStroke)
            currentStroke = emptyList()
        }
        val ratio = if (guide.isEmpty()) 0f else visited.size.toFloat() / guide.size.toFloat()
        if (!finished && ratio >= 0.70f) {
            finished = true
            onComplete()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = letterUpper(symbol) + "  " + symbol,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(symbol, boardSize) {
                    detectDragGestures(
                        onDragStart = { startPoint ->
                            currentStroke = listOf(startPoint)
                            markNearby(startPoint)
                        },
                        onDrag = { change, _ ->
                            val point = change.position
                            currentStroke = currentStroke + point
                            markNearby(point)
                            change.consume()
                        },
                        onDragEnd = { finishStroke() },
                        onDragCancel = { finishStroke() }
                    )
                }
        ) {
            guide.forEachIndexed { index, normalized ->
                drawCircle(
                    color = if (index in visited) visitedColor else guideColor,
                    radius = if (index == 0) 9f else 5f,
                    center = Offset(normalized.x * size.width, normalized.y * size.height)
                )
            }

            (strokes + listOf(currentStroke)).forEach { stroke ->
                stroke.zipWithNext().forEach { pair ->
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

        Text("Büyük başlangıç noktasından başla ve noktalı yolu takip et.")
        Button(
            onClick = {
                visited = emptySet()
                strokes = emptyList()
                currentStroke = emptyList()
                finished = false
            }
        ) {
            Text("Tekrar Çiz")
        }
    }
}

private fun letterGuide(symbol: String): List<Offset> {
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

    return when (symbol) {
        "a" -> arc(0.45f, 0.57f, 0.20f, 0.20f, -70f, 290f) +
            line(0.64f, 0.38f, 0.64f, 0.78f)
        "n" -> line(0.30f, 0.78f, 0.30f, 0.38f) +
            arc(0.49f, 0.58f, 0.19f, 0.20f, 180f, 360f) +
            line(0.68f, 0.58f, 0.68f, 0.78f)
        "e" -> line(0.30f, 0.57f, 0.68f, 0.57f, 18) +
            arc(0.50f, 0.57f, 0.20f, 0.20f, 15f, 330f, 38)
        "t" -> line(0.50f, 0.28f, 0.50f, 0.78f, 34) +
            line(0.34f, 0.46f, 0.66f, 0.46f, 20)
        "i" -> line(0.50f, 0.43f, 0.50f, 0.78f, 28) +
            listOf(Offset(0.50f, 0.30f))
        "l" -> line(0.50f, 0.25f, 0.50f, 0.78f, 38)
        "o" -> arc(0.50f, 0.57f, 0.22f, 0.22f, -90f, 270f, 54)
        "k" -> line(0.32f, 0.28f, 0.32f, 0.78f, 36) +
            line(0.68f, 0.38f, 0.32f, 0.58f, 24) +
            line(0.32f, 0.58f, 0.70f, 0.78f, 24)
        "u" -> line(0.30f, 0.38f, 0.30f, 0.62f, 18) +
            arc(0.49f, 0.62f, 0.19f, 0.16f, 180f, 0f, 28) +
            line(0.68f, 0.62f, 0.68f, 0.38f, 18)
        else -> line(0.25f, 0.55f, 0.75f, 0.55f)
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
