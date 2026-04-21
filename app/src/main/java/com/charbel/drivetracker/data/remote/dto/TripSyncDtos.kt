package com.charbel.drivetracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RemoteTripInsertDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("client_local_id")
    val clientLocalId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("started_at_millis")
    val startedAtMillis: Long,
    @SerializedName("ended_at_millis")
    val endedAtMillis: Long,
    @SerializedName("distance_meters")
    val distanceMeters: Double,
    @SerializedName("duration_seconds")
    val durationSeconds: Long,
    @SerializedName("average_speed_kmh")
    val averageSpeedKmh: Double,
    @SerializedName("max_speed_kmh")
    val maxSpeedKmh: Double,
    @SerializedName("start_address")
    val startAddress: String?,
    @SerializedName("end_address")
    val endAddress: String?,
)

data class RemoteTripRecordDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("client_local_id")
    val clientLocalId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("started_at_millis")
    val startedAtMillis: Long,
    @SerializedName("ended_at_millis")
    val endedAtMillis: Long,
    @SerializedName("distance_meters")
    val distanceMeters: Double,
    @SerializedName("duration_seconds")
    val durationSeconds: Long,
    @SerializedName("average_speed_kmh")
    val averageSpeedKmh: Double,
    @SerializedName("max_speed_kmh")
    val maxSpeedKmh: Double,
    @SerializedName("start_address")
    val startAddress: String?,
    @SerializedName("end_address")
    val endAddress: String?,
)

data class RemoteTripPointInsertDto(
    @SerializedName("trip_id")
    val tripId: String,
    @SerializedName("sequence_index")
    val sequenceIndex: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("recorded_at_millis")
    val recordedAtMillis: Long,
    @SerializedName("speed_meters_per_second")
    val speedMetersPerSecond: Float,
)

data class RemoteTripPointRecordDto(
    @SerializedName("trip_id")
    val tripId: String,
    @SerializedName("sequence_index")
    val sequenceIndex: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("recorded_at_millis")
    val recordedAtMillis: Long,
    @SerializedName("speed_meters_per_second")
    val speedMetersPerSecond: Float,
)
