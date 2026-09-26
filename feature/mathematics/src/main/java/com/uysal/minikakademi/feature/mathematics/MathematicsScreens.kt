package com.uysal.minikakademi.feature.mathematics

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uysal.minikakademi.core.audio.OfflineAudioPlayer
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.json.JSONObject

data class MathOption(
    val id: String,
    val label: String,
    val correct: Boolean
)

data class MathActivityConfig(
    val id: String,
    val curriculumId: String,
    val sourceType: String,
    val title: String,
    val activityType: String,
    val instruction: String,
    val instructionAudioId: String,
    val successAudioId: String,
    val retryAudioId: String,
    val successSfx: String,
    val retrySfx: String,
    val targetSymbol: String,
    val options: List<MathOption>
)

data class MathNode(
    val id: String,
    val title: String,
    val sequenceId: String,
    val sequenceTitle: String,
    val sequenceOrder: Int,
    val order: Int,
    val prerequisites: List<String>,
    val sourceType: String
)

private object MathematicsContentLoader {
    private val directories = listOf(
        "mathematics/spatial",
        "mathematics/numbers",
        "mathematics/measurement",
        "mathematics/extensions"
    )

    fun loadActivities(context: Context): List<MathActivityConfig> =
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

    fun loadNodes(context: Context, activities: List<MathActivityConfig>): List<MathNode> {
        val activityNodes = activities.map { it.curriculumId }.toSet()
        val text = context.assets.open("curriculum_manifest.json")
            .bufferedReader()
            .use { it.readText() }
        val root = JSONObject(text)
        val sequences = root.getJSONArray("sequences")
        val result = mutableListOf<MathNode>()

        for (sequenceIndex in 0 until sequences.length()) {
            val sequence = sequences.getJSONObject(sequenceIndex)
            if (sequence.getString("domain") != "MATHEMATICS") continue
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
                    MathNode(
                        id = id,
                        title = node.getString("title"),
                        sequenceId = sequence.getString("id"),
                        sequenceTitle = sequence.getString("title"),
                        sequenceOrder = sequenceIndex,
                        order = node.getInt("order"),
                        prerequisites = prerequisites,
                        sourceType = node.getString("sourceType")
                    )
                )
            }
        }

        return result.sortedWith(compareBy<MathNode> { it.sequenceOrder }.thenBy { it.order })
    }

    private fun parseActivity(json: JSONObject): MathActivityConfig {
        val interaction = json.getJSONObject("interaction")
        val options = buildList {
            val array = interaction.optJSONArray("options")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(
                        MathOption(
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
        return MathActivityConfig(
            id = json.getString("id"),
            curriculumId = json.getString("curriculumId"),
            sourceType = json.getString("sourceType"),
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

private val mathCategories = listOf(
    "SEQ-MAT-SPATIAL" to "Yer ve Yön",
    "SEQ-MAT-NUMBERS" to "Sayılar ve Nicelikler",
    "SEQ-MAT-MEASURE" to "Ölçme ve Tartma",
    "SEQ-EXTENSIONS" to "Toplama • Çıkarma • Çarpma"
)

@Composable
fun MathematicsHomeScreen(
    avatarId: String,
    completedActivityIds: Set<String>,
    onOpenCategory: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activities by remember { mutableStateOf(emptyList<MathActivityConfig>()) }
    var nodes by remember { mutableStateOf(emptyList<MathNode>()) }

    LaunchedEffect(Unit) {
        val loaded = MathematicsContentLoader.loadActivities(context)
        activities = loaded
        nodes = MathematicsContentLoader.loadNodes(context, loaded)
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
                    Text("Matematik Öğreniyorum", style = MaterialTheme.typography.headlineMedium)
                    Text("Nesnelerle düşün, say, karşılaştır ve çöz.")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mathCategories) { (sequenceId, label) ->
                    val categoryNodes = nodes.filter { it.sequenceId == sequenceId }
                    val categoryActivities = activities.filter { a ->
                        categoryNodes.any { it.id == a.curriculumId }
                    }
                    val completed = categoryActivities.count { it.id in completedActivityIds }
                    val total = categoryActivities.size
                    Card(
                        onClick = { onOpenCategory(sequenceId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            if (sequenceId == "SEQ-EXTENSIONS") {
                                Text("Kaynak dışı, kilitli genişletme bölümü")
                            } else {
                                Text("Kaynak-temelli matematik çalışmaları")
                            }
                            Text("Tamamlanan: $completed / $total")
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Ana Sayfa", onClick = onBack)
        }
    }
}

@Composable
fun MathematicsCategoryScreen(
    sequenceId: String,
    avatarId: String,
    completedActivityIds: Set<String>,
    onOpenActivity: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activities = remember(sequenceId) { MathematicsContentLoader.loadActivities(context) }
    val nodes = remember(sequenceId, activities) {
        MathematicsContentLoader.loadNodes(context, activities)
            .filter { it.sequenceId == sequenceId }
    }
    val title = mathCategories.firstOrNull { it.first == sequenceId }?.second ?: "Matematik"

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
                Column {
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    if (sequenceId == "SEQ-EXTENSIONS") {
                        Text("Bu bölüm mevcut PDF'lerden türetilmemiştir.")
                    } else {
                        Text("Etkinlikleri sırayla tamamla.")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(nodes) { node ->
                    val activity = activities.firstOrNull { it.curriculumId == node.id }
                    if (activity != null) {
                        val complete = activity.id in completedActivityIds
                        val unlocked = node.prerequisites.all { prerequisite ->
                            val prerequisiteActivities = activities.filter { it.curriculumId == prerequisite }
                            prerequisiteActivities.isEmpty() ||
                                prerequisiteActivities.all { it.id in completedActivityIds }
                        }
                        Card(
                            onClick = { if (unlocked) onOpenActivity(activity.id) },
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
                            Column(
                                modifier = Modifier.padding(15.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(node.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    when {
                                        complete -> "✓ Tamamlandı"
                                        unlocked -> activity.instruction
                                        else -> "Önce önceki matematik çalışmasını tamamla."
                                    }
                                )
                                Text(
                                    if (activity.sourceType == "EXTENSION") "Genişletme"
                                    else "Kaynak-temelli"
                                )
                            }
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Matematik Menüsüne Dön", onClick = onBack)
        }
    }
}

@Composable
fun MathematicsActivityScreen(
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
        MathematicsContentLoader.loadActivities(context).firstOrNull { it.id == activityId }
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
                    Text(config.instruction)
                    if (config.sourceType == "EXTENSION") {
                        Text("Genişletme etkinliği", fontWeight = FontWeight.Bold)
                    }
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
                    "TRACE_NUMBER" -> DigitTraceGame(
                        digit = config.targetSymbol,
                        onComplete = ::completeOnce
                    )
                    "COUNT_OBJECTS" -> CountObjectsGame(
                        count = config.targetSymbol.toIntOrNull() ?: 0,
                        options = config.options,
                        onRetry = ::playRetryAudio,
                        onComplete = ::completeOnce
                    )
                    "NUMBER_INTRO" -> NumberIntroGame(
                        target = config.targetSymbol,
                        onComplete = ::completeOnce
                    )
                    "RHYTHMIC_COUNT", "PATTERN_COMPLETE", "BUILD_SEQUENCE" ->
                        SequenceChoiceGame(
                            target = config.targetSymbol,
                            options = config.options,
                            onRetry = ::playRetryAudio,
                            onComplete = ::completeOnce
                        )
                    "ADD_OBJECTS", "SUBTRACT_OBJECTS", "GROUP_OBJECTS" ->
                        ArithmeticGame(
                            expression = config.targetSymbol,
                            options = config.options,
                            onRetry = ::playRetryAudio,
                            onComplete = ::completeOnce
                        )
                    else -> ChoiceGame(
                        target = config.targetSymbol,
                        options = config.options,
                        onRetry = ::playRetryAudio,
                        onComplete = ::completeOnce
                    )
                }
            }

            if (completed) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "Harika! Matematik çalışmasını tamamladın.",
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
private fun ChoiceGame(
    target: String,
    options: List<MathOption>,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var finished by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (target.isNotBlank()) {
            Text(
                target,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { option ->
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

@Composable
private fun NumberIntroGame(
    target: String,
    onComplete: () -> Unit
) {
    val number = target.toIntOrNull()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            target,
            fontSize = 140.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (number != null && number in 1..20) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                repeat(number) {
                    Text("●", fontSize = 26.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        KidPrimaryButton(text = "Anladım", onClick = onComplete)
    }
}

@Composable
private fun CountObjectsGame(
    count: Int,
    options: List<MathOption>,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(count.coerceIn(0, 30)) {
                Text("●", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
        ChoiceGame(target = "Kaç tane?", options = options, onRetry = onRetry, onComplete = onComplete)
    }
}

@Composable
private fun SequenceChoiceGame(
    target: String,
    options: List<MathOption>,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            target,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        ChoiceGame(target = "Boşluğa hangisi gelir?", options = options, onRetry = onRetry, onComplete = onComplete)
    }
}

@Composable
private fun ArithmeticGame(
    expression: String,
    options: List<MathOption>,
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val plus = Regex("""(\d+)\s*\+\s*(\d+)""").find(expression)
    val minus = Regex("""(\d+)\s*-\s*(\d+)""").find(expression)
    val groups = Regex("""(\d+)\s*[x×]\s*(\d+)""").find(expression)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            expression,
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        when {
            plus != null -> {
                val a = plus.groupValues[1].toInt()
                val b = plus.groupValues[2].toInt()
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    DotGroup(a)
                    Text("+", fontSize = 36.sp)
                    DotGroup(b)
                }
            }
            minus != null -> {
                val a = minus.groupValues[1].toInt()
                val b = minus.groupValues[2].toInt()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DotGroup(a)
                    Text("$b nesne ayrılıyor", style = MaterialTheme.typography.titleMedium)
                }
            }
            groups != null -> {
                val groupCount = groups.groupValues[1].toInt()
                val perGroup = groups.groupValues[2].toInt()
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(groupCount.coerceAtMost(5)) {
                        Card {
                            Box(modifier = Modifier.padding(10.dp)) {
                                DotGroup(perGroup)
                            }
                        }
                    }
                }
            }
        }

        ChoiceGame(target = "Sonuç kaç?", options = options, onRetry = onRetry, onComplete = onComplete)
    }
}

@Composable
private fun DotGroup(count: Int) {
    FlowRow(
        modifier = Modifier.size(width = 110.dp, height = 90.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(count.coerceIn(0, 12)) {
            Text("●", fontSize = 24.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun DigitTraceGame(
    digit: String,
    onComplete: () -> Unit
) {
    val guide = remember(digit) { digitGuide(digit) }
    var visited by remember(digit) { mutableStateOf(emptySet<Int>()) }
    var strokes by remember(digit) { mutableStateOf(emptyList<List<Offset>>()) }
    var currentStroke by remember(digit) { mutableStateOf(emptyList<Offset>()) }
    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    var finished by remember(digit) { mutableStateOf(false) }

    val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    val strokeColor = MaterialTheme.colorScheme.primary
    val visitedColor = MaterialTheme.colorScheme.secondary

    fun markNearby(point: Offset) {
        if (boardSize.width <= 0 || boardSize.height <= 0) return
        val tolerance = (boardSize.width.coerceAtMost(boardSize.height) * 0.075f)
            .coerceIn(20f, 50f)
        val additions = buildSet {
            guide.forEachIndexed { index, normalized ->
                val gp = Offset(normalized.x * boardSize.width, normalized.y * boardSize.height)
                val dx = gp.x - point.x
                val dy = gp.y - point.y
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
        if (!finished && ratio >= 0.68f) {
            finished = true
            onComplete()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            digit,
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(digit, boardSize) {
                    detectDragGestures(
                        onDragStart = { p ->
                            currentStroke = listOf(p)
                            markNearby(p)
                        },
                        onDrag = { change, _ ->
                            val p = change.position
                            currentStroke = currentStroke + p
                            markNearby(p)
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
        Text("Büyük noktadan başla ve rakam yolunu takip et.")
        Button(
            onClick = {
                visited = emptySet()
                strokes = emptyList()
                currentStroke = emptyList()
                finished = false
            }
        ) { Text("Tekrar Çiz") }
    }
}

private fun digitGuide(digit: String): List<Offset> {
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, count: Int = 28): List<Offset> =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
        }

    fun arc(cx: Float, cy: Float, rx: Float, ry: Float, start: Float, end: Float, count: Int = 42): List<Offset> =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            val angle = Math.toRadians((start + (end - start) * t).toDouble())
            Offset(
                cx + cos(angle).toFloat() * rx,
                cy + sin(angle).toFloat() * ry
            )
        }

    return when (digit) {
        "0" -> arc(0.50f, 0.53f, 0.22f, 0.34f, -90f, 270f, 58)
        "1" -> line(0.40f, 0.38f, 0.52f, 0.25f, 12) + line(0.52f, 0.25f, 0.52f, 0.82f, 38)
        "2" -> arc(0.48f, 0.38f, 0.23f, 0.17f, 200f, 20f, 30) + line(0.69f, 0.42f, 0.30f, 0.80f, 30) + line(0.30f, 0.80f, 0.70f, 0.80f, 22)
        "3" -> arc(0.45f, 0.39f, 0.22f, 0.17f, 230f, 80f, 28) + arc(0.45f, 0.67f, 0.22f, 0.17f, -80f, 130f, 30)
        "4" -> line(0.62f, 0.24f, 0.30f, 0.62f, 28) + line(0.30f, 0.62f, 0.70f, 0.62f, 22) + line(0.62f, 0.24f, 0.62f, 0.82f, 34)
        "5" -> line(0.68f, 0.26f, 0.34f, 0.26f, 20) + line(0.34f, 0.26f, 0.34f, 0.52f, 18) + arc(0.47f, 0.65f, 0.21f, 0.17f, 220f, -80f, 32)
        "6" -> arc(0.51f, 0.58f, 0.22f, 0.25f, -40f, 300f, 48) + line(0.34f, 0.48f, 0.55f, 0.25f, 18)
        "7" -> line(0.30f, 0.26f, 0.70f, 0.26f, 24) + line(0.70f, 0.26f, 0.43f, 0.82f, 34)
        "8" -> arc(0.50f, 0.39f, 0.18f, 0.15f, -90f, 270f, 34) + arc(0.50f, 0.67f, 0.21f, 0.18f, -90f, 270f, 38)
        "9" -> arc(0.49f, 0.40f, 0.21f, 0.18f, -90f, 270f, 38) + line(0.68f, 0.40f, 0.46f, 0.82f, 28)
        else -> line(0.30f, 0.50f, 0.70f, 0.50f)
    }
}
