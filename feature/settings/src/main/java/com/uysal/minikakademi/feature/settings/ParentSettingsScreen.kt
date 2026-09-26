package com.uysal.minikakademi.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.model.AppSettings
import com.uysal.minikakademi.core.model.SpeechRateOption
import com.uysal.minikakademi.core.model.ThemePreference

@Composable
fun ParentSettingsScreen(
    settings: AppSettings,
    onThemeChanged: (ThemePreference) -> Unit,
    onSpeechRateChanged: (SpeechRateOption) -> Unit,
    onNarrationChanged: (Boolean) -> Unit,
    onSfxChanged: (Boolean) -> Unit,
    onMusicChanged: (Boolean) -> Unit,
    onReducedMotionChanged: (Boolean) -> Unit,
    onLargeUiChanged: (Boolean) -> Unit,
    onHighContrastChanged: (Boolean) -> Unit,
    onLeftHandedChanged: (Boolean) -> Unit,
    onDailyGoalChanged: (Int) -> Unit,
    onBack: () -> Unit
) {
    KidScreen {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ayarlar", style = MaterialTheme.typography.headlineMedium)

            KidCard {
                Text("Konuşma Hızı", style = MaterialTheme.typography.titleMedium)
                SpeechRateOption.entries.forEach { rate ->
                    ChoiceRow(
                        text = rate.label + " — " + rate.multiplier + "x",
                        selected = settings.speechRate == rate,
                        onClick = { onSpeechRateChanged(rate) }
                    )
                }
            }

            KidCard {
                Text("Tema", style = MaterialTheme.typography.titleMedium)
                ThemePreference.entries.forEach { theme ->
                    ChoiceRow(
                        text = theme.label,
                        selected = settings.theme == theme,
                        onClick = { onThemeChanged(theme) }
                    )
                }
            }

            KidCard {
                Text("Ses", style = MaterialTheme.typography.titleMedium)
                ToggleRow("Anlatıcı", settings.narrationEnabled, onNarrationChanged)
                ToggleRow("Efekt Sesleri", settings.sfxEnabled, onSfxChanged)
                ToggleRow("Arka Plan Müziği", settings.musicEnabled, onMusicChanged)
            }

            KidCard {
                Text("Erişilebilirlik", style = MaterialTheme.typography.titleMedium)
                ToggleRow("Azaltılmış Hareket", settings.reducedMotion, onReducedMotionChanged)
                ToggleRow("Büyük UI", settings.largeUi, onLargeUiChanged)
                ToggleRow("Yüksek Kontrast", settings.highContrast, onHighContrastChanged)
                ToggleRow("Solak Kullanım", settings.leftHanded, onLeftHandedChanged)
            }

            KidCard {
                Text("Günlük Hedef", style = MaterialTheme.typography.titleMedium)
                Text(settings.dailyGoalMinutes.toString() + " dakika")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.Button(
                        onClick = { onDailyGoalChanged(settings.dailyGoalMinutes - 5) },
                        modifier = Modifier.weight(1f)
                    ) { Text("-5 dk") }
                    androidx.compose.material3.Button(
                        onClick = { onDailyGoalChanged(settings.dailyGoalMinutes + 5) },
                        modifier = Modifier.weight(1f)
                    ) { Text("+5 dk") }
                }
            }

            KidPrimaryButton(text = "Ebeveyn Paneline Dön", onClick = onBack)
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ChoiceRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (selected) "✓  $text" else text)
    }
}
