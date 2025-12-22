package com.thomas.doorbell.dto

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Event(
    val id: UUID,
    @SerializedName("device_id")
    val deviceId: UUID,
    @SerializedName("occurred_at")
    val occurredAt: String,
    @SerializedName("event_code")
    val eventTypeCode: String,
    @SerializedName("event_label")
    val eventTypeLabel: String,
    @SerializedName("response_code")
    val responseTypeCode: String,
    @SerializedName("response_label")
    val responseTypeLabel: String,
    @SerializedName("response_timestamp")
    val responseTimestamp: String?,
    @SerializedName("responder_user_id")
    val responderUserId: UUID?,
    @SerializedName("responder_display_name")
    val responderDisplayName: String?,
    @SerializedName("stream_status_code")
    val streamStatusCode: String?,
    @SerializedName("stream_status_label")
    val streamStatusLabel: String?,
    @SerializedName("stream_started")
    val streamStartedAt: String?,
    @SerializedName("stream_ended")
    val streamEndedAt: String?,
    @SerializedName("duration")
    val durationSeconds: Int?
)
