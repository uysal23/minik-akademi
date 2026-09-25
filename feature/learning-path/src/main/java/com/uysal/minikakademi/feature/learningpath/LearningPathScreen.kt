package com.uysal.minikakademi.feature.learningpath

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.KidCard
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen

@Composable
fun LearningPathScreen(
    category: String,
    onBackHome: () -> Unit
) {
    if (category == "math") {
        MathematicsRootScreen(onBackHome = onBackHome)
        return
    }

    val title = when (category) {
        "tracing" -> "Çiziyorum"
        "literacy" -> "Harfleri Öğreniyorum"
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

@Composable
private fun MathematicsRootScreen(onBackHome: () -> Unit) {
    val context = LocalContext.current
    val avatarId = remember {
        context.getSharedPreferences("minik_akademi_bridge", 0)
            .getString("avatar_id", "avatar_01")
            ?: "avatar_01"
    }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var activityId by remember { mutableStateOf<String?>(null) }

    when {
        activityId != null -> MathematicsActivityScreen(
            activityId = activityId.orEmpty(),
            avatarId = avatarId,
            onBack = { activityId = null }
        )

        categoryId != null -> MathematicsCategoryScreen(
            sequenceId = categoryId.orEmpty(),
            avatarId = avatarId,
            onOpenActivity = { activityId = it },
            onBack = { categoryId = null }
        )

        else -> MathematicsHomeScreen(
            avatarId = avatarId,
            onOpenCategory = { categoryId = it },
            onBack = onBackHome
        )
    }
}
