package com.uysal.minikakademi.feature.tracing

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.json.JSONObject

data class TracingActivityConfig(
    val id: String,
    val title: String,
    val activityType: String,
    val instruction: String,
    val targetSymbol: String
)

private object TracingContentLoader {
    private const val DIRECTORY = "literacy/preparation"

    fun loadAll(context: Context): List<TracingActivityConfig> =
        context.assets.list(DIRECTORY)
            .orEmpty()
            .filter { it.endsWith(".json") }
            .sorted()
            .mapNotNull { fileName ->
                runCatching {
                    val text = context.assets.open("$DIRECTORY/$fileName")
                        .bufferedReader()
                        .use { it.readText() }
                    parse(JSONObject(text))
                }.getOrNull()
            }

    private fun parse(json: JSONObject): TracingActivityConfig {
        val instruction = json.getJSONObject("instruction").getString("text")
        val target = json.getJSONObject("learningTarget").optString("targetSymbol", "WAVE_PATH")
        return TracingActivityConfig(
            id = json.getString("id"),
            title = json.getString("title"),
            activityType = json.getString("activityType"),
            instruction = instruction,
            targetSymbol = target
        )
    }
}

@Composable
fun TracingHomeScreen(
    onOpenActivity: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activities by remember { mutableStateOf(emptyList<TracingActivityConfig>()) }

    LaunchedEffect(Unit) {
        activities = TracingContentLoader.loadAll(context)
    }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Çiziyorum", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Parmağınla çizgileri takip et. Acele etmene gerek yok.",
                style = MaterialTheme.typography.bodyLarge
            )

            if (activities.isEmpty()) {
                KidCard {
                    Text("Çizgi etkinlikleri hazırlanıyor.")
                }
            } else {
                activities.forEachIndexed { index, activity ->
                    Card(
                        onClick = { onOpenActivity(activity.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = (index + 1).toString() + ". " + activity.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(activity.instruction)
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Ana Sayfa", onClick = onBack)
        }
    }
}

@Composable
fun TracingActivityScreen(
    activityId: String,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val config = remember(activityId) {
        TracingContentLoader.loadAll(context).firstOrNull { it.id == activityId }
    }

    if (config == null) {
        KidScreen {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Etkinlik bulunamadı.", style = MaterialTheme.typography.headlineMedium)
                KidPrimaryButton(text = "Geri Dön", onClick = onBack)
            }
        }
        return
    }

    var completed by remember(activityId) { mutableStateOf(false) }

    KidScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(config.title, style = MaterialTheme.typography.headlineMedium)
            Text(config.instruction, style = MaterialTheme.typography.bodyLarge)

            if (config.activityType == "FIND_DIFFERENCE") {
                FindDifferenceGame(
                    modifier = Modifier.weight(1f),
                    onComplete = {
                        if (!completed) {
                            completed = true
                            onComplete()
                        }
                    }
                )
            } else {
                TraceBoard(
                    targetSymbol = config.targetSymbol,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onComplete = {
                        if (!completed) {
                            completed = true
                            onComplete()
                        }
                    }
                )
            }

            if (completed) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Harika! Çalışmayı tamamladın.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            KidPrimaryButton(text = "Etkinlik Listesi", onClick = onBack)
        }
    }
}

@Composable
private fun TraceBoard(
    targetSymbol: String,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit
) {
    val normalizedGuide = remember(targetSymbol) { createGuide(targetSymbol) }
    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    var visited by remember(targetSymbol) { mutableStateOf(emptySet<Int>()) }
    var stroke by remember(targetSymbol) { mutableStateOf(emptyList<Offset>()) }
    var completed by remember(targetSymbol) { mutableStateOf(false) }

    fun markNearby(point: Offset) {
        if (boardSize.width <= 0 || boardSize.height <= 0) return
        val tolerance = (boardSize.width.coerceAtMost(boardSize.height) * 0.075f)
            .coerceIn(22f, 52f)
        val additions = buildSet {
            normalizedGuide.forEachIndexed { index, n ->
                val guidePoint = Offset(
                    x = n.x * boardSize.width,
                    y = n.y * boardSize.height
                )
                val dx = guidePoint.x - point.x
                val dy = guidePoint.y - point.y
                if (sqrt(dx * dx + dy * dy) <= tolerance) add(index)
            }
        }
        if (additions.isNotEmpty()) {
            visited = visited + additions
        }
    }

    fun finishStroke() {
        val ratio = if (normalizedGuide.isEmpty()) 0f
        else visited.size.toFloat() / normalizedGuide.size.toFloat()

        if (!completed && ratio >= 0.72f) {
            completed = true
            onComplete()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(targetSymbol, boardSize) {
                    detectDragGestures(
                        onDragStart = { point ->
                            stroke = listOf(point)
                            markNearby(point)
                        },
                        onDrag = { change, _ ->
                            val point = change.position
                            stroke = stroke + point
                            markNearby(point)
                            change.consume()
                        },
                        onDragEnd = { finishStroke() },
                        onDragCancel = { finishStroke() }
                    )
                }
        ) {
            val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            val childColor = MaterialTheme.colorScheme.primary
            val successColor = MaterialTheme.colorScheme.secondary

            normalizedGuide.forEachIndexed { index, n ->
                val point = Offset(n.x * size.width, n.y * size.height)
                drawCircle(
                    color = if (index in visited) successColor else guideColor,
                    radius = if (index % 4 == 0) 6.5f else 4.5f,
                    center = point
                )
            }

            if (normalizedGuide.isNotEmpty()) {
                val start = normalizedGuide.first()
                val end = normalizedGuide.last()
                drawCircle(
                    color = childColor,
                    radius = 13f,
                    center = Offset(start.x * size.width, start.y * size.height)
                )
                drawCircle(
                    color = successColor,
                    radius = 13f,
                    center = Offset(end.x * size.width, end.y * size.height)
                )
            }

            stroke.zipWithNext().forEach { pair ->
                drawLine(
                    color = childColor,
                    start = pair.first,
                    end = pair.second,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun createGuide(symbol: String): List<Offset> =
    when (symbol) {
        "SPIRAL" -> {
            val count = 90
            List(count) { i ->
                val t = i.toFloat() / (count - 1)
                val angle = t * 3.8f * 2f * PI.toFloat()
                val radius = 0.04f + t * 0.34f
                Offset(
                    x = 0.5f + cos(angle) * radius,
                    y = 0.5f + sin(angle) * radius
                )
            }
        }

        "ZIGZAG" -> {
            val count = 70
            List(count) { i ->
                val t = i.toFloat() / (count - 1)
                val section = (t * 6f).toInt()
                val local = (t * 6f) - section
                val fromHigh = section % 2 == 0
                val y0 = if (fromHigh) 0.30f else 0.70f
                val y1 = if (fromHigh) 0.70f else 0.30f
                Offset(
                    x = 0.08f + t * 0.84f,
                    y = y0 + (y1 - y0) * local
                )
            }
        }

        "CURVE_COPY" -> {
            val count = 75
            List(count) { i ->
                val t = i.toFloat() / (count - 1)
                Offset(
                    x = 0.08f + t * 0.84f,
                    y = 0.52f + sin(t * 3f * PI.toFloat()) * 0.16f
                )
            }
        }

        else -> {
            val count = 75
            List(count) { i ->
                val t = i.toFloat() / (count - 1)
                Offset(
                    x = 0.08f + t * 0.84f,
                    y = 0.50f + sin(t * 4f * PI.toFloat()) * 0.13f
                )
            }
        }
    }

@Composable
private fun FindDifferenceGame(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit
) {
    val left = listOf("●", "▲", "■", "★", "◆", "●", "▲", "■")
    val right = listOf("○", "▲", "□", "☆", "◆", "○", "▲", "□")
    val correct = setOf(0, 2, 3, 5, 7)
    var found by remember { mutableStateOf(emptySet<Int>()) }
    var finished by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "İki kutuya bak. Farklı olan 5 şekli sağ tarafta bul.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Bulunan: " + found.size + " / 5",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DifferencePanel(
                symbols = left,
                selected = emptySet(),
                clickable = false,
                onSelect = {}
            )
            DifferencePanel(
                symbols = right,
                selected = found,
                clickable = true,
                onSelect = { index ->
                    if (index in correct) {
                        found = found + index
                        if (!finished && found.size == correct.size) {
                            finished = true
                            onComplete()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DifferencePanel(
    symbols: List<String>,
    selected: Set<Int>,
    clickable: Boolean,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier.width(145.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        symbols.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, symbol ->
                    val index = rowIndex * 2 + columnIndex
                    Card(
                        onClick = { if (clickable) onSelect(index) },
                        enabled = clickable,
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (index in selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (index in selected) "✓" else symbol,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
