package com.github.adnanrangrej.focusmodes.ui.utils

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.Settings
import android.text.TextUtils

fun canDrawOverlays(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun isAccessibilityServiceEnabled(
    context: Context,
    serviceClass: Class<out AccessibilityService>
): Boolean {

    // Expected component name
    val serviceId = "${context.packageName}/${serviceClass.canonicalName}"

    // Get the list of all enabled accessibility services
    val settingValues = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )

    // Check if expected component name is in the list of enabled accessibility services
    return settingValues?.let {
        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(it)
        while (splitter.hasNext()) {
            if (splitter.next().equals(serviceId, ignoreCase = true)) {
                return@let true
            }
        }
        false
    } ?: false
}