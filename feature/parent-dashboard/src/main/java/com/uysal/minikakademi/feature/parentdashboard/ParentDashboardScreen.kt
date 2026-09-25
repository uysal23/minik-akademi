package com.uysal.minikakademi.feature.parentdashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.model.AppSettings

@Composable
fun ParentDashboardScreen(
    settings: AppSettings,
    onOpenSettings: () -> Unit,
    onReturnToChild: () -> Unit
) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(settings.avatarId, size = 66.dp)
                Column {
                    Text("Ebeveyn Paneli", style = MaterialTheme.typography.headlineMedium)
                    Text(settings.childName.ifBlank { "Çocuk profili" })
                }
            }

            KidCard {
                Text("Bugün", style = MaterialTheme.typography.titleMedium)
                Text("Çalışma süresi: 0 dk")
            }
            KidCard {
                Text("Türkçe", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tamamlanan Türkçe etkinliği: " +
                        settings.completedLiteracyActivities.size
                )
            }
            KidCard {
                Text("Matematik", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Tamamlanan Matematik etkinliği: " +
                        settings.completedMathematicsActivities.size
                )
            }
            KidCard {
                Text("Tekrar Önerisi", style = MaterialTheme.typography.titleMedium)
                Text("Henüz kayıtlı tekrar bulunmuyor.")
            }

            KidPrimaryButton(text = "Ayarlar", onClick = onOpenSettings)
            KidPrimaryButton(text = "Çocuk Moduna Dön", onClick = onReturnToChild)
        }
    }
}
