@file:OptIn(ExperimentalMaterialApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.WinCheckbox
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun SettingsDialog(navigator: Navigator<Destinations>) {
    val LOG = remember { LoggerFactory.getLogger("SettingsDialog") }
    val viewModel by KoinJavaComponent.inject<SettingsViewModel>(SettingsViewModel::class.java)
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(SettingsEvent.SettingsDisplayed)
    }
    Dialog(
        title = "Settings",
        undecorated = false,
        resizable = false,
        visible = true,
        state = DialogState(width = 380.dp, height = 320.dp),
        onKeyEvent = {
            if (it.key == Key.Escape) {
                navigator.goBack()
            }
            return@Dialog true
        },
        onCloseRequest = {
            navigator.goBack()
        }
    ) {

        Column(
            modifier = Modifier
                .padding(MaterialTheme.spaces.small)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spaces.medium)
            ) {
                WinCheckbox(
                    text = "Is debug mode",
                    isChecked = state.isDebugMode,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.IsDebugMode(it))
                    },
                    enabled = false
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.thumbnailsWidth.toString(),
                    title = "Thumbnails width:",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.ThumbnailsWidth(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.thumbnailsFormat,
                    title = "Thumbnails format:",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.ThumbnailsFormat(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.keywordsDelimiter,
                    title = "Keywords delimiter:",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.KeywordsDelimiter(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinCheckbox(
                    text = "Open last project on start",
                    isChecked = state.openLastProjectOnStart,
                    onCheckedChange = {
                        viewModel.onEvent(SettingsEvent.OpenLastProjectOnStart(it))
                    }
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.supportedImageFormats,
                    title = "Supported image formats:",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.SupportedImageFormats(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.small)
                )
                WinTextField(
                    text = state.supportedCaptionExtensions,
                    title = "Supported caption extensions:",
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.SupportedCaptionExtensions(it))
                    },
                    maxLines = 1
                )
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.medium)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    WinButton(text = "Close", onClick = {
                        navigator.goBack()
                    })
                }
            }
        }
    }
}