package com.thomas.doorbell.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.thomas.doorbell.viewmodel.AppViewModel

@Composable
fun DoorbellComposeApp(
    modifier: Modifier,
    viewModel: AppViewModel = hiltViewModel()
) {
    val navStack = rememberNavBackStack(NavRoute.Auth)
    NavDisplay(
        modifier = modifier,
        backStack = navStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<NavRoute.Auth> {
                AuthNavigation(
                    rootBackStack = navStack,
                    modifier = modifier,
                    appViewModel = viewModel
                )
            }
            entry<NavRoute.Home> {
                HomeEntry()
            }
        }
    )
}


@Composable
fun HomeEntry() {

}

