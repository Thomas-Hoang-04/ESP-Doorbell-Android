package com.thomas.doorbell.dto

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: UUID,
    val email: String,
    val username: String?,
    @SerializedName("active")
    val isActive: Boolean = true,
    @SerializedName("email_verified")
    val isEmailVerified: Boolean = false,
    @SerializedName("notification_enabled")
    val notificationEnabled: Boolean = true,
    @SerializedName("last_login")
    val lastLoginAt: String? = null,
    @SerializedName("device_access")
    val deviceAccess: List<UserDeviceAccess> = emptyList()
)

data class UserDeviceAccess(
    @SerializedName("user_id")
    val userId: UUID,
    @SerializedName("device_id")
    val deviceId: UUID,
    @SerializedName("role")
    val roleCode: String,
    @SerializedName("role_label")
    val roleLabel: String,
    @SerializedName("access_status")
    val accessStatusCode: String,
    @SerializedName("access_status_label")
    val accessStatusLabel: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("updated_by")
    val updatedByUserId: UUID?,
    @SerializedName("updated_by_name")
    val updatedByUsername: String?
)

data class RegisterRequest(
    val username: String?,
    val email: String,
    val password: String
)

data class AvailabilityResponse(
    val available: Boolean
)