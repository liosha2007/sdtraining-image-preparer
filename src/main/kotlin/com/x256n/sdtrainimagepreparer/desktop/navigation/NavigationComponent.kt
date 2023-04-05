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
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about.AboutDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject.CreateProjectDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.settings.SettingsDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.component.MainMenu
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeScreen
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
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
        val viewModel by inject<HomeViewModel>(HomeViewModel::class.java)
        HomeScreen(viewModel, navigator)

        CreateProjectDialog(navigator, isDialogVisible = dest is Destinations.CreateProject)
        SettingsDialog(navigator, isDialogVisible = dest is Destinations.Settings)
        AboutDialog(navigator, isDialogVisible = dest is Destinations.About)
    }
    NavContainer(navigator)
}