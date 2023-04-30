@file:OptIn(ExperimentalNavigationApi::class, ExperimentalMaterialApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.deletecaptions

import WinButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.WinCheckbox
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import javax.swing.FocusManager

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun DeleteCaptionsConfirmationDialog(
    navigator: Navigator<Destinations>,
    dest: Destinations.DeleteCaptionsConfirmation
) {
    val log = remember { LoggerFactory.getLogger("DeleteCaptionsConfirmationDialog") }
    val viewModel by KoinJavaComponent.inject<DeleteCaptionsConfirmationViewModel>(DeleteCaptionsConfirmationViewModel::class.java)
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            DeleteCaptionsConfirmationEvent.DeleteCaptionsConfirmationDisplayed
        )
    }
    if (navigator.currentKey is Destinations.DeleteCaptionsConfirmation) {
        DisposableEffect(Unit) {
            val keyEventDispatcher = KeyEventDispatcher { keyEvent ->
                when (keyEvent.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        confirmDeleting(navigator, state.isDeleteOnlyEmpty)
                        true
                    }
                    KeyEvent.VK_ESCAPE -> {
                        navigator.goBack()
                        true
                    }
                    else -> false
                }
            }
            FocusManager.getCurrentManager().addKeyEventDispatcher(keyEventDispatcher)
            onDispose {
                FocusManager.getCurrentManager().removeKeyEventDispatcher(keyEventDispatcher)
            }
        }
    }
    Dialog(
        title = "Delete caption files",
        undecorated = false,
        resizable = true,
        visible = true,
        state = DialogState(width = 360.dp, height = 240.dp),
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
                .fillMaxSize()
                .padding(MaterialTheme.spaces.extraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        top = MaterialTheme.spaces.medium,
                        start = MaterialTheme.spaces.medium,
                        end = MaterialTheme.spaces.medium
                    ),
                text = "Caption files will be deleted!\nAre you sure?",
                fontSize = MaterialTheme.typography.h6.fontSize,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WinCheckbox(
                    text = "Delete only empty caption files",
                    isChecked = state.isDeleteOnlyEmpty,
                    onCheckedChange = {
                        viewModel.onEvent(DeleteCaptionsConfirmationEvent.DeleteOnlyEmptyChanged(it))
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WinButton(text = "Yes", onClick = {
                    confirmDeleting(navigator, state.isDeleteOnlyEmpty)
                })
                WinButton(text = "Cancel", onClick = {
                    navigator.goBack()
                })
            }
        }
    }
}

fun confirmDeleting(navigator: Navigator<Destinations>, isDeleteOnlyEmpty: Boolean) {
    navigator.goTo(
        Destinations.Home(
            action = Destinations.Home.Action.DeleteCaptionsConfirmationDialogResult(isDeleteOnlyEmpty)
        ),
        strategy = StackDuplicateContentStrategy.CLEAR_STACK
    )
}
