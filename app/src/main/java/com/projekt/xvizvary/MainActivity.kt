package com.projekt.xvizvary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.projekt.xvizvary.ui.MainActivityViewModel
import com.projekt.xvizvary.ui.navigation.SmartBudgetRoot
import com.projekt.xvizvary.ui.theme.ProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.language.collect { lang ->
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(lang.languageTag)
                    )
                }
            }
        }

        setContent {
            ProjectTheme(darkTheme = true, dynamicColor = false) { SmartBudgetRoot() }
        }
    }
}