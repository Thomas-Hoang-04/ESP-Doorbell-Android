package com.thomas.doorbell.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomas.doorbell.R
import com.thomas.doorbell.dto.AvailabilityResponse
import com.thomas.doorbell.dto.LoginRequest
import com.thomas.doorbell.dto.LoginResponse
import com.thomas.doorbell.dto.User
import com.thomas.doorbell.keystore.TokenManager
import com.thomas.doorbell.keystore.UserData
import com.thomas.doorbell.network.AuthInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: DataStore<UserData>,
    private val authInterface: AuthInterface,
    private val tokenManager: TokenManager,
): ViewModel() {
    val username = TextFieldState("")
    val pwd = TextFieldState("")

    private val _usernameError = MutableStateFlow<Int?>(null)
    val usernameError = _usernameError.asStateFlow()
    private val _usernameTouched = MutableStateFlow(false)

    private val _passwordError = MutableStateFlow<Int?>(null)
    val passwordError = _passwordError.asStateFlow()
    private val _passwordTouched = MutableStateFlow(false)

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

    fun login(
        onSuccess: suspend (User) -> Unit,
        onLoginFailed: () -> Unit,
        onFailed: suspend () -> Unit
    ) {
        if (validateAll()) {
            viewModelScope.launch {
                val req = authInterface.checkLoginExists(
                    username.text.toString()
                )
                if (req.isSuccessful) {
                    val res = req.body() as AvailabilityResponse
                    if (res.available) {
                        val loginReq = authInterface.login(
                            LoginRequest(
                                username.text.toString(),
                                pwd.text.toString()
                            )
                        )
                        if (loginReq.isSuccessful) {
                            val loginRes = loginReq.body() as LoginResponse
                            dataStore.updateData {
                                it.copy(
                                    username = username.text.toString(),
                                    password = pwd.text.toString(),
                                )
                            }
                            tokenManager.setToken(loginRes.token)
                            onSuccess(loginRes.user)
                        } else {
                            _passwordError.update { R.string.pwd_error }
                            onLoginFailed()
                        }
                    } else {
                        _usernameError.update { R.string.user_not_exist_error }
                        onLoginFailed()
                    }
                } else onFailed()
            }
        }
    }

    private fun validateUsername(usr: String) {
        val error = when {
            usr.isEmpty() -> R.string.username_empty_error
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

    private fun validateAll(): Boolean {
        validateUsername(username.text.toString())
        validatePassword(pwd.text.toString())

        return _usernameError.value == null && _passwordError.value == null
    }
}