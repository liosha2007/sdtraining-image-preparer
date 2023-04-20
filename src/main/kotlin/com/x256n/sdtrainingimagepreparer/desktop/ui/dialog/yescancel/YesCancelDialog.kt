package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yescancel

import WinButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Navigator
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
fun YesCancelDialog(navigator: Navigator<Destinations>, dest: Destinations.YesCancel) {
    val log = remember { LoggerFactory.getLogger("YesCancelDialog") }
    val viewModel by KoinJavaComponent.inject<YesCancelViewModel>(YesCancelViewModel::class.java)
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            YesCancelEvent.YesCancelDisplayed
        )
    }
    if (navigator.currentKey is Destinations.YesCancel) {
        DisposableEffect(Unit) {
            val keyEventDispatcher = KeyEventDispatcher { keyEvent ->
                when (keyEvent.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        navigator.goTo(key = dest.targetDest, strategy = StackDuplicateContentStrategy.CLEAR_STACK)
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
        title = dest.title,
        undecorated = false,
        resizable = true,
        visible = true,
        state = DialogState(width = dest.width, height = dest.height),
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
                text = dest.message,
                fontSize = MaterialTheme.typography.caption.fontSize,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spaces.medium),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WinButton(text = "Yes", onClick = {
                    navigator.goTo(key = dest.targetDest, strategy = StackDuplicateContentStrategy.CLEAR_STACK)
                })
                WinButton(text = "Cancel", onClick = {
                    navigator.goBack()
                })
            }
        }
    }
}