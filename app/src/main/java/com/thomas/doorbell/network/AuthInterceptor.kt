package com.thomas.doorbell.network

import com.thomas.doorbell.keystore.TokenManager
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    private val excludePaths = listOf("/auth/login", "/auth/register", "/otp/send", "/otp/verify")

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        if (excludePaths.any { req.url.encodedPath.contains(it) }) {
            return chain.proceed(req)
        }

        val reqWithAuth = req.newBuilder().apply {
            addHeader("Authorization", "Bearer ${tokenManager.token}")
            addHeader("Content-Type", "application/json")
        }.build()

        return chain.proceed(reqWithAuth)
    }
}