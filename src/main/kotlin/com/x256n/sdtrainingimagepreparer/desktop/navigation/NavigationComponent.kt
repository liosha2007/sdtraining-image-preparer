@file:OptIn(ExperimentalNavigationApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.NavContainer
import com.chrynan.navigation.compose.rememberNavigatorByKey
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.about.AboutDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.createproject.CreateProjectDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.deletecaptions.DeleteCaptionsConfirmationDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings.SettingsDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yescancel.YesCancelDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component.MainMenu
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeScreen
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import kotlin.io.path.ExperimentalPathApi

typealias Navigator<T> = ComposeNavigatorByKey<T, Destinations>

@ExperimentalPathApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun FrameWindowScope.NavigationComponent() {
    val log = remember { LoggerFactory.getLogger("NavigationComponent") }
    val navigator = rememberNavigatorByKey<Destinations, Destinations>(initialContext = Destinations.Home()) { dest ->

        val viewModel by remember {
            KoinJavaComponent.inject<HomeViewModel>(HomeViewModel::class.java)
        }
        val state by viewModel.state

        MenuBar {
            MainMenu(
                navigator = navigator,
                onOpenProject = { viewModel.sendEvent(HomeEvent.OpenProject) },
                onCloseProject = { viewModel.sendEvent(HomeEvent.CloseProject) },
                onExit = { viewModel.sendEvent(HomeEvent.Exit()) },
                onDeleteImage = { viewModel.sendEvent(HomeEvent.DeleteImage) },
                onSyncImages = { viewModel.sendEvent(HomeEvent.SyncImages) },
                onCreateAllCaptions = { viewModel.sendEvent(HomeEvent.CreateAllCaptions) },
            )
        }

        HomeScreen(
            navigator = ChrynanNavigator(navigator),
            dest = dest,
            state = state,
            sendEvent = viewModel::sendEvent,
            rootPanel = window.rootPane
        )
        when (dest) {
            is Destinations.CreateProject -> {
                CreateProjectDialog(navigator)
            }

            is Destinations.Settings -> {
                SettingsDialog(navigator)
            }

            is Destinations.YesCancel -> {
                YesCancelDialog(navigator, dest)
            }

            is Destinations.DeleteCaptionsConfirmation -> {
                DeleteCaptionsConfirmationDialog(navigator, dest)
            }

            is Destinations.About -> {
                AboutDialog(navigator)
            }

            else -> {
                if (dest is Destinations.Home) {
                    LoggerFactory.getLogger("NavigationComponent").debug("Home destination is processed by HomeScreen")
                } else {
                    LoggerFactory.getLogger("NavigationComponent").warn("Unknown destination: $dest")
                }
            }
        }
    }
    NavContainer(navigator)
}