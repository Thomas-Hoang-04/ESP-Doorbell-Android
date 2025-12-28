package com.thomas.doorbell.dto

enum class DeviceAccess {
    GRANTED,
    REVOKED,
    EXPIRED
}

enum class EventType {
    DOORBELL_RING,
    MOTION_DETECTED,
    LIVE_VIEW
}

enum class ResponseType {
    ANSWERED,
    MISSED,
    DECLINED,
    PENDING
}

enum class StreamStatus {
    STREAMING,
    PROCESSING,
    COMPLETED,
    FAILED
}

enum class UserDeviceRole {
    OWNER,
    MEMBER
}
