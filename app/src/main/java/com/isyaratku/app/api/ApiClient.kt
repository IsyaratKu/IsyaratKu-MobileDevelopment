package com.isyaratku.app.api

import com.isyaratku.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val NEWS_URL = BuildConfig.NEWS_URL


object ApiClient {

    val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val authInterceptor = Interceptor { chain ->
        val req = chain.request()
        val requestHeaders = req.newBuilder()
            .build()
        chain.proceed(requestHeaders)
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(NEWS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiNewsService: ApiService = retrofit.create(ApiService::class.java)
    const val API_KEY = BuildConfig.API_KEY

}