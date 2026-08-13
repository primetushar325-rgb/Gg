package com.example.smartedge.floating

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.smartedge.model.FloatingMode
import com.example.smartedge.utils.AppUtils

class FloatingModeManager(private val context: Context) {

    fun launchApp(packageName: String) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)

        if (launchIntent == null) {
            Toast.makeText(context, "Cannot open app: Launcher intent not found.", Toast.LENGTH_SHORT).show()
            return
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val capability = AppUtils.checkAppCapabilities(context, packageName)

        if (capability == FloatingMode.PIP_SUPPORTED) {
            Toast.makeText(context, "Opening app (Supports Picture-in-Picture)", Toast.LENGTH_SHORT).show()
        } else if (capability == FloatingMode.UNKNOWN && TecnoFloatingWindowHelper.isTecnoDevice()) {
            Toast.makeText(context, "Opening app via HiOS standard launcher", Toast.LENGTH_SHORT).show()
        }

        try {
            context.startActivity(launchIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to launch app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
