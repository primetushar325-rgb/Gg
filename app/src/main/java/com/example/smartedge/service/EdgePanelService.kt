package com.example.smartedge.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smartedge.MainApplication
import com.example.smartedge.R
import com.example.smartedge.data.AppRepository
import com.example.smartedge.data.PreferencesRepository
import com.example.smartedge.ui.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine

class EdgePanelService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var appRepository: AppRepository
    private lateinit var overlayManager: OverlayManager

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(applicationContext)
        appRepository = AppRepository(applicationContext, preferencesRepository)
        overlayManager = OverlayManager(applicationContext, preferencesRepository, appRepository)

        startForegroundServiceNotification()
        observePreferences()
    }

    private fun startForegroundServiceNotification() {
        val openIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, MainApplication.CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                MainApplication.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(MainApplication.NOTIFICATION_ID, notification)
        }
    }

    private fun observePreferences() {
        serviceScope.launch {
            combine(
                preferencesRepository.isRightSide,
                preferencesRepository.handleWidth,
                preferencesRepository.handleHeight,
                preferencesRepository.handlePositionY,
                preferencesRepository.handleOpacity
            ) { isRight, width, height, posY, opacity ->
                OverlayParams(isRight, width, height, posY, opacity)
            }.collect { params ->
                overlayManager.showEdgeHandle(
                    isRight = params.isRight,
                    widthDp = params.width,
                    heightDp = params.height,
                    posYRatio = params.posY,
                    opacity = params.opacity
                )
            }
        }
    }

    private data class OverlayParams(
        val isRight: Boolean,
        val width: Int,
        val height: Int,
        val posY: Float,
        val opacity: Float
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.removeAll()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP_SERVICE = "com.example.smartedge.STOP_SERVICE"
    }
}
