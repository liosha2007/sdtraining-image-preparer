@file:OptIn(ExperimentalPathApi::class, ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import WinButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlin.io.path.ExperimentalPathApi

@Composable
fun HeaderToolsPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val state by viewModel.state
    Row(
        modifier = modifier
            .padding(start = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.isProjectLoaded) {
            if (state.isEditMode) {
                WinButton(modifier = Modifier
                    .fillMaxHeight(),
                    onClick = {
                        viewModel.sendEvent(HomeEvent.CropApplyClicked)
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
                        viewModel.sendEvent(HomeEvent.ChangeAreaToSize(512f))
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
                        viewModel.sendEvent(HomeEvent.ChangeAreaToSize(768f))
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
                        viewModel.sendEvent(HomeEvent.EditModeClicked(false))
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
                        viewModel.sendEvent(HomeEvent.EditModeClicked(true))
                    }
                ) {
                    Text(
                        text = "Crop mode",
                        fontSize = MaterialTheme.typography.body2.fontSize
                    )
                }
            }
        }
    }
}