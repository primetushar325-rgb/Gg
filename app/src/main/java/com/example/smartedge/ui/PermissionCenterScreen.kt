package com.example.smartedge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.smartedge.utils.PermissionUtils

@Composable
fun PermissionCenterScreen() {
    val context = LocalContext.current
    var hasOverlay by remember { mutableStateOf(PermissionUtils.hasOverlayPermission(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Permission Center", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Display Over Other Apps (Overlay)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (hasOverlay) "Status: Granted ✓" else "Status: Not Granted ✗",
                    color = if (hasOverlay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    PermissionUtils.requestOverlayPermission(context)
                }) {
                    Text("Grant Permission")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Battery Optimization / Background Execution", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Ensure background service is not killed by HiOS power manager.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    PermissionUtils.openBatteryOptimizationSettings(context)
                }) {
                    Text("Battery Settings")
                }
            }
        }
    }
}
