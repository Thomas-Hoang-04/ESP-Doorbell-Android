package com.thomas.doorbell.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
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
    data object Auth: NavRoute {
        @Serializable
        data object Login: NavRoute

        @Serializable
        data object Register: NavRoute

        @Serializable
        data object ForgetPassword: NavRoute
    }

    @Serializable
    data object Home: NavRoute
}