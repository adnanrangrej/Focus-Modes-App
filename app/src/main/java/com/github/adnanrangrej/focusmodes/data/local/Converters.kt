package com.github.adnanrangrej.focusmodes.data.local

import androidx.room.TypeConverter
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import com.github.adnanrangrej.focusmodes.domain.model.TriggerConfig
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.serialization.json.Json

class Converters {

    private val triggerConfigJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        classDiscriminator = "configType"
    }

    @TypeConverter
    fun dateToTimestamp(value: LocalDateTime?): Long? {
        return value?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun timestampToDate(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(
                it,
                0,
                ZoneOffset.UTC
            )
        }
    }

    @TypeConverter
    fun sessionOutcomeToString(value: SessionOutcome?): String? {
        return value?.name
    }

    @TypeConverter
    fun stringToSessionOutcome(value: String?): SessionOutcome? {
        return value?.let {
            SessionOutcome.valueOf(it)
        }
    }

    @TypeConverter
    fun stringListToString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun stringToStringList(value: String?): List<String> {
        return value?.split(",") ?: emptyList()
    }

    @TypeConverter
    fun triggerTypeToString(value: TriggerType?): String? {
        return value?.name
    }

    @TypeConverter
    fun stringToTriggerType(value: String?): TriggerType? {
        return value?.let { TriggerType.valueOf(it) }
    }

    @TypeConverter
    fun triggerConfigToString(config: TriggerConfig?): String? {
        return config?.let {
            triggerConfigJson.encodeToString(TriggerConfig.serializer(), it)
        }
    }

    @TypeConverter
    fun stringToTriggerConfig(value: String?): TriggerConfig? {
        return value?.let {
            triggerConfigJson.decodeFromString(TriggerConfig.serializer(), it)
        }
    }
}
