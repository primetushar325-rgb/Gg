package com.example.smartedge.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.smartedge.data.PreferencesRepository
import com.example.smartedge.service.EdgePanelService
import com.example.smartedge.utils.PermissionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val repository = PreferencesRepository(context)
            CoroutineScope(Dispatchers.IO).launch {
                val autoStart = repository.isAutoStartEnabled.first()
                val isEnabled = repository.isServiceEnabled.first()

                if (autoStart && isEnabled && PermissionUtils.hasOverlayPermission(context)) {
                    val serviceIntent = Intent(context, EdgePanelService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            }
        }
    }
}
