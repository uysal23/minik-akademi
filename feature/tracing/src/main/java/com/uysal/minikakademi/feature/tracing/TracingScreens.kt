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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.uysal.minikakademi.core.audio.OfflineAudioPlayer
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import org.json.JSONObject

data class TracingActivityConfig(
    val id: String,
    val title: String,
    val activityType: String,
    val instruction: String,
    val instructionAudioId: String,
    val successAudioId: String,
    val retryAudioId: String,
    val successSfx: String,
    val retrySfx: String,
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
        val instructionObject = json.getJSONObject("instruction")
        val feedback = json.getJSONObject("feedback")
        val instruction = instructionObject.getString("text")
        val target = json.getJSONObject("learningTarget").optString("targetSymbol", "WAVE_PATH")
        return TracingActivityConfig(
            id = json.getString("id"),
            title = json.getString("title"),
            activityType = json.getString("activityType"),
            instruction = instruction,
            instructionAudioId = instructionObject.getString("audioId"),
            successAudioId = feedback.getString("successAudioId"),
            retryAudioId = feedback.getString("retryAudioId"),
            successSfx = feedback.getString("successSfx").lowercase(),
            retrySfx = feedback.getString("retrySfx").lowercase(),
            targetSymbol = target
        )
    }
}

@Composable
fun TracingHomeScreen(
    avatarId: String,
    completedActivityIds: Set<String>,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(avatarId = avatarId, size = 64.dp)
                Column {
                    Text("Çiziyorum", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Parmağınla çizgileri takip et. Acele etmene gerek yok.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                if (activity.id in completedActivityIds) {
                                    Text(
                                        text = "✓ Tamamlandı",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
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
fun TracingActivityScreen(
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
    val audioPlayer = remember(context) { OfflineAudioPlayer(context) }

    DisposableEffect(audioPlayer) {
        onDispose { audioPlayer.close() }
    }

    LaunchedEffect(config.id, narrationEnabled, sfxEnabled, speechRate) {
        if (sfxEnabled) {
            audioPlayer.playSfx("sfx_slide_soft")
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

    fun playSuccessAudio() {
        if (narrationEnabled) audioPlayer.playSpeech(config.successAudioId, speechRate)
        if (sfxEnabled) audioPlayer.playSfx(config.successSfx)
    }

    fun playRetryAudio() {
        if (narrationEnabled) audioPlayer.playSpeech(config.retryAudioId, speechRate)
        if (sfxEnabled) audioPlayer.playSfx(config.retrySfx)
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
                    Text(config.instruction, style = MaterialTheme.typography.bodyLarge)
                }
                if (narrationEnabled) {
                    androidx.compose.material3.Button(
                        onClick = {
                            if (sfxEnabled) audioPlayer.playSfx("sfx_gentle_tap")
                            audioPlayer.playSpeech(config.instructionAudioId, speechRate)
                        }
                    ) {
                        Text("🔊 Dinle")
                    }
                }
            }

            if (config.activityType == "FIND_DIFFERENCE") {
                FindDifferenceGame(
                    modifier = Modifier.weight(1f),
                    onRetry = ::playRetryAudio,
                    onComplete = {
                        if (!completed) {
                            completed = true
                            playSuccessAudio()
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
                    onRetry = ::playRetryAudio,
                    onComplete = {
                        if (!completed) {
                            completed = true
                            playSuccessAudio()
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
    onRetry: () -> Unit,
    onComplete: () -> Unit
) {
    val normalizedGuide = remember(targetSymbol) { createGuide(targetSymbol) }
    var boardSize by remember { mutableStateOf(IntSize.Zero) }
    var progressIndex by remember(targetSymbol) { mutableStateOf(0) }
    var stroke by remember(targetSymbol) { mutableStateOf(emptyList<Offset>()) }
    var acceptedStart by remember(targetSymbol) { mutableStateOf(false) }
    var completed by remember(targetSymbol) { mutableStateOf(false) }
    var hint by remember(targetSymbol) { mutableStateOf("Büyük başlangıç noktasından başla.") }

    val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
    val childColor = MaterialTheme.colorScheme.primary
    val successColor = MaterialTheme.colorScheme.secondary
    val arrowColor = MaterialTheme.colorScheme.primary

    fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    fun toCanvasPoint(normalized: Offset): Offset =
        Offset(normalized.x * boardSize.width, normalized.y * boardSize.height)

    fun tolerance(): Float =
        (boardSize.width.coerceAtMost(boardSize.height) * 0.075f).coerceIn(22f, 52f)

    fun startTolerance(): Float = tolerance() * 1.30f

    fun markForwardProgress(point: Offset) {
        if (!acceptedStart || normalizedGuide.isEmpty()) return
        val from = progressIndex.coerceIn(0, normalizedGuide.lastIndex)
        val to = (from + 10).coerceAtMost(normalizedGuide.lastIndex)
        var bestIndex = from
        var bestDistance = Float.MAX_VALUE

        for (index in from..to) {
            val d = distance(toCanvasPoint(normalizedGuide[index]), point)
            if (d < bestDistance) {
                bestDistance = d
                bestIndex = index
            }
        }

        if (bestDistance <= tolerance() && bestIndex >= progressIndex) {
            progressIndex = bestIndex
        }
    }

    fun finishStroke() {
        if (!acceptedStart || normalizedGuide.isEmpty()) {
            stroke = emptyList()
            acceptedStart = false
            return
        }

        val ratio = (progressIndex + 1).toFloat() / normalizedGuide.size.toFloat()
        val reachedEnd = progressIndex >= (normalizedGuide.lastIndex - 2).coerceAtLeast(0)

        if (!completed && ratio >= 0.90f && reachedEnd) {
            completed = true
            hint = "Harika! Başlangıçtan hedefe doğru yolu tamamladın."
            onComplete()
        } else if (!completed) {
            hint = "Yolun sonuna ulaşmadın. Başlangıç noktasından yeniden dene."
            progressIndex = 0
            stroke = emptyList()
            acceptedStart = false
            onRetry()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .onSizeChanged { boardSize = it }
                .pointerInput(targetSymbol, boardSize, completed) {
                    detectDragGestures(
                        onDragStart = { point ->
                            if (!completed && normalizedGuide.isNotEmpty()) {
                                val expectedStart = toCanvasPoint(normalizedGuide.first())
                                if (distance(expectedStart, point) <= startTolerance()) {
                                    acceptedStart = true
                                    progressIndex = 0
                                    stroke = listOf(point)
                                    hint = "Ok yönünde ilerle ve hedef noktasına ulaş."
                                    markForwardProgress(point)
                                } else {
                                    acceptedStart = false
                                    stroke = emptyList()
                                    hint = "Önce büyük başlangıç noktasına dokun."
                                    onRetry()
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            if (acceptedStart && !completed) {
                                val point = change.position
                                stroke = stroke + point
                                markForwardProgress(point)
                            }
                            change.consume()
                        },
                        onDragEnd = { finishStroke() },
                        onDragCancel = { finishStroke() }
                    )
                }
        ) {
            normalizedGuide.forEachIndexed { index, n ->
                val point = Offset(n.x * size.width, n.y * size.height)
                val passed = acceptedStart && index <= progressIndex
                drawCircle(
                    color = if (passed || completed) successColor else guideColor,
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

                if (normalizedGuide.size >= 7) {
                    val fromN = normalizedGuide[2]
                    val toN = normalizedGuide[6]
                    val from = Offset(fromN.x * size.width, fromN.y * size.height)
                    val to = Offset(toN.x * size.width, toN.y * size.height)
                    drawLine(arrowColor, from, to, 4f, cap = StrokeCap.Round)
                    val angle = Math.atan2((to.y - from.y).toDouble(), (to.x - from.x).toDouble())
                    val head = 16f
                    val spread = Math.toRadians(28.0)
                    val p1 = Offset(
                        to.x - (kotlin.math.cos(angle - spread) * head).toFloat(),
                        to.y - (kotlin.math.sin(angle - spread) * head).toFloat()
                    )
                    val p2 = Offset(
                        to.x - (kotlin.math.cos(angle + spread) * head).toFloat(),
                        to.y - (kotlin.math.sin(angle + spread) * head).toFloat()
                    )
                    drawLine(arrowColor, to, p1, 4f, cap = StrokeCap.Round)
                    drawLine(arrowColor, to, p2, 4f, cap = StrokeCap.Round)
                }
            }

            if (acceptedStart) {
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

        Text(hint, style = MaterialTheme.typography.titleMedium)
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
    onRetry: () -> Unit,
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
                        val next = found + index
                        found = next
                        if (!finished && next.size == correct.size) {
                            finished = true
                            onComplete()
                        }
                    } else {
                        onRetry()
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
