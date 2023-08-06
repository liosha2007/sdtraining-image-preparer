@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class,
    ExperimentalNavigationApi::class, ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class
)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.INavigator
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.ScreenMode
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.toolbar.*

@Composable
fun HeaderToolsPanel(
    modifier: Modifier = Modifier,
    navigator: INavigator<Destinations, StackDuplicateContentStrategy>,
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FileToolbarPanel(
            isProjectLoaded = state.isProjectLoaded,
            enabled = state.screenMode == ScreenMode.Default,
            onCreateProject = {
                sendEvent(HomeEvent.CreateProject())
            },
            onOpenFolderOrProject = {
                sendEvent(HomeEvent.Open())
            },
            onCloseFolderOrProject = {
                sendEvent(HomeEvent.CloseProject)
            },
            onClearProject = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "Project files and config will be deleted.\nImages and captions will NOT be affected. Clear project?",
                        targetDest = Destinations.Home(
                            action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.DropProject)
                        )
                    )
                )
            },
        )
        ImageToolbarPanel(
            isProjectLoaded = state.isProjectLoaded,
            enabled = state.screenMode == ScreenMode.Default,
            isImageSelected = state.hasData,
            onAddImages = {
                TODO("Adding image is not implemented")
            },
            onDeleteImage = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "Selected image and caption file will be deleted.\nDelete image and caption file?",
                        targetDest = Destinations.Home(
                            action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.DeleteImage)
                        )
                    )
                )
            },
            onImageCrop = {
                sendEvent(HomeEvent.ImageCropModeClicked(state.screenMode != ScreenMode.ImageCrop))
            },
            onConvertAllImages = {
                navigator.goTo(
                    Destinations.YesCancel(
                        message = "All images will be converted to format that was specified in create project dialog.\nSource images will be deleted. Convert images?",
                        targetDest = Destinations.Home(
                            action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.ConvertImages)
                        )
                    )
                )
            },
            onSyncImages = {
                sendEvent(HomeEvent.SyncImages)
            },
        )
        CaptionToolbarPanel(
            isProjectLoaded = state.isProjectLoaded,
            enabled = state.screenMode == ScreenMode.Default,
            onCreateCaptionFiles = {
                sendEvent(HomeEvent.CreateAllCaptions)
            },
            onDeleteCaptionFiles = {
                navigator.goTo(Destinations.DeleteCaptionsConfirmation)
            },
            onCaptionReplace = {
                sendEvent(HomeEvent.CaptionReplaceModeClicked(state.screenMode !is ScreenMode.CaptionReplace))
            }
        )

    }

    if (state.screenMode == ScreenMode.ImageCrop) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageCropModeToolbarPanel(
                show = true,
                onCropMode512 = {
                    sendEvent(HomeEvent.ChangeAreaToSize(512f))
                },
                onCropMode768 = {
                    sendEvent(HomeEvent.ChangeAreaToSize(768f))
                },
                onCropModeMax = {
                    sendEvent(HomeEvent.ChangeAreaToMax)
                },
                onCropModeApply = {
                    sendEvent(HomeEvent.ImageCropApplyClicked)
                },
                onCropModeCancel = {
                    sendEvent(HomeEvent.ImageCropModeClicked(false))
                }
            )
        }
    } else if (state.screenMode is ScreenMode.CaptionReplace) {
        Row(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CaptionReplaceModeToolbarPanel(
                show = true,
                onSearchValueChange = {
                    sendEvent(HomeEvent.CaptionReplaceSourceValueChange(it))
                },
                onCaptionReplaceApply = { searchValue, replacementValue ->
                    sendEvent(
                        HomeEvent.CaptionReplaceApplyClicked(
                            searchValue = searchValue,
                            replacementValue = replacementValue
                        )
                    )
                },
                onCaptionReplaceCancel = {
                    sendEvent(HomeEvent.CaptionReplaceModeClicked(false))
                }
            )
        }
    }
}