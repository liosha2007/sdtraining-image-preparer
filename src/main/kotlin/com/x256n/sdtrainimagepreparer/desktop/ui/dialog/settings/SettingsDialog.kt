package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.settings

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
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
        state = DialogState(width = 360.dp, height = 500.dp),
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

        if (!state.isLoading) {
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spaces.small)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.spaces.extraSmall)
                ) {
                    WinTextField("Settings", onValueChange = {

                    })
                    WinButton(text = "Home", onClick = {
                        navigator.goBack()
                    })
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.medium)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}