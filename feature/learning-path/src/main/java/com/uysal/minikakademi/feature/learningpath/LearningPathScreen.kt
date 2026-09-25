package com.uysal.minikakademi.feature.learningpath

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen

@Composable
fun LearningPathScreen(
    category: String,
    onBackHome: () -> Unit
) {
    val title = when (category) {
        "tracing" -> "Çiziyorum"
        "literacy" -> "Harfleri Öğreniyorum"
        "math" -> "Matematik Öğreniyorum"
        "games" -> "Oyun Zamanı"
        else -> "Öğrenme Yolu"
    }

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Text(
                "Öğrenme yolu hazır. Gerçek ders düğümleri ilgili build adımlarında bağlanacak.",
                style = MaterialTheme.typography.bodyLarge
            )

            listOf("Tamamlandı", "Sıradaki", "Biraz daha çalışınca açılacak").forEachIndexed { itemIndex, itemText ->
                KidCard {
                    Text((itemIndex + 1).toString() + ". adım", style = MaterialTheme.typography.titleMedium)
                    Text(itemText)
                }
            }

            KidPrimaryButton(text = "Ana Sayfa", onClick = onBackHome)
        }
    }
}
