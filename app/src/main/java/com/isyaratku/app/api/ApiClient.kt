package com.isyaratku.app.api

import com.isyaratku.app.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val NEWS_URL = BuildConfig.NEWS_URL


object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(NEWS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiNewsService: ApiService = retrofit.create(ApiService::class.java)
    const val API_KEY = BuildConfig.API_KEY

}