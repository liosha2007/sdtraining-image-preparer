package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

import WinButton
import WinTextField
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import com.chrynan.navigation.ExperimentalNavigationApi
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.regexapplier.desktop.component.WinCheckbox
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun CreateProjectDialog(navigator: Navigator<Destinations>, isDialogVisible: Boolean) {
    val LOG = remember { LoggerFactory.getLogger("CreateProjectScreen") }
    val viewModel by KoinJavaComponent.inject<CreateProjectViewModel>(CreateProjectViewModel::class.java)
    val state by viewModel.state
    var showImagesDirectoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateProjectEvent.CreateProjectDisplayed)
    }
    Dialog(
        title = "Create project",
        undecorated = false,
        resizable = false,
        visible = isDialogVisible,
        state = DialogState(width = 480.dp, height = 360.dp),
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

        DirectoryPicker(showImagesDirectoryPicker) { imagesDirectory ->
            showImagesDirectoryPicker = false
            imagesDirectory?.let {
                viewModel.onEvent(CreateProjectEvent.ImagesDirectoryChanged(imagesDirectory))
            }
        }

        if (!state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(MaterialTheme.spaces.medium)
                ) {
                    Row {
                        Text(
                            modifier = Modifier

                                .padding(horizontal = MaterialTheme.spaces.small),
                            text = "Images directory: "
                        )
                        WinTextField(modifier = Modifier
                            .weight(1f),
                            text = state.imageDirectory ?: "", onValueChange = {
                                viewModel.onEvent(CreateProjectEvent.ImagesDirectoryChanged(it))
                            })
                        WinButton(modifier = Modifier
                            .height(20.dp)
                            .width(25.dp),
                            text = "...", onClick = {
                                showImagesDirectoryPicker = true
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    if (state.isOverrideExistingProject) {
                        Row {
                            WinCheckbox(
                                text = "Override (delete) existing project",
                                isChecked = true,
                                enabled = false,
                                onCheckedChange = {}
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(MaterialTheme.spaces.small)
                        )
                    }
                    Row {
                        Text(
                            modifier = Modifier

                                .padding(horizontal = MaterialTheme.spaces.small),
                            text = "Caption files extension: "
                        )
                        WinTextField(modifier = Modifier
                            .weight(1f),
                            text = state.captionExtension,
                            onValueChange = {
                                viewModel.onEvent(CreateProjectEvent.CaptionExtensionsChanged(it))
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row {
                        WinCheckbox(
                            text = "Merge existing .caption files",
                            isChecked = state.isMergeExistingCaptionFiles,
                            onCheckedChange = {
                                viewModel.onEvent(CreateProjectEvent.MergeExistingCaptionFiles(it))
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row {
                        WinCheckbox(
                            text = "Merge existing .txt files",
                            isChecked = state.isMergeExistingTxtFiles,
                            onCheckedChange = {
                                viewModel.onEvent(CreateProjectEvent.MergeExistingTxtFiles(it))
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )


                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spaces.medium),
                        horizontalArrangement = Arrangement.End
                    ) {
                        WinButton(
                            text = "Create project",
                            onClick = {
                                viewModel.onEvent(CreateProjectEvent.CreateProject)
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, SolidColor(if (state.errorMessage == null) Color.Transparent else Color.Red))
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .horizontalScroll(state = rememberScrollState())
                            .padding(horizontal = 3.dp, vertical = 1.dp),
                        fontSize = MaterialTheme.typography.body2.fontSize,
                        text = if (state.errorMessage == null) "" else "Error: " + state.errorMessage
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