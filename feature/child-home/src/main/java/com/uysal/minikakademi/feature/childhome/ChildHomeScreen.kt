package com.uysal.minikakademi.feature.childhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.model.AppSettings

@Composable
fun ChildHomeScreen(
    settings: AppSettings,
    onContinue: () -> Unit,
    onOpenTracing: () -> Unit,
    onOpenLiteracy: () -> Unit,
    onOpenMath: () -> Unit,
    onOpenGames: () -> Unit,
    onParentAccess: () -> Unit
) {
    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarPlaceholder(settings.avatarId, size = 74.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Merhaba, " + settings.childName.ifBlank { "Minik Kaşif" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bugün birlikte biraz öğrenelim.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                ParentAccessButton(onParentAccess = onParentAccess)
            }

            KidPrimaryButton(text = "DEVAM ET", onClick = onContinue)

            CategoryCard(
                title = "Çiziyorum",
                subtitle = "Çizgi ve parmak çalışmaları",
                onClick = onOpenTracing
            )
            CategoryCard(
                title = "Harfleri Öğreniyorum",
                subtitle = "Ses, harf, hece ve kelimeler",
                onClick = onOpenLiteracy
            )
            CategoryCard(
                title = "Matematik Öğreniyorum",
                subtitle = "Sayılar, sayma ve matematik kavramları",
                onClick = onOpenMath
            )
            CategoryCard(
                title = "Oyun Zamanı",
                subtitle = "Açılan pedagojik mini oyunlar",
                onClick = onOpenGames
            )
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ParentAccessButton(onParentAccess: () -> Unit) {
    Card(
        onClick = onParentAccess,
        modifier = Modifier.size(54.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .size(54.dp)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("⚙", style = MaterialTheme.typography.titleMedium)
        }
    }
}
