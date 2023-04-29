package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBarScope
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.goTo
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalNavigationApi
@Composable
fun MenuBarScope.MainMenu(navigator: ComposeNavigatorByKey<Destinations, Destinations>, viewModel: HomeViewModel) {
    Menu("File") {
        Item(
            "New project...",
            mnemonic = 'N',
            onClick = {
                navigator.goTo(Destinations.CreateProject)
            }
        )
        Item(
            "Open project...",
            mnemonic = 'O',
            onClick = {
                viewModel.sendEvent(HomeEvent.OpenProject)
            }
        )
        Item(
            "Close project",
            mnemonic = 'C',
            onClick = {
                viewModel.sendEvent(HomeEvent.CloseProject)
            }
        )
        Item(
            "Drop project...",
            mnemonic = 'D',
            onClick = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "Project files and config will be deleted.\nImages and captions will NOT be affected. Drop project?",
                        targetDest = Destinations.Home(
                            action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.DropProject)
                        )
                    )
                )
            }
        )
        Item(
            "Exit",
            mnemonic = 'Q',
            onClick = {
                viewModel.sendEvent(HomeEvent.Exit())
            }
        )
    }

    Menu("Actions") {
        Item(
            "Delete image...",
            onClick = {
                viewModel.sendEvent(HomeEvent.DeleteImage)
            }
        )
        Menu("Caption files") {
            Item(
                "Create all...",
                onClick = {
                    viewModel.sendEvent(HomeEvent.CreateAllCaptions)
                }
            )
            Item(
                "Delete all...",
                onClick = {
                    navigator.goTo(Destinations.DeleteCaptionsConfirmation)
                }
            )
        }
    }

    Menu("Options") {
        Item(
            "Settings...",
            mnemonic = 'S',
            onClick = {
                navigator.goTo(Destinations.Settings)
            }
        )
    }

    Menu("Help") {
        Item(
            "About",
            mnemonic = 'A',
            onClick = {
                navigator.goTo(Destinations.About)
            }
        )
    }
}