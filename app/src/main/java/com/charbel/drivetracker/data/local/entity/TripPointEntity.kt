package com.charbel.drivetracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "trip_points",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["tripId"])],
)
data class TripPointEntity(
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAtMillis: Long,
    val speedMetersPerSecond: Float,
    val sequenceIndex: Int,
    @androidx.room.PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
)

