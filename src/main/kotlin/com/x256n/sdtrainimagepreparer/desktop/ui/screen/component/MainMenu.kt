package com.x256n.sdtrainimagepreparer.desktop.ui.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBarScope
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.goTo
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
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
                viewModel.onEvent(HomeEvent.OpenProject)
            }
        )
        CheckboxItem(
            "Advanced settings",
            mnemonic = 'A',
            checked = true,
            onCheckedChange = { /*isAdvancedSettings = !isAdvancedSettings*/ }
        )
        Menu("Theme") {
            Item(
                "Item 1",
                onClick = { println("Item 1") }
            )
            Item(
                "Item 2",
                onClick = { println("Item 2") }
            )
        }
        Item(
            "Exit",
            mnemonic = 'Q',
            onClick = { println("Exit") }
        )
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