package com.github.adnanrangrej.focusmodes.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TriggerType {
    TIME,
    LOCATION,
    WIFI
}

@Serializable
sealed interface TriggerConfig {

    @Serializable
    @SerialName("time")
    data class TimeConfig(
        val startMinutes: Int,
        val endMinutes: Int,
        val daysOfWeek: List<Int>
    ) : TriggerConfig

    @Serializable
    @SerialName("location")
    data class LocationConfig(
        val latitude: Double,
        val longitude: Double,
        val radiusMeters: Float,
        val activateOnEntry: Boolean
    ) : TriggerConfig

    @Serializable
    @SerialName("wifi")
    data class WifiConfig(
        val ssid: String,
        val activateOnConnect: Boolean
    ) : TriggerConfig
}

data class Trigger(
    val id: Long = 0,
    val name: String,
    val type: TriggerType,
    val focusModeId: Long,
    val isEnabled: Boolean,
    val config: TriggerConfig
)
