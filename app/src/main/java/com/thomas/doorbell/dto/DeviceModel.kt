package com.thomas.doorbell.dto

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Device(
    val id: UUID,
    @SerializedName("device_id")
    val deviceIdentifier: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("location")
    val locationDescription: String?,
    @SerializedName("model")
    val modelName: String?,
    @SerializedName("fw_ver")
    val firmwareVersion: String?,
    val active: Boolean,
    @SerializedName("battery_level")
    val batteryLevelPercent: Int,
    @SerializedName("signal_strength")
    val signalStrengthDbm: Int?,
    @SerializedName("last_online")
    val lastOnlineAt: String?
)

data class DeviceRegisterRequest(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("display_name")
    val displayName: String,
    val location: String?,
    val model: String?,
    @SerializedName("fw_ver")
    val firmwareVer: String?
)

data class DeviceUpdateRequest(
    @SerializedName("display_name")
    val displayName: String?,
    val location: String?,
    val model: String?,
    @SerializedName("fw_ver")
    val firmwareVer: String?
)

data class DeviceAccessRequest(
    @SerializedName("user_id")
    val userId: UUID,
    val role: String
)
