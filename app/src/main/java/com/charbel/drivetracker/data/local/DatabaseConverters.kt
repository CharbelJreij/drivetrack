package com.charbel.drivetracker.data.local

import androidx.room.TypeConverter
import com.charbel.drivetracker.model.SyncStatus

class DatabaseConverters {

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}

