package com.uysal.minikakademi.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Original, offline, child-friendly pictograms used by literacy/math activities.
 * They intentionally use simple 2D vector forms and do not copy source illustrations.
 */
@Composable
fun LearningObjectArt(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val k = label.trim().lowercase()

            fun leaf(x: Float, y: Float) {
                drawOval(
                    color = Color(0xFF5B9A58),
                    topLeft = Offset(x, y),
                    size = Size(w * 0.16f, h * 0.09f)
                )
            }
            fun wheel(x: Float, y: Float) {
                drawCircle(Color(0xFF34363B), w * 0.075f, Offset(x, y))
                drawCircle(Color(0xFFB9BEC7), w * 0.03f, Offset(x, y))
            }
            fun eye(x: Float, y: Float) {
                drawCircle(Color(0xFF22252A), w * 0.026f, Offset(x, y))
            }
            fun animalFace(face: Color, ear: Color = face) {
                drawCircle(face, w * 0.28f, Offset(w * 0.50f, h * 0.53f))
                drawOval(ear, Offset(w * 0.16f, h * 0.22f), Size(w * 0.22f, h * 0.20f))
                drawOval(ear, Offset(w * 0.62f, h * 0.22f), Size(w * 0.22f, h * 0.20f))
                eye(w * 0.41f, h * 0.50f)
                eye(w * 0.59f, h * 0.50f)
                drawArc(
                    color = Color(0xFF5F4A3C),
                    startAngle = 10f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(w * 0.42f, h * 0.57f),
                    size = Size(w * 0.16f, h * 0.10f),
                    style = Stroke(width = w * 0.025f, cap = StrokeCap.Round)
                )
            }

            when (k) {
                "elma" -> {
                    drawCircle(Color(0xFFE85A5A), w * 0.25f, Offset(w * 0.44f, h * 0.55f))
                    drawCircle(Color(0xFFE85A5A), w * 0.25f, Offset(w * 0.58f, h * 0.55f))
                    drawLine(Color(0xFF6D4C33), Offset(w * 0.51f, h * 0.34f), Offset(w * 0.53f, h * 0.22f), w * 0.04f)
                    leaf(w * 0.53f, h * 0.20f)
                }
                "armut" -> {
                    drawOval(Color(0xFFB7D75E), Offset(w * 0.27f, h * 0.34f), Size(w * 0.46f, h * 0.49f))
                    drawCircle(Color(0xFFB7D75E), w * 0.16f, Offset(w * 0.50f, h * 0.35f))
                    drawLine(Color(0xFF6D4C33), Offset(w * 0.50f, h * 0.23f), Offset(w * 0.51f, h * 0.14f), w * 0.035f)
                    leaf(w * 0.51f, h * 0.13f)
                }
                "limon" -> {
                    drawOval(Color(0xFFF4D84A), Offset(w * 0.22f, h * 0.36f), Size(w * 0.56f, h * 0.34f))
                    leaf(w * 0.58f, h * 0.26f)
                }
                "incir" -> {
                    drawOval(Color(0xFF8E5DA8), Offset(w * 0.29f, h * 0.33f), Size(w * 0.42f, h * 0.48f))
                    drawCircle(Color(0xFF8E5DA8), w * 0.12f, Offset(w * 0.50f, h * 0.34f))
                    leaf(w * 0.50f, h * 0.21f)
                }
                "yumurta" -> drawOval(Color(0xFFFFF1C8), Offset(w * 0.31f, h * 0.20f), Size(w * 0.38f, h * 0.62f))

                "araba" -> {
                    drawRoundRect(Color(0xFF4E86D8), Offset(w * 0.18f, h * 0.43f), Size(w * 0.64f, h * 0.26f), CornerRadius(w * 0.08f))
                    drawRoundRect(Color(0xFF84B7F1), Offset(w * 0.34f, h * 0.30f), Size(w * 0.34f, h * 0.22f), CornerRadius(w * 0.06f))
                    wheel(w * 0.32f, h * 0.72f); wheel(w * 0.68f, h * 0.72f)
                }
                "tren" -> {
                    drawRoundRect(Color(0xFF55A06B), Offset(w * 0.18f, h * 0.34f), Size(w * 0.60f, h * 0.36f), CornerRadius(w * 0.05f))
                    drawRect(Color(0xFFD8ECFF), Offset(w * 0.28f, h * 0.42f), Size(w * 0.16f, h * 0.13f))
                    drawRect(Color(0xFFD8ECFF), Offset(w * 0.50f, h * 0.42f), Size(w * 0.16f, h * 0.13f))
                    wheel(w * 0.32f, h * 0.74f); wheel(w * 0.66f, h * 0.74f)
                }
                "kayık" -> {
                    val p = Path().apply {
                        moveTo(w * 0.18f, h * 0.55f); lineTo(w * 0.82f, h * 0.55f)
                        lineTo(w * 0.68f, h * 0.76f); lineTo(w * 0.32f, h * 0.76f); close()
                    }
                    drawPath(p, Color(0xFFC47B3A))
                    drawLine(Color(0xFF6F4A2F), Offset(w * 0.50f, h * 0.55f), Offset(w * 0.50f, h * 0.20f), w * 0.025f)
                    val sail = Path().apply {
                        moveTo(w * 0.50f, h * 0.22f); lineTo(w * 0.74f, h * 0.50f); lineTo(w * 0.50f, h * 0.50f); close()
                    }
                    drawPath(sail, Color(0xFFFFD86B))
                }
                "uçak" -> {
                    drawRoundRect(Color(0xFFD9E2EA), Offset(w * 0.18f, h * 0.43f), Size(w * 0.64f, h * 0.14f), CornerRadius(w * 0.07f))
                    val wing = Path().apply {
                        moveTo(w * 0.44f, h * 0.44f); lineTo(w * 0.30f, h * 0.22f); lineTo(w * 0.53f, h * 0.44f); close()
                    }
                    drawPath(wing, Color(0xFF6E9DCE))
                    drawPath(Path().apply {
                        moveTo(w * 0.44f, h * 0.56f); lineTo(w * 0.30f, h * 0.77f); lineTo(w * 0.56f, h * 0.56f); close()
                    }, Color(0xFF6E9DCE))
                }
                "uçurtma" -> {
                    val p = Path().apply {
                        moveTo(w * 0.50f, h * 0.18f); lineTo(w * 0.75f, h * 0.44f)
                        lineTo(w * 0.50f, h * 0.70f); lineTo(w * 0.25f, h * 0.44f); close()
                    }
                    drawPath(p, Color(0xFFE86D83))
                    drawLine(Color(0xFF635A56), Offset(w * 0.50f, h * 0.70f), Offset(w * 0.62f, h * 0.90f), w * 0.018f)
                }

                "masa" -> {
                    drawRoundRect(Color(0xFFB98152), Offset(w * 0.16f, h * 0.35f), Size(w * 0.68f, h * 0.18f), CornerRadius(w * 0.04f))
                    drawRect(Color(0xFF8A5F3F), Offset(w * 0.24f, h * 0.50f), Size(w * 0.09f, h * 0.32f))
                    drawRect(Color(0xFF8A5F3F), Offset(w * 0.67f, h * 0.50f), Size(w * 0.09f, h * 0.32f))
                }
                "telefon" -> {
                    drawRoundRect(Color(0xFF424A57), Offset(w * 0.32f, h * 0.16f), Size(w * 0.36f, h * 0.68f), CornerRadius(w * 0.08f))
                    drawRoundRect(Color(0xFF9FD4F3), Offset(w * 0.37f, h * 0.24f), Size(w * 0.26f, h * 0.46f), CornerRadius(w * 0.03f))
                    drawCircle(Color(0xFFCFD3D7), w * 0.025f, Offset(w * 0.50f, h * 0.77f))
                }
                "balon" -> {
                    drawOval(Color(0xFFE96D87), Offset(w * 0.28f, h * 0.16f), Size(w * 0.44f, h * 0.52f))
                    drawLine(Color(0xFF777777), Offset(w * 0.50f, h * 0.67f), Offset(w * 0.56f, h * 0.92f), w * 0.015f)
                }
                "ceket", "mont" -> {
                    val p = Path().apply {
                        moveTo(w * 0.35f, h * 0.24f); lineTo(w * 0.22f, h * 0.38f)
                        lineTo(w * 0.30f, h * 0.80f); lineTo(w * 0.70f, h * 0.80f)
                        lineTo(w * 0.78f, h * 0.38f); lineTo(w * 0.65f, h * 0.24f)
                        lineTo(w * 0.55f, h * 0.33f); lineTo(w * 0.45f, h * 0.33f); close()
                    }
                    drawPath(p, if (k == "mont") Color(0xFF5B77B5) else Color(0xFFE17A5F))
                    drawLine(Color.White, Offset(w * 0.50f, h * 0.35f), Offset(w * 0.50f, h * 0.76f), w * 0.02f)
                }
                "terlik" -> {
                    drawOval(Color(0xFF8AB8D8), Offset(w * 0.25f, h * 0.36f), Size(w * 0.50f, h * 0.30f))
                    drawArc(Color(0xFF557E9E), 190f, 160f, false, Offset(w * 0.33f, h * 0.33f), Size(w * 0.34f, h * 0.28f), style = Stroke(w * 0.045f))
                }
                "silgi" -> {
                    drawRoundRect(Color(0xFFF49AA8), Offset(w * 0.25f, h * 0.34f), Size(w * 0.50f, h * 0.32f), CornerRadius(w * 0.07f))
                    drawRect(Color(0xFF80A7D7), Offset(w * 0.25f, h * 0.34f), Size(w * 0.20f, h * 0.32f))
                }
                "kalem" -> {
                    drawRoundRect(Color(0xFFF2C94C), Offset(w * 0.18f, h * 0.43f), Size(w * 0.60f, h * 0.12f), CornerRadius(w * 0.06f))
                    val tip = Path().apply {
                        moveTo(w * 0.78f, h * 0.43f); lineTo(w * 0.92f, h * 0.49f); lineTo(w * 0.78f, h * 0.55f); close()
                    }
                    drawPath(tip, Color(0xFFD5B28A))
                }
                "okul" -> {
                    drawRect(Color(0xFFF1C96D), Offset(w * 0.20f, h * 0.38f), Size(w * 0.60f, h * 0.42f))
                    val roof = Path().apply {
                        moveTo(w * 0.14f, h * 0.38f); lineTo(w * 0.50f, h * 0.16f); lineTo(w * 0.86f, h * 0.38f); close()
                    }
                    drawPath(roof, Color(0xFFD86555))
                    drawRect(Color(0xFF6E94B8), Offset(w * 0.44f, h * 0.58f), Size(w * 0.12f, h * 0.22f))
                }
                "orman" -> {
                    listOf(0.30f, 0.50f, 0.70f).forEachIndexed { index, x ->
                        drawRect(Color(0xFF79553B), Offset(w * (x - 0.025f), h * 0.58f), Size(w * 0.05f, h * 0.23f))
                        drawCircle(
                            if (index == 1) Color(0xFF4A9656) else Color(0xFF5BA567),
                            w * 0.18f,
                            Offset(w * x, h * 0.45f)
                        )
                    }
                }

                "inek" -> {
                    animalFace(Color(0xFFF1ECE5))
                    drawCircle(Color(0xFF5D5550), w * 0.05f, Offset(w * 0.42f, h * 0.39f))
                    drawCircle(Color(0xFF5D5550), w * 0.06f, Offset(w * 0.61f, h * 0.60f))
                }
                "aslan" -> {
                    drawCircle(Color(0xFFC98535), w * 0.36f, Offset(w * 0.50f, h * 0.52f))
                    animalFace(Color(0xFFE7B75A), Color(0xFFD79A3F))
                }
                "tavşan" -> {
                    drawOval(Color(0xFFE7DDD3), Offset(w * 0.31f, h * 0.06f), Size(w * 0.15f, h * 0.36f))
                    drawOval(Color(0xFFE7DDD3), Offset(w * 0.54f, h * 0.06f), Size(w * 0.15f, h * 0.36f))
                    drawCircle(Color(0xFFE7DDD3), w * 0.27f, Offset(w * 0.50f, h * 0.56f))
                    eye(w * 0.42f, h * 0.52f); eye(w * 0.58f, h * 0.52f)
                    drawCircle(Color(0xFFE58A9D), w * 0.045f, Offset(w * 0.50f, h * 0.61f))
                }
                "koyun" -> {
                    listOf(
                        Offset(w * 0.38f, h * 0.47f), Offset(w * 0.50f, h * 0.42f),
                        Offset(w * 0.62f, h * 0.47f), Offset(w * 0.42f, h * 0.60f),
                        Offset(w * 0.58f, h * 0.60f)
                    ).forEach { drawCircle(Color(0xFFF5F1E9), w * 0.16f, it) }
                    drawOval(Color(0xFF62554E), Offset(w * 0.37f, h * 0.44f), Size(w * 0.26f, h * 0.30f))
                    eye(w * 0.45f, h * 0.55f); eye(w * 0.55f, h * 0.55f)
                }
                "kelebek" -> {
                    drawOval(Color(0xFFF08CB4), Offset(w * 0.18f, h * 0.27f), Size(w * 0.30f, h * 0.32f))
                    drawOval(Color(0xFF8AB9EC), Offset(w * 0.52f, h * 0.27f), Size(w * 0.30f, h * 0.32f))
                    drawOval(Color(0xFFF5B66E), Offset(w * 0.24f, h * 0.52f), Size(w * 0.24f, h * 0.24f))
                    drawOval(Color(0xFF9CD38C), Offset(w * 0.52f, h * 0.52f), Size(w * 0.24f, h * 0.24f))
                    drawRoundRect(Color(0xFF5D5049), Offset(w * 0.47f, h * 0.28f), Size(w * 0.06f, h * 0.48f), CornerRadius(w * 0.03f))
                }
                else -> {
                    drawCircle(Color(0xFF8DB7E8), w * 0.27f, Offset(w * 0.50f, h * 0.50f))
                    drawCircle(Color(0xFFF6D46B), w * 0.12f, Offset(w * 0.50f, h * 0.50f))
                }
            }
        }
    }
}
