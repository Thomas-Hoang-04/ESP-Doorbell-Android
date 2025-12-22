package com.thomas.doorbell.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomas.doorbell.dto.LoginRequest
import com.thomas.doorbell.dto.LoginResponse
import com.thomas.doorbell.dto.User
import com.thomas.doorbell.keystore.TokenManager
import com.thomas.doorbell.keystore.UserData
import com.thomas.doorbell.nav.NavRoute
import com.thomas.doorbell.network.AuthInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStore: DataStore<UserData>,
    private val authInterface: AuthInterface,
    private val tokenManager: TokenManager
): ViewModel() {
    private val _startDestination = MutableStateFlow<NavRoute>(NavRoute.Auth)
    val startDestination = _startDestination.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()


    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun updateUser(user: User) = _user.update { user }

    fun load() {
        viewModelScope.launch {
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
                    _startDestination.update { NavRoute.Home }
                    delay(100)
                    _isReady.value = true
                } else {
                    delay(100)
                    _isReady.value = true
                }
            }
        }
    }
}