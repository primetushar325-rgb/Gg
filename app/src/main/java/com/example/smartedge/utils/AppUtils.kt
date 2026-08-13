package com.example.smartedge.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import com.example.smartedge.model.FloatingMode

object AppUtils {

    fun checkAppCapabilities(context: Context, packageName: String): FloatingMode {
        val pm = context.packageManager
        return try {
            val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA)
            if (resolveInfos.isNotEmpty()) {
                val activityInfo = resolveInfos[0].activityInfo
                val supportsPip = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    (activityInfo.flags and ActivityInfo.FLAG_SUPPORTS_PICTURE_IN_PICTURE) != 0
                } else false

                if (supportsPip) {
                    FloatingMode.PIP_SUPPORTED
                } else {
                    FloatingMode.UNKNOWN
                }
            } else {
                FloatingMode.NORMAL_ONLY
            }
        } catch (e: Exception) {
            FloatingMode.NORMAL_ONLY
        }
    }
}
