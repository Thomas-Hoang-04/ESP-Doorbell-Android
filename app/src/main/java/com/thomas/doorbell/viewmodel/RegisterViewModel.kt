package com.thomas.doorbell.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomas.doorbell.R
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
class RegisterViewModel @Inject constructor(
    private val dataStore: DataStore<UserData>,
    private val authInterface: AuthInterface,
    private val tokenManager: TokenManager,
): ViewModel() {
    val username = TextFieldState("")
    val pwd = TextFieldState("")

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe = _rememberMe.asStateFlow()

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

    fun rememberMe() = _rememberMe.update { !it }

    fun rememberMe(value: Boolean) = _rememberMe.update { value }

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

    private fun storeData() {
        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    username = username.text.toString(),
                    password = pwd.text.toString(),
                )
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
            pwd.length < 6 -> R.string.password_length_error
            else -> null
        }

        _passwordError.update { error }
    }
}