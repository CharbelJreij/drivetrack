package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseServiceFactory {

    fun createApi(config: SupabaseConfig): SupabaseApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val headersInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("apikey", config.anonKey)
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(config.normalizedBaseUrl ?: "https://example.supabase.co/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
