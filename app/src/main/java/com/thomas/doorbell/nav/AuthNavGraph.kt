package com.thomas.doorbell.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.thomas.doorbell.screens.ForgetPasswordEntry
import com.thomas.doorbell.screens.LoginEntry
import com.thomas.doorbell.screens.RegisterEntry
import com.thomas.doorbell.viewmodel.AppViewModel

@Composable
fun AuthNavigation(
    rootBackStack: NavBackStack<NavKey>,
    modifier: Modifier,
    appViewModel: AppViewModel
) {
    val authBackStack = rememberNavBackStack(NavRoute.Auth.Login)
    NavDisplay(
        backStack = authBackStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<NavRoute.Auth.Login> {
                LoginEntry(
                    backStack = authBackStack,
                    appViewModel = appViewModel
                ) {
                    rootBackStack.clear()
                    rootBackStack.add(NavRoute.Home)
                }
            }
            entry<NavRoute.Auth.Register> {
                RegisterEntry()
            }
            entry<NavRoute.Auth.ForgetPassword> {
                ForgetPasswordEntry()
            }
        }
    )
}

