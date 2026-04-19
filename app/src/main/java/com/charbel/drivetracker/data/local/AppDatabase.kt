package com.charbel.drivetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.charbel.drivetracker.data.local.dao.TripDao
import com.charbel.drivetracker.data.local.entity.TripEntity
import com.charbel.drivetracker.data.local.entity.TripPointEntity

@Database(
    entities = [TripEntity::class, TripPointEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    companion object {
        const val DATABASE_NAME = "drive_tracker.db"
    }
}
