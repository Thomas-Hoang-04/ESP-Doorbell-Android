package com.thomas.doorbell.network

import com.thomas.doorbell.dto.Device
import com.thomas.doorbell.dto.DeviceAccessRequest
import com.thomas.doorbell.dto.DeviceRegisterRequest
import com.thomas.doorbell.dto.DeviceUpdateRequest
import com.thomas.doorbell.dto.Event
import com.thomas.doorbell.dto.User
import com.thomas.doorbell.dto.UserDeviceAccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface APIInterface {

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<User>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: UUID): Response<User>

    @GET("api/users/{id}/devices")
    suspend fun listUserDevices(@Path("id") id: UUID): Response<List<UserDeviceAccess>>

    @GET("api/devices")
    suspend fun listDevices(): Response<List<Device>>

    @GET("api/devices/active")
    suspend fun listActiveDevices(): Response<List<Device>>

    @GET("api/devices/{id}")
    suspend fun getDevice(@Path("id") id: UUID): Response<Device>

    @POST("api/devices")
    suspend fun createDevice(@Body request: DeviceRegisterRequest): Response<Device>

    @PATCH("api/devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: UUID,
        @Body request: DeviceUpdateRequest
    ): Response<Device>

    @DELETE("api/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: UUID): Response<Unit>

    @GET("api/devices/{id}/access")
    suspend fun listDeviceAccess(@Path("id") id: UUID): Response<List<UserDeviceAccess>>

    @POST("api/devices/{id}/access")
    suspend fun grantDeviceAccess(
        @Path("id") id: UUID,
        @Body request: DeviceAccessRequest
    ): Response<UserDeviceAccess>

    @GET("api/events")
    suspend fun listEvents(): Response<List<Event>>

    @GET("api/events/device/{deviceId}")
    suspend fun listEventsByDevice(@Path("deviceId") deviceId: UUID): Response<List<Event>>

    @GET("api/events/recent")
    suspend fun listRecentEvents(@Query("limit") limit: Int = 10): Response<List<Event>>

    @GET("api/events/{id}")
    suspend fun getEvent(@Path("id") id: UUID): Response<Event>
}