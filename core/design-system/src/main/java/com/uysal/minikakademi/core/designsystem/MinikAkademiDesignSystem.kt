package com.uysal.minikakademi.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun AvatarPlaceholder(
    avatarId: String,
    modifier: Modifier = Modifier,
    size: Dp = 92.dp
) {
    val number = avatarId.substringAfterLast("_").padStart(2, '0')
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.26f), CircleShape)
            .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "A$number",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
