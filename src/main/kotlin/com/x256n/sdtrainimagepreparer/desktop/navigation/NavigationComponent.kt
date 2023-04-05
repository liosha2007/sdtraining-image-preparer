package com.x256n.sdtrainimagepreparer.desktop.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.NavContainer
import com.chrynan.navigation.compose.rememberNavigatorByKey
import com.x256n.sdtrainimagepreparer.desktop.screen.component.MainMenu
import com.x256n.sdtrainimagepreparer.desktop.screen.config.ConfigScreen
import com.x256n.sdtrainimagepreparer.desktop.screen.config.ConfigViewModel
import com.x256n.sdtrainimagepreparer.desktop.screen.home.HomeScreen
import com.x256n.sdtrainimagepreparer.desktop.screen.home.HomeViewModel
import org.koin.java.KoinJavaComponent.inject

typealias Navigator<T> = ComposeNavigatorByKey<T, Destinations>

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun FrameWindowScope.NavigationComponent() {
    val navigator = rememberNavigatorByKey<Destinations, Destinations>(initialContext = Destinations.Home()) { dest ->

        MenuBar {
            MainMenu(navigator)
        }
        when (dest) {
            is Destinations.Home -> {
                val viewModel by inject<HomeViewModel>(HomeViewModel::class.java)
                HomeScreen(viewModel, navigator)
            }
            is Destinations.Config -> {
                val viewModel by inject<ConfigViewModel>(ConfigViewModel::class.java)
                ConfigScreen(viewModel, navigator)
            }
            else -> throw IllegalStateException("Unknown destination! Check NavigationComponent.")
        }
    }
    NavContainer(navigator)
}