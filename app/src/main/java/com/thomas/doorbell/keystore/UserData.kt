package com.thomas.doorbell.keystore

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String? = null,
    val password: String? = null,
)
