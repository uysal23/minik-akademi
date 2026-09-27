package com.uysal.minikakademi.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uysal.minikakademi.core.model.ThemePreference

private val PastelScheme = lightColorScheme(
    primary = Color(0xFF5B67D6),
    onPrimary = Color.White,
    secondary = Color(0xFFFFB86B),
    onSecondary = Color(0xFF3C2A18),
    background = Color(0xFFFFFBF7),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF273043)
)

private val NatureScheme = lightColorScheme(
    primary = Color(0xFF4F8A62),
    onPrimary = Color.White,
    secondary = Color(0xFFE3B95F),
    onSecondary = Color(0xFF2A2416),
    background = Color(0xFFF8F5E9),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF24352A)
)

private val SkyScheme = lightColorScheme(
    primary = Color(0xFF4778C9),
    onPrimary = Color.White,
    secondary = Color(0xFF8C7AC8),
    onSecondary = Color.White,
    background = Color(0xFFF6FAFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF26344A)
)

private val HighContrastScheme = darkColorScheme(
    primary = Color(0xFFFFE66D),
    onPrimary = Color.Black,
    secondary = Color(0xFF8BE9FD),
    onSecondary = Color.Black,
    background = Color(0xFF101010),
    surface = Color(0xFF181818),
    onSurface = Color.White
)

@Composable
fun MinikAkademiTheme(
    themePreference: ThemePreference,
    highContrast: Boolean,
    largeUi: Boolean,
    content: @Composable () -> Unit
) {
    val scheme: ColorScheme = if (highContrast || themePreference == ThemePreference.HIGH_CONTRAST) {
        HighContrastScheme
    } else {
        when (themePreference) {
            ThemePreference.PASTEL -> PastelScheme
            ThemePreference.NATURE -> NatureScheme
            ThemePreference.SKY -> SkyScheme
            ThemePreference.HIGH_CONTRAST -> HighContrastScheme
        }
    }

    val baseTypography = Typography()
    val typography = if (largeUi) {
        Typography(
            headlineLarge = baseTypography.headlineLarge.copy(fontSize = 36.sp),
            headlineMedium = baseTypography.headlineMedium.copy(fontSize = 30.sp),
            titleLarge = baseTypography.titleLarge.copy(fontSize = 26.sp),
            titleMedium = baseTypography.titleMedium.copy(fontSize = 22.sp),
            bodyLarge = baseTypography.bodyLarge.copy(fontSize = 20.sp),
            labelLarge = baseTypography.labelLarge.copy(fontSize = 18.sp)
        )
    } else {
        baseTypography
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = typography,
        content = content
    )
}

@Composable
fun KidScreen(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = horizontalPadding, vertical = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}

@Composable
fun KidPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun KidCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

enum class AvatarPose {
    IDLE,
    SMILE,
    WAVE,
    LISTEN,
    POINT_LEFT,
    POINT_RIGHT,
    POINT_UP,
    POINT_DOWN,
    THINKING,
    WALK_LEFT,
    WALK_RIGHT,
    HOLD_OBJECT,
    CELEBRATE,
    CLAP,
    SIT,
    SURPRISED_SOFT
}

private data class AvatarVisualSpec(
    val skin: Color,
    val hair: Color,
    val shirt: Color,
    val longHair: Boolean,
    val curlyHair: Boolean,
    val glasses: Boolean
)

private val avatarVisuals = listOf(
    AvatarVisualSpec(Color(0xFFF5C9A6), Color(0xFF5A3825), Color(0xFF5B67D6), false, false, false),
    AvatarVisualSpec(Color(0xFFD99A6C), Color(0xFF2E211C), Color(0xFFDB6E82), true, false, false),
    AvatarVisualSpec(Color(0xFF8B5A3C), Color(0xFF221713), Color(0xFF4F8A62), false, true, false),
    AvatarVisualSpec(Color(0xFFF0B78D), Color(0xFF9A5A2E), Color(0xFF4778C9), false, false, false),
    AvatarVisualSpec(Color(0xFFC47F57), Color(0xFF3B281F), Color(0xFFE3A857), true, true, false),
    AvatarVisualSpec(Color(0xFF6F452F), Color(0xFF17120F), Color(0xFF8C7AC8), false, true, false),
    AvatarVisualSpec(Color(0xFFE7AE83), Color(0xFF49301F), Color(0xFF3B8D99), false, false, true),
    AvatarVisualSpec(Color(0xFF9D6546), Color(0xFF201613), Color(0xFFC96A84), true, false, true),
    AvatarVisualSpec(Color(0xFFF1C3A0), Color(0xFF6B4028), Color(0xFF609D62), true, false, false),
    AvatarVisualSpec(Color(0xFF7A4D36), Color(0xFF19110E), Color(0xFFD58A3D), false, true, false),
    AvatarVisualSpec(Color(0xFFB97852), Color(0xFF332219), Color(0xFF6676D8), true, true, false),
    AvatarVisualSpec(Color(0xFFE3A77E), Color(0xFF7A4B2B), Color(0xFF4D9B8F), false, false, false)
)

@Composable
fun AvatarPlaceholder(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Dp = 92.dp,
    pose: AvatarPose = AvatarPose.SMILE
) {
    val rawIndex = avatarId.substringAfterLast("_").toIntOrNull()?.minus(1) ?: 0
    val spec = avatarVisuals[rawIndex.coerceIn(0, avatarVisuals.lastIndex)]
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)

    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f), CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.08f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.50f
            val headY = h * 0.39f
            val headR = w * 0.25f

            if (spec.longHair) {
                drawRoundRect(
                    color = spec.hair,
                    topLeft = Offset(cx - headR * 1.15f, headY - headR * 0.60f),
                    size = Size(headR * 2.30f, headR * 2.55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(headR * 0.7f)
                )
            }

            drawRoundRect(
                color = spec.shirt,
                topLeft = Offset(w * 0.22f, h * 0.66f),
                size = Size(w * 0.56f, h * 0.28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.16f)
            )

            drawCircle(color = spec.skin, radius = headR, center = Offset(cx, headY))
            drawCircle(
                color = outline,
                radius = headR,
                center = Offset(cx, headY),
                style = Stroke(width = (w * 0.018f).coerceAtLeast(1.5f))
            )

            if (spec.curlyHair) {
                val curls = listOf(-0.78f, -0.42f, 0f, 0.42f, 0.78f)
                curls.forEach { shift ->
                    drawCircle(
                        color = spec.hair,
                        radius = headR * 0.38f,
                        center = Offset(cx + shift * headR, headY - headR * 0.78f)
                    )
                }
            } else {
                drawArc(
                    color = spec.hair,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(cx - headR * 1.05f, headY - headR * 1.05f),
                    size = Size(headR * 2.10f, headR * 1.45f)
                )
            }

            val eyeY = headY - headR * 0.05f
            val eyeDx = headR * 0.38f
            drawCircle(outline, headR * 0.065f, Offset(cx - eyeDx, eyeY))
            drawCircle(outline, headR * 0.065f, Offset(cx + eyeDx, eyeY))

            if (spec.glasses) {
                val lensR = headR * 0.25f
                drawCircle(outline, lensR, Offset(cx - eyeDx, eyeY), style = Stroke(width = w * 0.018f))
                drawCircle(outline, lensR, Offset(cx + eyeDx, eyeY), style = Stroke(width = w * 0.018f))
                drawLine(
                    outline,
                    Offset(cx - eyeDx + lensR, eyeY),
                    Offset(cx + eyeDx - lensR, eyeY),
                    strokeWidth = w * 0.018f
                )
            }

            val mouthY = headY + headR * 0.38f
            when (pose) {
                AvatarPose.SURPRISED_SOFT -> drawCircle(
                    color = outline,
                    radius = headR * 0.10f,
                    center = Offset(cx, mouthY),
                    style = Stroke(width = w * 0.018f)
                )
                AvatarPose.THINKING -> drawLine(
                    color = outline,
                    start = Offset(cx - headR * 0.16f, mouthY),
                    end = Offset(cx + headR * 0.10f, mouthY + headR * 0.04f),
                    strokeWidth = w * 0.022f,
                    cap = StrokeCap.Round
                )
                else -> drawArc(
                    color = outline,
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(cx - headR * 0.30f, mouthY - headR * 0.20f),
                    size = Size(headR * 0.60f, headR * 0.38f),
                    style = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
                )
            }

            val armColor = spec.skin
            when (pose) {
                AvatarPose.WAVE, AvatarPose.CELEBRATE -> {
                    drawLine(
                        armColor,
                        Offset(w * 0.72f, h * 0.74f),
                        Offset(w * 0.88f, h * 0.47f),
                        strokeWidth = w * 0.07f,
                        cap = StrokeCap.Round
                    )
                }
                AvatarPose.POINT_LEFT -> drawLine(
                    armColor,
                    Offset(w * 0.30f, h * 0.75f),
                    Offset(w * 0.08f, h * 0.66f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round
                )
                AvatarPose.POINT_RIGHT -> drawLine(
                    armColor,
                    Offset(w * 0.70f, h * 0.75f),
                    Offset(w * 0.92f, h * 0.66f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round
                )
                AvatarPose.POINT_UP -> drawLine(
                    armColor,
                    Offset(w * 0.70f, h * 0.74f),
                    Offset(w * 0.72f, h * 0.46f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round
                )
                AvatarPose.POINT_DOWN -> drawLine(
                    armColor,
                    Offset(w * 0.70f, h * 0.74f),
                    Offset(w * 0.76f, h * 0.93f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round
                )
                AvatarPose.CLAP, AvatarPose.HOLD_OBJECT -> {
                    drawLine(
                        armColor,
                        Offset(w * 0.30f, h * 0.76f),
                        Offset(w * 0.48f, h * 0.78f),
                        strokeWidth = w * 0.07f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        armColor,
                        Offset(w * 0.70f, h * 0.76f),
                        Offset(w * 0.52f, h * 0.78f),
                        strokeWidth = w * 0.07f,
                        cap = StrokeCap.Round
                    )
                }
                else -> Unit
            }
        }
    }
}

