package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenRouteServiceFactory {

    fun createApi(config: OpenRouteServiceConfig): OpenRouteServiceApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .header("Accept", "application/json")

            val request = if (config.isConfigured) {
                val updatedUrl = chain.request().url.newBuilder()
                    .addQueryParameter("api_key", config.apiKey)
                    .build()
                requestBuilder.url(updatedUrl).build()
            } else {
                requestBuilder.build()
            }

            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(OpenRouteServiceConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouteServiceApi::class.java)
    }
}
