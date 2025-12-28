package com.thomas.doorbell.dto

enum class OTPPurpose { RESET_PASSWORD, VERIFY_EMAIL }

enum class OTPStatus { SUCCESS, FAILED, INVALID, EXPIRED, TOO_MANY_REQUESTS }

data class OTPRequest(
    val username: String?,
    val email: String,
    val purpose: OTPPurpose
)

data class OTPValidationRequest(
    val email: String,
    val otp: String
)

data class OTPResponse(
    val status: OTPStatus,
    val message: String
)
