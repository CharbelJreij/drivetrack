package com.charbel.drivetracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenRouteServiceReverseResponseDto(
    @SerializedName("features")
    val features: List<OpenRouteServiceFeatureDto> = emptyList(),
)

data class OpenRouteServiceFeatureDto(
    @SerializedName("properties")
    val properties: OpenRouteServicePropertiesDto? = null,
)

data class OpenRouteServicePropertiesDto(
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("street")
    val street: String? = null,
    @SerializedName("locality")
    val locality: String? = null,
    @SerializedName("region")
    val region: String? = null,
    @SerializedName("country")
    val country: String? = null,
)

fun OpenRouteServicePropertiesDto.formattedLabel(): String? {
    val directLabel = label?.trim().orEmpty()
    if (directLabel.isNotEmpty()) return directLabel

    return listOf(
        name,
        street,
        locality,
        region,
        country,
    )
        .mapNotNull { value -> value?.trim()?.takeIf(String::isNotEmpty) }
        .distinct()
        .joinToString(separator = ", ")
        .ifBlank { null }
}
