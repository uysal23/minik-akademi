package com.uysal.minikakademi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.uysal.minikakademi.core.datastore.AppPreferencesRepository
import com.uysal.minikakademi.core.designsystem.MinikAkademiTheme
import com.uysal.minikakademi.core.model.AppSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AppPreferencesRepository(applicationContext)

        setContent {
            val settings by produceState<AppSettings?>(initialValue = null, repository) {
                repository.settings.collect { value = it }
            }

            val current = settings ?: AppSettings()

            MinikAkademiTheme(
                themePreference = current.theme,
                highContrast = current.highContrast,
                largeUi = current.largeUi
            ) {
                if (settings == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    MinikAkademiNavHost(
                        repository = repository,
                        settings = current
                    )
                }
            }
        }
    }
}
