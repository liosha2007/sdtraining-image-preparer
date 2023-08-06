package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
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
            icon = painterResource("create_project.png"),
            onClick = onCreateProject
        )
        Item(
            "Open...",
            icon = painterResource("open_folder.png"),
            mnemonic = 'O',
            onClick = onOpenProject
        )
        Item(
            "Close project",
            mnemonic = 'C',
            icon = painterResource("close.png"),
            onClick = onCloseProject
        )
        Item(
            "Clear project...",
            mnemonic = 'D',
            icon = painterResource("clear.png"),
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
            icon = painterResource("delete_image.png"),
            onClick = onDeleteImage
        )
        Item(
            "Convert all...",
            mnemonic = 'c',
            icon = painterResource("convert_all.png"),
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
            "Add images...",
            mnemonic = 'i',
            icon = painterResource("add_images.png"),
            onClick = {
                TODO("Add images...")
            }
        )
        Item(
            "Sync images (F5)",
            mnemonic = 's',
            icon = painterResource("sync.png"),
            onClick = onSyncImages
        )
    }

    Menu("Captions") {
        Item(
            "Create all caption files",
            mnemonic = 'c',
            icon = painterResource("create_caption_files.png"),
            onClick = onCreateAllCaptions
        )
        Item(
            "Delete all caption files...",
            mnemonic = 'd',
            icon = painterResource("delete_caption_files.png"),
            onClick = {
                navigator.goTo(Destinations.DeleteCaptionsConfirmation)
            }
        )
        Item(
            "Replace in all caption files...",
            mnemonic = 'd',
            icon = painterResource("replace_in_captions.png"),
            onClick = {
                TODO("Replace in all caption files...")
            }
        )
    }

    Menu("Options") {
        Item(
            "Settings...",
            mnemonic = 's',
            icon = painterResource("settings.png"),
            onClick = {
                navigator.goTo(Destinations.Settings)
            }
        )
    }

    Menu("Help") {
        Item(
            "About",
            mnemonic = 'A',
            icon = painterResource("about.png"),
            onClick = {
                navigator.goTo(Destinations.About)
            }
        )
    }
}