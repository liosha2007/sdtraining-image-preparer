@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class,
    ExperimentalNavigationApi::class, ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class
)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import WinButton
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.INavigator
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.ScreenMode

@Composable
fun HeaderToolsPanel(
    modifier: Modifier = Modifier,
    navigator: INavigator<Destinations, StackDuplicateContentStrategy>,
    state: HomeState,
    sendEvent: (HomeEvent) -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(start = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.isProjectLoaded) {
            if (state.screenMode == ScreenMode.ResizeCrop) {
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.CropApplyClicked)
                    }
                ) {
                    Text(
                        text = "Apply crop",
                        fontSize = MaterialTheme.typography.body2.fontSize
                    )
                }
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.ChangeAreaToSize(512f))
                    }
                ) {
                    Text(
                        text = "512x512",
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.ChangeAreaToSize(768f))
                    }
                ) {
                    Text(
                        text = "768x768",
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.ChangeAreaToMax)
                    }
                ) {
                    Text(
                        text = "max",
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.EditModeClicked(false))
                    }
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = MaterialTheme.typography.body2.fontSize
                    )
                }
            } else {
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        sendEvent(HomeEvent.EditModeClicked(true))
                    }
                ) {
                    Text(
                        text = "Crop mode",
                        fontSize = MaterialTheme.typography.body2.fontSize
                    )
                }
                Spacer(
                    modifier = Modifier
                        .width(MaterialTheme.spaces.small)
                )
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        navigator.goTo(
                            Destinations.YesCancel(
                                message = "Selected image and caption file will be deleted.\nDelete image and caption file?",
                                targetDest = Destinations.Home(
                                    action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.DeleteImage)
                                )
                            )
                        )
                    }
                ) {
                    Text(
                        text = "Delete image",
                        fontSize = MaterialTheme.typography.body2.fontSize
                    )
                }
            }
        }
    }
}