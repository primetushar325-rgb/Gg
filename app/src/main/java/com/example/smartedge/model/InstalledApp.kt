package com.example.smartedge.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val isFavorite: Boolean = false,
    val isSystemApp: Boolean = false
)
