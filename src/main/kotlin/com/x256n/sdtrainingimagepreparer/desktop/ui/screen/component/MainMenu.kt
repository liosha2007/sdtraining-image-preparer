package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBarScope
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.goTo
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalNavigationApi
@Composable
fun MenuBarScope.MainMenu(
    navigator: ComposeNavigatorByKey<Destinations, Destinations>,
    onCreateProject: () -> Unit = {},
    onOpenProject: () -> Unit = {},
    onCloseProject: () -> Unit = {},
    onExit: () -> Unit = {},
    onDeleteImage: () -> Unit = {},
    onSyncImages: () -> Unit = {},
    onCreateAllCaptions: () -> Unit = {},
) {
    Menu("File") {
        Item(
            "New project...",
            mnemonic = 'N',
            onClick = onCreateProject
        )
        Item(
            "Open...",
            mnemonic = 'O',
            onClick = onOpenProject
        )
        Item(
            "Close project",
            mnemonic = 'C',
            onClick = onCloseProject
        )
        Item(
            "Clear project...",
            mnemonic = 'D',
            onClick = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "Project files and config will be deleted.\nImages and captions will NOT be affected. Clear project?",
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
            onClick = onExit
        )
    }

    Menu("Images") {
        Item(
            "Delete image...",
            mnemonic = 'd',
            onClick = onDeleteImage
        )
        Item(
            "Convert all...",
            mnemonic = 'c',
            onClick = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "All images will be converted to format that was specified in create project dialog.\nSource images will be deleted. Convert images?",
                        targetDest = Destinations.Home(
                            action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.ConvertImages)
                        )
                    )
                )
            }
        )
        Item(
            "Sync images (F5)",
            mnemonic = 's',
            onClick = onSyncImages
        )
    }

    Menu("Captions") {
        Item(
            "Create all caption files",
            mnemonic = 'c',
            onClick = onCreateAllCaptions
        )
        Item(
            "Delete all caption files...",
            mnemonic = 'd',
            onClick = {
                navigator.goTo(Destinations.DeleteCaptionsConfirmation)
            }
        )
    }

    Menu("Options") {
        Item(
            "Settings...",
            mnemonic = 's',
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