package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import java.awt.Dimension

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun CreateProjectDialog(navigator: Navigator<Destinations>, isDialogVisible: Boolean) {
    val LOG = remember { LoggerFactory.getLogger("CreateProjectScreen") }
    val viewModel by KoinJavaComponent.inject<CreateProjectViewModel>(CreateProjectViewModel::class.java)
    val state by viewModel.state

    val dialogState = remember { DialogState(width = 360.dp, height = 500.dp) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateProjectEvent.CreateProjectDisplayed)
    }
    Dialog(
        title = "Create project",
        undecorated = false,
        resizable = true,
        visible = isDialogVisible,
        state = dialogState,
        onCloseRequest = {
            navigator.goBack()
        }
    ) {
        this.window.minimumSize = Dimension(240, 410)

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
                    WinTextField("Config", onValueChange = {

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