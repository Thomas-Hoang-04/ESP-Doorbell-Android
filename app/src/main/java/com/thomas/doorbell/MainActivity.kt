package com.thomas.doorbell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomas.doorbell.nav.DoorbellComposeApp
import com.thomas.doorbell.ui.theme.DoorbellTheme
import com.thomas.doorbell.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val account by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = 0xFFFFFFFF.toInt(),
                darkScrim = 0xFF000000.toInt()
            )
        )
        account.load()
        installSplashScreen().setKeepOnScreenCondition { !account.isReady.value }
        setContent {
            val startDestination by account.startDestination.collectAsStateWithLifecycle()
            DoorbellTheme {
                DoorbellComposeApp(
                    startDestination = startDestination,
                    modifier = Modifier,
                    viewModel = account
                )
            }
        }
    }
}
