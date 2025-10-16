package com.github.adnanrangrej.focusmodes.data.triggers

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.github.adnanrangrej.focusmodes.domain.model.Trigger
import com.github.adnanrangrej.focusmodes.domain.model.TriggerConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiTriggerEvaluator @Inject constructor(
    private val application: Application,
    private val executionManager: TriggerExecutionManager
) {

    suspend fun evaluate(trigger: Trigger) {
        val config = trigger.config as? TriggerConfig.WifiConfig ?: return
        val currentSsid = currentSsid() ?: ""
        val isConnected = currentSsid.equals(config.ssid, ignoreCase = false)

        val shouldActivate = if (config.activateOnConnect) {
            isConnected
        } else {
            !isConnected
        }

        if (shouldActivate) {
            executionManager.activate(trigger)
        } else {
            executionManager.deactivate(trigger)
        }
    }

    private fun currentSsid(): String? {
        val connectivityManager = application.getSystemService(ConnectivityManager::class.java)
            ?: return null
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return null

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return null
        }

        val wifiInfo = (capabilities.transportInfo as? WifiInfo) ?: legacyWifiInfo()
            ?: return null

        val ssid = wifiInfo.ssid ?: return null
        return ssid.trim('"')
    }

    private fun legacyWifiInfo(): WifiInfo? {
        val wifiManager = application.applicationContext.getSystemService(WifiManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            null
        } else {
            @Suppress("DEPRECATION")
            wifiManager?.connectionInfo
        }
    }
}
