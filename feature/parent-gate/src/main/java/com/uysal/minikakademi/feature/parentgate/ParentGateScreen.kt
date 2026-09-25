package com.uysal.minikakademi.feature.parentgate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uysal.minikakademi.core.designsystem.KidPrimaryButton
import com.uysal.minikakademi.core.designsystem.KidScreen

@Composable
fun ParentGateScreen(
    hasError: Boolean,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }

    KidScreen {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Ebeveyn Alanı", style = MaterialTheme.typography.headlineMedium)
            Text("Devam etmek için 4 haneli ebeveyn PIN'ini girin.")

            OutlinedTextField(
                value = pin,
                onValueChange = { value ->
                    pin = value.filter(Char::isDigit).take(4)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                isError = hasError
            )
            if (hasError) {
                Text("PIN eşleşmedi. Tekrar deneyin.", color = MaterialTheme.colorScheme.error)
            }

            KidPrimaryButton(
                text = "Giriş",
                enabled = pin.length == 4,
                onClick = { onSubmit(pin) }
            )
            KidPrimaryButton(text = "Çocuk Moduna Dön", onClick = onCancel)
        }
    }
}
