package com.uysal.minikakademi.feature.avatarselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.AvatarPlaceholder
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen
import com.uysal.minikakademi.core.model.AvatarCatalog

@Composable
fun AvatarSelectionScreen(
    selectedAvatarId: String,
    onAvatarSelected: (String) -> Unit,
    onContinue: () -> Unit
) {
    KidScreen {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Avatarını Seç", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Bu karakter uygulamadaki tüm etkinliklerde seninle birlikte olacak.",
                style = MaterialTheme.typography.bodyLarge
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(AvatarCatalog.avatars, key = { it.id }) { avatar ->
                    val selected = avatar.id == selectedAvatarId
                    Card(
                        onClick = { onAvatarSelected(avatar.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AvatarPlaceholder(avatarId = avatar.id, size = 72.dp)
                            Text(avatar.label, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            KidPrimaryButton(text = "Bu Benim", onClick = onContinue)
        }
    }
}
