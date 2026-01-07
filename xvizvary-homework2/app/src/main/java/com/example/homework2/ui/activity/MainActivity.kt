package com.example.homework2.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homework2.navigation.Destination
import com.example.homework2.navigation.NavGraph
import com.example.homework2.ui.components.LoadingScreen
import com.example.homework2.ui.theme.Homework2Theme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt automaticky vytvoří ViewModel a repository za nás
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Homework2Theme {
                // Sledujeme stav přihlášení (null = načítání, true = ok, false = login)
                val isLoggedIn by mainViewModel.authState.collectAsStateWithLifecycle()

                when (isLoggedIn) {
                    null -> {
                        // Tady můžeš mít SplashScreen nebo jen prázdné pozadí
                        LoadingScreen()
                    }
                    else -> {
                        // NavGraphu předáme informaci o startovní destinaci
                        val startDest = if (isLoggedIn == true) {
                            Destination.MapScreen.route
                        } else {
                            "auth_section" // Tvůj route pro login
                        }

                        NavGraph()
                    }
                }
            }
        }
    }
}
