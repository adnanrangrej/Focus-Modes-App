package com.github.adnanrangrej.focusmodes.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.github.adnanrangrej.focusmodes.MainActivity
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeByIdUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.GetFocusModeIdUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.ObserveFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.SetFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.ui.blockingoverlay.BlockingOverlay
import com.github.adnanrangrej.focusmodes.ui.blockingoverlay.OverlayLifecycleOwner
import com.github.adnanrangrej.focusmodes.ui.theme.FocusModesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockerService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @Inject
    lateinit var observeFocusModeActive: ObserveFocusModeActiveUseCase

    @Inject
    lateinit var getFocusModeId: GetFocusModeIdUseCase

    @Inject
    lateinit var getFocusModeById: GetFocusModeByIdUseCase

    @Inject
    lateinit var setFocusModeActiveUseCase: SetFocusModeActiveUseCase

    private lateinit var windowManager: WindowManager

    private var overlayView: View? = null

    private var overlayLifecycleOwner: OverlayLifecycleOwner? = null

    private val blockedAppList = mutableListOf<String>()

    private var isFocusModeActive = false


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (isFocusModeActive) {
            val packageName = event?.packageName.toString()
            if (blockedAppList.contains(packageName)) {
                Log.d("AppBlockerService", "Blocked app detected: $packageName")
                showOverlay(packageName)
            }
        }
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AppBlockerService", "Service connected")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppBlockerService", "Service created")

        Log.d("AppBlockerService", "Initializing window manager")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        Log.d("AppBlockerService", "Starting to observe focus state")
        startObservingFocusState()
    }

    override fun onDestroy() {
        scope.cancel()
        hideOverlay()
        setFocusModeActiveUseCase(false, null)
        Log.d("AppBlockerService", "Service destroyed")
        super.onDestroy()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startObservingFocusState() {
        scope.launch {
            observeFocusModeActive()
                .flatMapLatest { isActive ->
                    if (isActive) {
                        val modeId = getFocusModeId()
                        if (modeId != null) {
                            getFocusModeById(modeId) ?: flowOf(null)
                        } else {
                            flowOf(null)
                        }
                    } else {
                        flowOf(null)
                    }

                }.collect { activeMode ->
                    if (activeMode != null) {
                        isFocusModeActive = true
                        Log.d("AppBlockerService", "Focus mode is active")
                        blockedAppList.clear()
                        blockedAppList.addAll(activeMode.blockedAppPackages)
                        Log.d("AppBlockerService", "Blocked apps: $blockedAppList")
                    } else {
                        isFocusModeActive = false
                        blockedAppList.clear()
                        hideOverlay()
                        Log.d("AppBlockerService", "Focus mode deactivated")
                    }
                }
        }
    }

    private fun showOverlay(packageName: String) {
        if (overlayView != null) return

        // Get app name and icon
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = applicationInfo.loadLabel(packageManager).toString()
        val appIcon = applicationInfo.loadIcon(packageManager)

        overlayLifecycleOwner = OverlayLifecycleOwner()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        overlayView = ComposeView(this).apply {

            overlayLifecycleOwner?.let { owner ->
                owner.onCreate()
                setViewTreeLifecycleOwner(owner)
                setViewTreeViewModelStoreOwner(owner)
                setViewTreeSavedStateRegistryOwner(owner)
            }
            setContent {
                FocusModesTheme {
                    BlockingOverlay(
                        blockedAppIcon = appIcon,
                        blockedAppName = appName,
                        onGoBack = {
                            hideOverlay()
                            val intent =
                                Intent(this@AppBlockerService, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
        windowManager.addView(overlayView, params)
        overlayLifecycleOwner?.onResume()
        Log.d("AppBlockerService", "Overlay shown")

    }

    private fun hideOverlay() {
        overlayView?.let {
            windowManager.removeView(overlayView)
            overlayLifecycleOwner?.onDestroy()
            Log.d("AppBlockerService", "Overlay hidden")
        }
        overlayView = null
        overlayLifecycleOwner = null
    }
}