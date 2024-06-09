package com.isyaratku.app.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {


    @POST("register")
    suspend fun register(
        @Body jsonObject: JsonObject
    ): ResponseBody


    @POST("login")
    suspend fun login(
        @Body jsonObject: JsonObject
    ): LoginResponse

    @GET("user-info")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ) : UserInfoResponse

    @GET("leaderboard")
    suspend fun getLeaderboard(
    ) : LeaderboardResponse

    @PUT("change-username")
    suspend fun changeUsername(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ) : MessageResponse

    @PUT("change-email")
    suspend fun changeEmail(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ) : MessageResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): MessageResponse


}