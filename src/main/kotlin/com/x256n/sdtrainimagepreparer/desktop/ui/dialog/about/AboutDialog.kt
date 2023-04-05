package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
fun AboutDialog(navigator: Navigator<Destinations>, isDialogVisible: Boolean) {
    val LOG = remember { LoggerFactory.getLogger("AboutDialog") }
    val viewModel by KoinJavaComponent.inject<AboutViewModel>(AboutViewModel::class.java)
    val state by viewModel.state


    LaunchedEffect(Unit) {
        viewModel.onEvent(AboutEvent.AboutDisplayed)
    }
    Dialog(
        title = "About",
        undecorated = false,
        resizable = false,
        visible = isDialogVisible,
        state = DialogState(width = 320.dp, height = 240.dp),
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
                        .padding(MaterialTheme.spaces.extraSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Made by me!")
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