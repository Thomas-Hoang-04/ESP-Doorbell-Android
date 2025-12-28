package com.thomas.doorbell.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.thomas.doorbell.dto.OTPPurpose
import com.thomas.doorbell.dto.OTPRequest
import kotlinx.serialization.Serializable

enum class HomeDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
    VIDEO("Stream", Icons.Default.Videocam)
}

@Serializable
sealed interface NavRoute: NavKey {

    @Serializable
    sealed interface Auth: NavRoute {
        @Serializable
        data object Login: Auth

        @Serializable
        data object Register: Auth

        @Serializable
        data object ForgetPassword: Auth

        @Serializable
        data class ResetPassword(val login: String): Auth

        @Serializable
        data class OTP(
            val username: String?,
            val email: String,
            val withOrigin: Boolean = false,
            val withAuthEndpoint: Auth? = null,
            val wipeBackStack: Boolean = false
        ): NavRoute {
            fun toRequest(purpose: OTPPurpose): OTPRequest = OTPRequest(username, email, purpose)
        }
    }

    @Serializable
    data object Home: NavRoute
}