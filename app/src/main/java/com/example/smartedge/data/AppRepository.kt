package com.example.smartedge.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.smartedge.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {

    suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedInfos = pm.queryIntentActivities(mainIntent, 0)
        val favorites = preferencesRepository.favoritePackages.first()

        resolvedInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == context.packageName) return@mapNotNull null

            val label = resolveInfo.loadLabel(pm).toString()
            val icon = resolveInfo.loadIcon(pm)
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            InstalledApp(
                packageName = packageName,
                label = label,
                icon = icon,
                isFavorite = favorites.contains(packageName),
                isSystemApp = isSystem
            )
        }.sortedWith(compareByDescending<InstalledApp> { it.isFavorite }.thenBy { it.label.lowercase() })
    }

    suspend fun getFavoriteApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val allApps = getInstalledApps()
        allApps.filter { it.isFavorite }
    }
}
