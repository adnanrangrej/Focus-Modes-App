package com.github.adnanrangrej.focusmodes.data.local

import androidx.room.TypeConverter
import com.github.adnanrangrej.focusmodes.domain.model.SessionOutcome
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

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
}