package com.thomas.doorbell.network

import com.thomas.doorbell.dto.AvailabilityResponse
import com.thomas.doorbell.dto.LoginRequest
import com.thomas.doorbell.dto.LoginResponse
import com.thomas.doorbell.dto.RegisterRequest
import com.thomas.doorbell.dto.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthInterface {
    @POST("api/auth/login")
    suspend fun login(@Body login: LoginRequest): Response<LoginResponse>

    @POST("api/auth/signup")
    suspend fun signup(@Body request: RegisterRequest): Response<User>

    @GET("api/auth/check-username")
    suspend fun checkUsernameAvailability(@Query("username") username: String): Response<AvailabilityResponse>

    @GET("api/auth/check-email")
    suspend fun checkEmailAvailability(@Query("email") email: String): Response<AvailabilityResponse>

    @GET("api/auth/check-exist")
    suspend fun checkLoginAvailability(@Query("login") login: String): Response<AvailabilityResponse>
}
