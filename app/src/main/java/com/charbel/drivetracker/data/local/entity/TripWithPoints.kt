package com.charbel.drivetracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TripWithPoints(
    @Embedded
    val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId",
    )
    val points: List<TripPointEntity>,
)

