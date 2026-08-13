package com.example.smartedge.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "smart_edge_settings")

class PreferencesRepository(private val context: Context) {

    companion object {
        val KEY_SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        val KEY_HANDLE_POSITION_Y = floatPreferencesKey("handle_position_y")
        val KEY_PANEL_SIDE_RIGHT = booleanPreferencesKey("panel_side_right")
        val KEY_HANDLE_WIDTH = intPreferencesKey("handle_width")
        val KEY_HANDLE_HEIGHT = intPreferencesKey("handle_height")
        val KEY_HANDLE_OPACITY = floatPreferencesKey("handle_opacity")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val KEY_GAMING_MODE = booleanPreferencesKey("gaming_mode")
        val KEY_AUTO_START = booleanPreferencesKey("auto_start_reboot")
        val KEY_FAVORITE_PACKAGES = stringSetPreferencesKey("favorite_packages")
    }

    val isServiceEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SERVICE_ENABLED] ?: false }
    val handlePositionY: Flow<Float> = context.dataStore.data.map { it[KEY_HANDLE_POSITION_Y] ?: 0.5f }
    val isRightSide: Flow<Boolean> = context.dataStore.data.map { it[KEY_PANEL_SIDE_RIGHT] ?: true }
    val handleWidth: Flow<Int> = context.dataStore.data.map { it[KEY_HANDLE_WIDTH] ?: 18 }
    val handleHeight: Flow<Int> = context.dataStore.data.map { it[KEY_HANDLE_HEIGHT] ?: 80 }
    val handleOpacity: Flow<Float> = context.dataStore.data.map { it[KEY_HANDLE_OPACITY] ?: 0.6f }
    val isVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_VIBRATION_ENABLED] ?: true }
    val isGamingMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_GAMING_MODE] ?: false }
    val isAutoStartEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_START] ?: false }
    val favoritePackages: Flow<Set<String>> = context.dataStore.data.map { it[KEY_FAVORITE_PACKAGES] ?: emptySet() }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SERVICE_ENABLED] = enabled }
    }

    suspend fun setHandlePositionY(ratio: Float) {
        context.dataStore.edit { it[KEY_HANDLE_POSITION_Y] = ratio.coerceIn(0.1f, 0.9f) }
    }

    suspend fun setPanelSideRight(isRight: Boolean) {
        context.dataStore.edit { it[KEY_PANEL_SIDE_RIGHT] = isRight }
    }

    suspend fun setHandleWidth(widthDp: Int) {
        context.dataStore.edit { it[KEY_HANDLE_WIDTH] = widthDp }
    }

    suspend fun setHandleHeight(heightDp: Int) {
        context.dataStore.edit { it[KEY_HANDLE_HEIGHT] = heightDp }
    }

    suspend fun setHandleOpacity(opacity: Float) {
        context.dataStore.edit { it[KEY_HANDLE_OPACITY] = opacity }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun setGamingMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_GAMING_MODE] = enabled }
    }

    suspend fun setAutoStartEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_START] = enabled }
    }

    suspend fun toggleFavorite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_PACKAGES] ?: emptySet()
            if (current.contains(packageName)) {
                prefs[KEY_FAVORITE_PACKAGES] = current - packageName
            } else {
                prefs[KEY_FAVORITE_PACKAGES] = current + packageName
            }
        }
    }
}
