package com.thomas.doorbell.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomas.doorbell.dto.LoginRequest
import com.thomas.doorbell.dto.LoginResponse
import com.thomas.doorbell.dto.OTPPurpose
import com.thomas.doorbell.dto.OTPRequest
import com.thomas.doorbell.dto.OTPResponse
import com.thomas.doorbell.dto.OTPStatus
import com.thomas.doorbell.dto.OTPValidationRequest
import com.thomas.doorbell.dto.User
import com.thomas.doorbell.keystore.TokenManager
import com.thomas.doorbell.keystore.UserData
import com.thomas.doorbell.nav.NavRoute
import com.thomas.doorbell.network.AuthInterface
import com.thomas.doorbell.network.NetworkMonitor
import com.thomas.doorbell.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStore: DataStore<UserData>,
    private val authInterface: AuthInterface,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor
): ViewModel() {
    private val _startDestination = MutableStateFlow<NavRoute>(NavRoute.Auth.Login)
    val startDestination = _startDestination.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified = _isVerified.asStateFlow()

    private val _verifiedButLoginFailed = MutableStateFlow(false)
    val verifiedButLoginFailed = _verifiedButLoginFailed.asStateFlow()

    val isOnline = networkMonitor.isOnline.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    fun updateUser(user: User) = _user.update { user }

    fun failedLogin() = _verifiedButLoginFailed.update { true }

    fun requestOTP(
        otpReq: OTPRequest,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) = viewModelScope.launch {
            val req = authInterface.sendOTP(otpReq)
            if (req.isSuccessful) {
                val res = req.body() as OTPResponse
                if (res.status == OTPStatus.SUCCESS) onSuccess()
                else onFailed()
            } else onFailed()
        }

    fun verifyOTP(
        email: String,
        otp: String,
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit,
        context: Context
    ) = viewModelScope.launch {
        val req = authInterface.validateOTP(OTPValidationRequest(email, otp))
        if (req.isSuccessful) {
            val res = req.body() as OTPResponse
            when (res.status) {
                OTPStatus.SUCCESS -> {
                    _isVerified.update { true }
                    onSuccess()
                }
                OTPStatus.INVALID -> onFailed(context.getString(R.string.otp_not_match))
                OTPStatus.EXPIRED -> onFailed(context.getString(R.string.otp_expired))
                else -> onFailed(context.getString(R.string.error))
            }
        } else onFailed(context.getString(R.string.error))
    }

    private suspend fun checkEmailVerified(
        verified: Boolean,
        username: String?,
        email: String,
    ) {
        if (verified) {
            _startDestination.update { NavRoute.Home }
            delay(100)
            _isReady.value = true
        }
        else {
            val req = authInterface.sendOTP(
                OTPRequest(
                    user.value!!.username,
                    user.value!!.email,
                    OTPPurpose.VERIFY_EMAIL
                )
            )
            if (req.isSuccessful) {
                val res = req.body() as OTPResponse
                if (res.status == OTPStatus.SUCCESS) {
                    _startDestination.update { NavRoute.Auth.OTP(username, email) }
                    delay(100)
                    _isReady.value = true
                }
            } // TODO: implement else
        }
    }

    fun load()
        = viewModelScope.launch {
            if (networkMonitor.isOnline.first()) {
                val account = dataStore.data.catch { e ->
                    e.printStackTrace()
                    emit(UserData())
                }.first()
                if (account.username == null || account.password == null) {
                    delay(100)
                    _isReady.value = true
                } else {
                    val req = authInterface.login(
                        LoginRequest(
                            account.username,
                            account.password
                        )
                    )
                    if (req.isSuccessful) {
                        val body = req.body() as LoginResponse
                        tokenManager.setToken(body.token)
                        _user.update { body.user }
                        checkEmailVerified(
                            user.value!!.isEmailVerified,
                            user.value!!.username,
                            user.value!!.email
                        )
                    } else {
                        delay(100)
                        _isReady.value = true
                    }
                }
            } else {
                delay(100)
                _isReady.value = true
            }
        }
}