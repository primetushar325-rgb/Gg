package com.example.smartedge.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartedge.data.AppRepository
import com.example.smartedge.data.PreferencesRepository
import com.example.smartedge.service.EdgePanelService
import com.example.smartedge.utils.PermissionUtils

class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesRepository = PreferencesRepository(applicationContext)
        appRepository = AppRepository(applicationContext, preferencesRepository)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                MainAppNavigation(
                    preferencesRepository = preferencesRepository,
                    appRepository = appRepository,
                    onToggleService = { enable -> toggleService(enable) }
                )
            }
        }
    }

    private fun toggleService(enable: Boolean) {
        val intent = Intent(this, EdgePanelService::class.java)
        if (enable) {
            if (PermissionUtils.hasOverlayPermission(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            } else {
                PermissionUtils.requestOverlayPermission(this)
            }
        } else {
            stopService(intent)
        }
    }
}

@Composable
fun MainAppNavigation(
    preferencesRepository: PreferencesRepository,
    appRepository: AppRepository,
    onToggleService: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Panel") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("picker") },
                    icon = { Icon(Icons.Default.Apps, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("permissions") },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Permissions") },
                    label = { Text("Permissions") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("details") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Details") },
                    label = { Text("Capabilities") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "settings",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("settings") {
                SettingsScreen(preferencesRepository, onToggleService)
            }
            composable("picker") {
                AppPickerScreen(appRepository, preferencesRepository)
            }
            composable("permissions") {
                PermissionCenterScreen()
            }
            composable("details") {
                AppDetailsScreen()
            }
        }
    }
}
