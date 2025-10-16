package com.github.adnanrangrej.focusmodes.data.local.mapper

import com.github.adnanrangrej.focusmodes.data.local.entity.TriggerEntity
import com.github.adnanrangrej.focusmodes.domain.model.Trigger

fun TriggerEntity.toDomainModel(): Trigger {
    return Trigger(
        id = id,
        name = name,
        type = type,
        focusModeId = focusModeId,
        isEnabled = isEnabled,
        config = config
    )
}

fun Trigger.toEntityModel(): TriggerEntity {
    return TriggerEntity(
        id = id,
        name = name,
        type = type,
        focusModeId = focusModeId,
        isEnabled = isEnabled,
        config = config
    )
}
