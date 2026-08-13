package com.example.smartedge.model

import androidx.compose.ui.graphics.vector.ImageVector

sealed class PanelItem {
    data class AppItem(val app: InstalledApp) : PanelItem()
    data class ToolItem(
        val id: String,
        val name: String,
        val icon: ImageVector
    ) : PanelItem()
}
