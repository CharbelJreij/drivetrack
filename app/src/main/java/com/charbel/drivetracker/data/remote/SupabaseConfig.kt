package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.BuildConfig

data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    val isConfigured: Boolean
        get() = normalizedBaseUrl != null && anonKey.isNotBlank()

    val normalizedBaseUrl: String?
        get() = url
            .trim()
            .takeIf { value ->
                value.startsWith("https://") || value.startsWith("http://")
            }
            ?.let { value ->
                if (value.endsWith("/")) value else "$value/"
            }

    companion object {
        fun fromBuildConfig(): SupabaseConfig = SupabaseConfig(
            url = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
}
