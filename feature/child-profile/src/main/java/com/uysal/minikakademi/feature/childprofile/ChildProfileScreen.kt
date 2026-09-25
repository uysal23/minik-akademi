package com.uysal.minikakademi.feature.childprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen

@Composable
fun ChildProfileScreen(
    initialName: String,
    onContinue: (String) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = "Çocuk Profili",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Çocuğun ekranda göreceği adı veya takma adı yazın.",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = name,
                onValueChange = { value -> if (value.length <= 24) name = value },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Görünen ad") }
            )
            KidPrimaryButton(
                text = "Devam Et",
                enabled = name.trim().isNotEmpty(),
                onClick = { onContinue(name.trim()) }
            )
        }
    }
}
