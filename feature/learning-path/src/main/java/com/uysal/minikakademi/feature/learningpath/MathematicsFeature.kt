package com.uysal.minikakademi.feature.learningpath

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val mathCategoryLabels = listOf(
    "SEQ-MAT-SPATIAL" to "Yer ve Yön",
    "SEQ-MAT-NUMBERS" to "Sayılar ve Nicelikler",
    "SEQ-MAT-MEASURE" to "Ölçme ve Tartma",
    "SEQ-EXTENSIONS" to "Toplama • Çıkarma • Çarpma"
)

@Composable
fun MathematicsHomeScreen(
    avatarId: String,
    onOpenCategory: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lessons = remember { MathContent.load(context) }
    val completed = remember { MathProgressStore.load(context) }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
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
                items(mathCategoryLabels) { item ->
                    val sequenceId = item.first
                    val categoryLessons = lessons.filter { it.sequenceId == sequenceId }
                    val done = categoryLessons.count { it.id in completed }

                    Card(
                        onClick = { onOpenCategory(sequenceId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(item.second, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                if (sequenceId == "SEQ-EXTENSIONS")
                                    "Kaynak dışı genişletme bölümü"
                                else
                                    "Kaynak-temelli matematik çalışmaları"
                            )
                            Text("Tamamlanan: $done / ${categoryLessons.size}")
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
    onOpenActivity: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lessons = remember(sequenceId) { MathContent.load(context) }
    val completed = remember { MathProgressStore.load(context) }
    val visible = lessons.filter { it.sequenceId == sequenceId }
    val title = mathCategoryLabels.firstOrNull { it.first == sequenceId }?.second ?: "Matematik"

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(avatarId = avatarId, size = 58.dp)
                Column {
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        if (sequenceId == "SEQ-EXTENSIONS")
                            "Bu bölüm mevcut PDF'lerin bağımsız öğretim dizisi değildir."
                        else
                            "Etkinlikleri sırayla tamamla."
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(visible) { lesson ->
                    val unlocked = lesson.prerequisites.all { prerequisite ->
                        val previous = lessons.firstOrNull { it.curriculumId == prerequisite }
                        previous == null || previous.id in completed
                    }
                    val done = lesson.id in completed

                    Card(
                        onClick = { if (unlocked) onOpenActivity(lesson.id) },
                        enabled = unlocked,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                done -> MaterialTheme.colorScheme.primaryContainer
                                unlocked -> MaterialTheme.colorScheme.surface
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                when {
                                    done -> "✓ Tamamlandı"
                                    unlocked -> lesson.prompt
                                    else -> "Önce önceki matematik çalışmasını tamamla."
                                }
                            )
                            Text(if (lesson.sourceType == "EXTENSION") "Genişletme" else "Kaynak-temelli")
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
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lesson = remember(activityId) {
        MathContent.load(context).firstOrNull { it.id == activityId }
    }
    var completed by remember(activityId) { mutableStateOf(false) }

    if (lesson == null) {
        KidScreen {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Etkinlik bulunamadı.")
                KidPrimaryButton(text = "Geri Dön", onClick = onBack)
            }
        }
        return
    }

    fun complete() {
        if (!completed) {
            MathProgressStore.complete(context, activityId)
            completed = true
        }
    }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(avatarId = avatarId, size = 56.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(lesson.title, style = MaterialTheme.typography.headlineMedium)
                    Text(lesson.prompt)
                    if (lesson.sourceType == "EXTENSION") {
                        Text("Genişletme etkinliği", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (lesson.activityType) {
                    "TRACE_NUMBER" -> DigitTraceGame(lesson.target, ::complete)
                    "COUNT" -> CountGame(lesson, ::complete)
                    "INTRO" -> IntroGame(lesson.target, ::complete)
                    "ADD", "SUB", "GROUP" -> ArithmeticGame(lesson, ::complete)
                    else -> ChoiceGame(lesson, ::complete)
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
    lesson: MathLesson,
    onComplete: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var finished by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        if (lesson.target.isNotBlank()) {
            Text(
                lesson.target,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            lesson.options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        if (index == lesson.correctIndex) {
                            message = "Doğru!"
                            if (!finished) {
                                finished = true
                                onComplete()
                            }
                        } else {
                            message = "Bir daha bakalım."
                        }
                    }
                ) {
                    Text(option, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        if (message.isNotBlank()) Text(message)
    }
}

@Composable
private fun IntroGame(target: String, onComplete: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(target, fontSize = 120.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        KidPrimaryButton(text = "Anladım", onClick = onComplete)
    }
}

@Composable
private fun CountGame(lesson: MathLesson, onComplete: () -> Unit) {
    val count = lesson.target.toIntOrNull() ?: 0
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            repeat(count.coerceIn(0, 30)) {
                Text("●", fontSize = 30.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
        ChoiceGame(lesson, onComplete)
    }
}

@Composable
private fun ArithmeticGame(lesson: MathLesson, onComplete: () -> Unit) {
    val values = Regex("""\d+""").findAll(lesson.target).map { it.value.toInt() }.toList()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            lesson.target,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        when (lesson.activityType) {
            "ADD" -> if (values.size >= 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    DotGroup(values[0])
                    Text("+", fontSize = 36.sp)
                    DotGroup(values[1])
                }
            }
            "SUB" -> if (values.size >= 2) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DotGroup(values[0])
                    Text("${values[1]} nesne ayrılıyor")
                }
            }
            "GROUP" -> if (values.size >= 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(values[0].coerceAtMost(5)) {
                        Card {
                            Box(modifier = Modifier.padding(8.dp)) {
                                DotGroup(values[1])
                            }
                        }
                    }
                }
            }
        }

        ChoiceGame(lesson, onComplete)
    }
}

@Composable
private fun DotGroup(count: Int) {
    FlowRow(
        modifier = Modifier.size(width = 105.dp, height = 85.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(count.coerceIn(0, 12)) {
            Text("●", fontSize = 22.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun DigitTraceGame(digit: String, onComplete: () -> Unit) {
    val guide = remember(digit) { digitGuide(digit) }
    var visited by remember(digit) { mutableStateOf(emptySet<Int>()) }
    var strokes by remember(digit) { mutableStateOf(emptyList<List<Offset>>()) }
    var current by remember(digit) { mutableStateOf(emptyList<Offset>()) }
    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    var finished by remember(digit) { mutableStateOf(false) }

    val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    val strokeColor = MaterialTheme.colorScheme.primary
    val successColor = MaterialTheme.colorScheme.secondary

    fun mark(point: Offset) {
        if (boardSize.width <= 0 || boardSize.height <= 0) return
        val tolerance = (boardSize.width.coerceAtMost(boardSize.height) * 0.075f).coerceIn(20f, 50f)
        val found = buildSet {
            guide.forEachIndexed { index, p ->
                val target = Offset(p.x * boardSize.width, p.y * boardSize.height)
                val dx = target.x - point.x
                val dy = target.y - point.y
                if (sqrt(dx * dx + dy * dy) <= tolerance) add(index)
            }
        }
        visited = visited + found
    }

    fun finish() {
        if (current.isNotEmpty()) {
            strokes = strokes + listOf(current)
            current = emptyList()
        }
        val ratio = if (guide.isEmpty()) 0f else visited.size.toFloat() / guide.size.toFloat()
        if (!finished && ratio >= 0.68f) {
            finished = true
            onComplete()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(digit, fontSize = 66.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(digit, boardSize) {
                    detectDragGestures(
                        onDragStart = { p -> current = listOf(p); mark(p) },
                        onDrag = { change, _ ->
                            current = current + change.position
                            mark(change.position)
                            change.consume()
                        },
                        onDragEnd = { finish() },
                        onDragCancel = { finish() }
                    )
                }
        ) {
            guide.forEachIndexed { index, p ->
                drawCircle(
                    color = if (index in visited) successColor else guideColor,
                    radius = if (index == 0) 9f else 5f,
                    center = Offset(p.x * size.width, p.y * size.height)
                )
            }
            (strokes + listOf(current)).forEach { stroke ->
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
        Text("Büyük noktadan başla ve noktalı yolu takip et.")
    }
}

private fun digitGuide(digit: String): List<Offset> {
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, count: Int = 28) =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
        }

    fun arc(cx: Float, cy: Float, rx: Float, ry: Float, start: Float, end: Float, count: Int = 42) =
        List(count) { i ->
            val t = i.toFloat() / (count - 1)
            val angle = Math.toRadians((start + (end - start) * t).toDouble())
            Offset(cx + cos(angle).toFloat() * rx, cy + sin(angle).toFloat() * ry)
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
