package com.uysal.minikakademi.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.audio.OfflineAudioPlayer
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.model.AppSettings
import com.uysal.minikakademi.core.model.LearningLevel
import com.uysal.minikakademi.core.model.SpeechRateOption
import com.uysal.minikakademi.core.model.ThemePreference

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val audioPlayer = remember(context) { OfflineAudioPlayer(context) }
    DisposableEffect(audioPlayer) {
        onDispose { audioPlayer.close() }
    }

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Minik Akademi'ye Hoş Geldiniz", style = MaterialTheme.typography.headlineMedium)
            Text(
                "İlk kurulumu bir yetişkin tamamlamalıdır. Kurulumdan sonra çocuk doğrudan kendi öğrenme alanına geçer.",
                style = MaterialTheme.typography.bodyLarge
            )
            KidPrimaryButton(
                text = "🔊 Karşılama Sesini Dinle",
                onClick = { audioPlayer.playSpeech("aud_common_welcome_01", 0.90f) }
            )
            KidPrimaryButton(text = "Kuruluma Başla", onClick = onStart)
        }
    }
}


@Composable
fun ParentStartGateScreen(onContinue: () -> Unit) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Yetişkin Kurulumu", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Bu ilk kurulum bölümü bir yetişkin tarafından tamamlanmalıdır.",
                style = MaterialTheme.typography.bodyLarge
            )
            KidPrimaryButton(text = "Yetişkin Olarak Devam Et", onClick = onContinue)
        }
    }
}

@Composable
fun LearningLevelScreen(
    selected: LearningLevel,
    onSelected: (LearningLevel) -> Unit,
    onContinue: () -> Unit
) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Eğitim Seviyesi", style = MaterialTheme.typography.headlineMedium)
            LearningLevel.entries.forEach { level ->
                SelectableCard(
                    title = level.label,
                    selected = level == selected,
                    onClick = { onSelected(level) }
                )
            }
            KidPrimaryButton(text = "Devam Et", onClick = onContinue)
        }
    }
}

@Composable
fun SoundSetupScreen(
    selected: SpeechRateOption,
    onSelected: (SpeechRateOption) -> Unit,
    onPreview: () -> Unit,
    onContinue: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val audioPlayer = remember(context) { OfflineAudioPlayer(context) }
    DisposableEffect(audioPlayer) {
        onDispose { audioPlayer.close() }
    }

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Konuşma Hızı", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Öğretmen/anlatıcı sesi uygulamanın içinde offline olarak bulunur. Burada konuşma temposunu seçin.",
                style = MaterialTheme.typography.bodyLarge
            )
            SpeechRateOption.entries.forEach { rate ->
                SelectableCard(
                    title = rate.label + " — " + rate.multiplier + "x",
                    selected = rate == selected,
                    onClick = { onSelected(rate) }
                )
            }
            KidPrimaryButton(
                text = "Örneği Dinle",
                onClick = {
                    audioPlayer.playSpeech("aud_common_ready_01", selected.multiplier)
                    onPreview()
                }
            )
            Text("Örnek ses APK içindeki offline ses paketinden çalınır.")
            KidPrimaryButton(text = "Devam Et", onClick = onContinue)
        }
    }
}

@Composable
fun ThemeSetupScreen(
    selected: ThemePreference,
    onSelected: (ThemePreference) -> Unit,
    onContinue: () -> Unit
) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Tema", style = MaterialTheme.typography.headlineMedium)
            ThemePreference.entries.forEach { theme ->
                SelectableCard(
                    title = theme.label,
                    selected = theme == selected,
                    onClick = { onSelected(theme) }
                )
            }
            KidPrimaryButton(text = "Devam Et", onClick = onContinue)
        }
    }
}

@Composable
fun ParentPinSetupScreen(
    onPinReady: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var repeatPin by remember { mutableStateOf("") }
    val ready = pin.length == 4 && pin == repeatPin

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Ebeveyn PIN'i", style = MaterialTheme.typography.headlineMedium)
            Text("Ebeveyn alanını çocuk ekranından ayırmak için 4 haneli PIN oluşturun.")

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(4) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("PIN") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
            OutlinedTextField(
                value = repeatPin,
                onValueChange = { repeatPin = it.filter(Char::isDigit).take(4) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("PIN tekrar") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = repeatPin.length == 4 && pin != repeatPin
            )
            if (repeatPin.length == 4 && pin != repeatPin) {
                Text("PIN'ler eşleşmiyor.", color = MaterialTheme.colorScheme.error)
            }

            KidPrimaryButton(
                text = "Devam Et",
                enabled = ready,
                onClick = { onPinReady(pin) }
            )
        }
    }
}

@Composable
fun SetupSummaryScreen(
    settings: AppSettings,
    onStartChildMode: () -> Unit
) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Kurulum Özeti", style = MaterialTheme.typography.headlineMedium)
            SummaryRow("Çocuk", settings.childName.ifBlank { "—" })
            SummaryRow("Seviye", settings.learningLevel.label)
            SummaryRow("Avatar", settings.avatarId)
            SummaryRow("Tema", settings.theme.label)
            SummaryRow(
                "Konuşma",
                settings.speechRate.label + " — " + settings.speechRate.multiplier + "x"
            )
            KidPrimaryButton(text = "Çocuk Modunu Başlat", onClick = onStartChildMode)
        }
    }
}

@Composable
private fun SelectableCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
