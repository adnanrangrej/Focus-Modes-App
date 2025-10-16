package com.github.adnanrangrej.focusmodes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.adnanrangrej.focusmodes.domain.model.TriggerConfig
import com.github.adnanrangrej.focusmodes.domain.model.TriggerType

@Entity(
    tableName = "smart_trigger",
    foreignKeys = [
        ForeignKey(
            entity = FocusModeEntity::class,
            parentColumns = ["id"],
            childColumns = ["focusModeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("focusModeId"),
        Index("type")
    ]
)
data class TriggerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: TriggerType,
    val focusModeId: Long,
    val isEnabled: Boolean,
    val config: TriggerConfig
)
