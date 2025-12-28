package com.thomas.doorbell.viewmodel

import android.content.Context
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomas.doorbell.R
import com.thomas.doorbell.dto.AvailabilityResponse
import com.thomas.doorbell.dto.LoginRequest
import com.thomas.doorbell.dto.LoginResponse
import com.thomas.doorbell.dto.OTPPurpose
import com.thomas.doorbell.dto.OTPRequest
import com.thomas.doorbell.dto.OTPResponse
import com.thomas.doorbell.dto.OTPStatus
import com.thomas.doorbell.dto.RegisterRequest
import com.thomas.doorbell.dto.User
import com.thomas.doorbell.keystore.TokenManager
import com.thomas.doorbell.keystore.UserData
import com.thomas.doorbell.network.AuthInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val dataStore: DataStore<UserData>,
    private val authInterface: AuthInterface,
    private val tokenManager: TokenManager,
): ViewModel() {
    val username = TextFieldState("")
    val pwd = TextFieldState("")
    val confirmPwd = TextFieldState("")
    val email = TextFieldState("")

    private val _usernameError = MutableStateFlow<Int?>(null)
    val usernameError = _usernameError.asStateFlow()
    private val _usernameTouched = MutableStateFlow(false)

    private val _passwordError = MutableStateFlow<Int?>(null)
    val passwordError = _passwordError.asStateFlow()
    private val _passwordTouched = MutableStateFlow(false)

    private val _confirmPwdError = MutableStateFlow<Int?>(null)
    val confirmPwdError = _confirmPwdError.asStateFlow()
    private val _confirmPwdTouched = MutableStateFlow(false)

    private val _emailError = MutableStateFlow<Int?>(null)
    val emailError = _emailError.asStateFlow()
    private val _emailTouched = MutableStateFlow(false)

    private val _register = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                snapshotFlow { username.text.toString() },
                _usernameTouched
            ) { usr, touched -> usr to touched }
                .debounce(150)
                .collect { (usr, touched) ->
                    if (touched)
                        validateUsername(usr)
                }
        }

        viewModelScope.launch {
            combine(
                snapshotFlow { pwd.text.toString() },
                _passwordTouched
            ) { pwd, touched -> pwd to touched }
                .debounce(150)
                .collect { (pwd, touched) ->
                    if (touched)
                        validatePassword(pwd)
                }
        }

        viewModelScope.launch {
            combine(
                snapshotFlow { email.text.toString() },
                _emailTouched
            ) { email, touched -> email to touched }
                .debounce(150)
                .collect { (email, touched) ->
                    if (touched)
                        validateEmail(email)
                }
        }

        viewModelScope.launch {
            combine(
                snapshotFlow { confirmPwd.text.toString() },
                _confirmPwdTouched
            ) { confirmPwd, touched -> confirmPwd to touched }
                .debounce(150)
                .collect { (confirmPwd, touched) ->
                    if (touched)
                        comparePassword(pwd.text.toString(), confirmPwd)
                }
        }
    }

    fun onUsernameFocusChange(focused: Boolean) {
        if (!focused) {
            _usernameTouched.update { true }
            validateUsername(username.text.toString())
        }
    }

    fun onPasswordFocusChange(focused: Boolean) {
        if (!focused) {
            _passwordTouched.update { true }
            validatePassword(pwd.text.toString())
        }
    }

    fun onEmailFocusChange(focused: Boolean) {
        if (!focused) {
            _emailTouched.update { true }
            validateEmail(email.text.toString())
        }
    }

    fun onConfirmPwdFocusChange(focused: Boolean) {
        if (!focused) {
            _confirmPwdTouched.update { true }
            comparePassword(pwd.text.toString(), confirmPwd.text.toString())
        }
    }

    private fun validateUsername(usr: String) {
        val error = when {
            usr.isNotBlank() && usr.length !in (3..50) -> R.string.username_length_error
            else -> null
        }

        _usernameError.update { error }
    }

    private fun validatePassword(pwd: String) {
        val error = when {
            pwd.isEmpty() -> R.string.password_empty_error
            pwd.length < 8 -> R.string.password_length_error
            else -> null
        }

        _passwordError.update { error }
    }

    private fun comparePassword(pwd: String, confirmPwd: String) {
        val error = when {
            confirmPwd.isNotBlank() && pwd != confirmPwd -> R.string.no_match_pwd
            else -> null
        }

        _confirmPwdError.update { error }
    }

    private fun validateEmail(email: String) {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        val error = when {
            email.isEmpty() -> R.string.email_empty_error
            !emailRegex.matches(email) -> R.string.email_format_error
            else -> null
        }

        _emailError.update { error }
    }

    private suspend fun validateAll(): Boolean = coroutineScope {
        validateUsername(username.text.toString())
        validateEmail(email.text.toString())

        if (_usernameError.value != null || _emailError.value != null)
            return@coroutineScope false

        val usernameCheck = async {
            val req = authInterface.checkUsernameAvailability(username.text.toString())
            if (req.isSuccessful)
                when ((req.body() as AvailabilityResponse).available) {
                    true -> _usernameError.update { null }
                    false -> _usernameError.update { R.string.user_exist_error }
                }
            else _usernameError.update { R.string.error }
        }

        val emailCheck = async {
            val req = authInterface.checkEmailAvailability(email.text.toString())
            if (req.isSuccessful)
                when ((req.body() as AvailabilityResponse).available) {
                    true -> _emailError.update { null }
                    false -> _emailError.update { R.string.email_exist_error }
                }
            else _emailError.update { R.string.error }
        }

        awaitAll(usernameCheck, emailCheck)

        validatePassword(pwd.text.toString())
        comparePassword(pwd.text.toString(), confirmPwd.text.toString())

        return@coroutineScope _usernameError.value == null &&
               _passwordError.value == null &&
               _confirmPwdError.value == null &&
               _emailError.value == null
    }

    fun registerAndVerify(
        context: Context,
        onSuccess: suspend (String?, String) -> Unit,
        onFailed: (String) -> Unit,
    ) = viewModelScope.launch {
            if (validateAll()) {
                if (!_register.value) {
                    val req = authInterface.signup(
                        RegisterRequest(
                            username.text.toString().takeIf(String::isNotBlank),
                            email.text.toString(),
                            pwd.text.toString()
                        )
                    )
                    if (req.isSuccessful) {
                        val res = req.body() as User
                        dataStore.updateData {
                            it.copy(
                                username = res.username ?: res.email,
                                password = pwd.text.toString()
                            )
                        }
                        _register.update { true }
                    } else {
                        onFailed(context.getString(R.string.error))
                        return@launch
                    }
                }
                val otpReq = authInterface.sendOTP(
                    OTPRequest(
                        username.text.toString().takeIf(String::isNotBlank),
                        email.text.toString(),
                        OTPPurpose.VERIFY_EMAIL
                    )
                )
                if (otpReq.isSuccessful) {
                    val otpRes = otpReq.body() as OTPResponse
                    if (otpRes.status == OTPStatus.SUCCESS)
                        onSuccess(
                            username.text.toString().takeIf(String::isNotBlank),
                            email.text.toString()
                        )
                    else onFailed(context.getString(R.string.otp_req_failed))
                } else onFailed(context.getString(R.string.error))
            }
        }

    fun firstLogin(
        onSuccess: (User) -> Unit,
        onFailed: () -> Unit
    ) = viewModelScope.launch {
            val req = authInterface.login(
                LoginRequest(
                    username.text.toString(),
                    pwd.text.toString()
                )
            )
            if (req.isSuccessful) {
                val res = req.body() as LoginResponse
                tokenManager.setToken(res.token)
                onSuccess(res.user)
            } else onFailed()
        }
}