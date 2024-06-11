package com.isyaratku.app.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {


    @POST("auth/register")
    suspend fun register(
        @Body jsonObject: JsonObject
    ): ResponseBody

    @POST("auth/forgot-password")
    suspend fun passwordChange(
        @Body jsonObject: JsonObject
    ): ResponseBody

    @POST("auth/login")
    suspend fun login(
        @Body jsonObject: JsonObject
    ): LoginResponse

    @GET("auth/user-info")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ) : UserInfoResponse

    @GET("challenge/leaderboard")
    suspend fun getLeaderboard(
    ) : LeaderboardResponse

    @PUT("auth/change-photo")
    suspend fun changeProfPicture(
        @Header("Authorization") token: String,
        @Body file: MultipartBody
    ) : UploadPhotoResponse

    @PUT("auth/change-username")
    suspend fun changeUsername(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ) : MessageResponse

    @PUT("auth/change-email")
    suspend fun changeEmail(
        @Header("Authorization") token: String,
        @Body jsonObject: JsonObject
    ) : MessageResponse

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): MessageResponse


}