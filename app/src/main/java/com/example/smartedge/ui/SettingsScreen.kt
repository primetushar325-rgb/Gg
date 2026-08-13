package com.example.smartedge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.smartedge.data.PreferencesRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferencesRepository: PreferencesRepository,
    onToggleService: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isServiceEnabled by preferencesRepository.isServiceEnabled.collectAsState(initial = false)
    val isRightSide by preferencesRepository.isRightSide.collectAsState(initial = true)
    val handleWidth by preferencesRepository.handleWidth.collectAsState(initial = 18)
    val handleHeight by preferencesRepository.handleHeight.collectAsState(initial = 80)
    val isGamingMode by preferencesRepository.isGamingMode.collectAsState(initial = false)
    val isAutoStart by preferencesRepository.isAutoStartEnabled.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Smart Edge Panel Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Service Master Toggle
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Enable Edge Panel", style = MaterialTheme.typography.titleMedium)
                    Text("Show handle overlay on screen", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = isServiceEnabled,
                    onCheckedChange = { active ->
                        coroutineScope.launch {
                            preferencesRepository.setServiceEnabled(active)
                            onToggleService(active)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Position Toggle
        Text("Position & Side", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Right Side Handle")
            RadioButton(
                selected = isRightSide,
                onClick = { coroutineScope.launch { preferencesRepository.setPanelSideRight(true) } }
            )
            Text("Left Side Handle")
            RadioButton(
                selected = !isRightSide,
                onClick = { coroutineScope.launch { preferencesRepository.setPanelSideRight(false) } }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Handle Sizing
        Text("Handle Width: $handleWidth dp", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = handleWidth.toFloat(),
            onValueChange = { coroutineScope.launch { preferencesRepository.setHandleWidth(it.toInt()) } },
            valueRange = 10f..30f
        )

        Text("Handle Height: $handleHeight dp", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = handleHeight.toFloat(),
            onValueChange = { coroutineScope.launch { preferencesRepository.setHandleHeight(it.toInt()) } },
            valueRange = 50f..150f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gaming Mode (Prevents Accidental Swipes)")
            Switch(
                checked = isGamingMode,
                onCheckedChange = { coroutineScope.launch { preferencesRepository.setGamingMode(it) } }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto-Start on Reboot")
            Switch(
                checked = isAutoStart,
                onCheckedChange = { coroutineScope.launch { preferencesRepository.setAutoStartEnabled(it) } }
            )
        }
    }
}
