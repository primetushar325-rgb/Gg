package com.example.smartedge.service

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.smartedge.data.AppRepository
import com.example.smartedge.data.PreferencesRepository
import com.example.smartedge.floating.FloatingModeManager
import com.example.smartedge.ui.EdgeHandleView
import com.example.smartedge.ui.PanelOverlayView

class OverlayManager(
    private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val appRepository: AppRepository
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val floatingModeManager = FloatingModeManager(context)

    private var handleView: ComposeView? = null
    private var panelView: ComposeView? = null

    private var isPanelOpen = false
    private var isFlashlightOn = false

    private val lifecycleRegistry = androidx.lifecycle.LifecycleRegistry(this)
    private val viewModelStoreInstance = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStoreInstance
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun showEdgeHandle(
        isRight: Boolean = true,
        widthDp: Int = 18,
        heightDp: Int = 80,
        posYRatio: Float = 0.5f,
        opacity: Float = 0.6f
    ) {
        if (handleView != null) removeHandle()

        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        val widthPx = (widthDp * density).toInt()
        val heightPx = (heightDp * density).toInt()
        val yOffsetPx = ((posYRatio - 0.5f) * displayMetrics.heightPixels).toInt()

        val layoutParams = WindowManager.LayoutParams(
            widthPx,
            heightPx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = if (isRight) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
            y = yOffsetPx
        }

        handleView = ComposeView(context).apply {
            initComposeView(this)
            setContent {
                EdgeHandleView(
                    isRight = isRight,
                    opacity = opacity,
                    onSwipeToOpen = {
                        triggerHaptic()
                        showPanel(isRight)
                    },
                    onDragY = { newRatio ->
                        // Dynamic Y position update saved via repository
                    }
                )
            }
        }

        try {
            windowManager.addView(handleView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showPanel(isRight: Boolean) {
        if (isPanelOpen) return

        val displayMetrics = context.resources.displayMetrics
        val panelWidth = (displayMetrics.widthPixels * 0.42f).toInt().coerceAtMost((280 * displayMetrics.density).toInt())

        val layoutParams = WindowManager.LayoutParams(
            panelWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = if (isRight) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
        }

        panelView = ComposeView(context).apply {
            initComposeView(this)
            setContent {
                PanelOverlayView(
                    appRepository = appRepository,
                    onAppClick = { packageName ->
                        triggerHaptic()
                        floatingModeManager.launchApp(packageName)
                        hidePanel()
                    },
                    onToolClick = { toolId ->
                        triggerHaptic()
                        handleQuickTool(toolId)
                        hidePanel()
                    },
                    onClose = { hidePanel() }
                )
            }
        }

        try {
            windowManager.addView(panelView, layoutParams)
            isPanelOpen = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hidePanel() {
        panelView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {}
            panelView = null
            isPanelOpen = false
        }
    }

    private fun removeHandle() {
        handleView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {}
            handleView = null
        }
    }

    fun removeAll() {
        hidePanel()
        removeHandle()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun handleQuickTool(toolId: String) {
        when (toolId) {
            "flashlight" -> toggleFlashlight()
            "settings" -> {
                val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            "calculator" -> {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_CALCULATOR)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Calculator app not found", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Toast.makeText(context, "Quick Tool executed: $toolId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFlashlight() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        try {
            val cameraId = cameraManager.cameraIdList[0]
            isFlashlightOn = !isFlashlightOn
            cameraManager.setTorchMode(cameraId, isFlashlightOn)
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(20)
                }
            }
        } catch (_: Exception) {}
    }

    private fun initComposeView(view: View) {
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)
    }
}
