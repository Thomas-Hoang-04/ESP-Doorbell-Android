package com.thomas.doorbell.keystore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    fun setToken(token: String) {
        _token.value = token
    }
}