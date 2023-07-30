package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.createproject

import WinButton
import WinTextField
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.WinCheckbox
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import java.nio.file.Path
import javax.swing.JFileChooser

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun CreateProjectDialog(navigator: Navigator<Destinations>, dest: Destinations.CreateProject) {
    val log = remember { LoggerFactory.getLogger("CreateProjectScreen") }
    val viewModel by remember {
        KoinJavaComponent.inject<CreateProjectViewModel>(CreateProjectViewModel::class.java)
    }
    val state by viewModel.state
    var showImagesDirectoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateProjectEvent.CreateProjectDialogDisplayed)
        dest.path?.let {
            viewModel.onEvent(CreateProjectEvent.ImagesDirectoryChanged(it.toString()))
        }
    }

    rememberSaveable(state.isProjectCreated) {
        if (state.isProjectCreated) {
            state.imageDirectory?.let {
                navigator.goTo(
                    key = Destinations.Home(Destinations.Home.Action.LoadProject(Path.of(it))),
                    strategy = StackDuplicateContentStrategy.CLEAR_STACK
                )
            }
        }
    }

    Dialog(
        title = "Create project",
        undecorated = false,
        resizable = false,
        visible = true,
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


        if (showImagesDirectoryPicker) {
            JFileChooser(System.getProperty("user.home") ?: "/").apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialogTitle = "Select a folder"
                approveButtonText = "Select"
//            approveButtonToolTipText = "Select current directory as save destination"
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                val result = showOpenDialog(window.rootPane)
                if (result == JFileChooser.APPROVE_OPTION) {
                    showImagesDirectoryPicker = false
                    selectedFile?.let {
                        log.error("result = ${it}")
                        viewModel.onEvent(CreateProjectEvent.ImagesDirectoryChanged(it.path))
                    }
                } else {
                    showImagesDirectoryPicker = false
                }
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
                        WinTextField(
                            modifier = Modifier
                                .weight(1f),
                            fieldModifier = Modifier
                                .weight(1f),
                            title = "Images directory: ",
                            singleLine = true,
                            maxLines = 1,
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier

                                .padding(horizontal = MaterialTheme.spaces.small),
                            text = "Caption files extension: "
                        )
                        WinTextField(modifier = Modifier
                            .width(64.dp),
                            text = state.captionExtension,
                            onValueChange = {
                                viewModel.onEvent(CreateProjectEvent.CaptionExtensionsChanged(it))
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier

                                .padding(horizontal = MaterialTheme.spaces.small),
                            text = "Target image resolution: "
                        )
                        WinTextField(modifier = Modifier
                            .width(64.dp),
                            text = state.targetImageResolution.toString(),
                            onValueChange = {
                                viewModel.onEvent(CreateProjectEvent.TargetImageResolutionChanged(it))
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier

                                .padding(horizontal = MaterialTheme.spaces.small),
                            text = "Target images format:",
                        )
                        WinTextField(modifier = Modifier
                            .width(64.dp),
                            text = state.targetImageFormat,
                            onValueChange = {
                                viewModel.onEvent(CreateProjectEvent.TargetImageFormatChanged(it))
                            })
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row {
                        WinCheckbox(
                            text = "Override (delete) existing project",
                            isChecked = state.overrideExistingProject,
                            onCheckedChange = {
                                viewModel.onEvent(CreateProjectEvent.OverrideExistingProject(it))
                            }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(MaterialTheme.spaces.small)
                    )
                    Row {
                        WinCheckbox(
                            text = "Merge all existing supported caption files",
                            isChecked = state.mergeExistingCaptionFiles,
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
                            text = "Do not create empty caption files when project is opening",
                            isChecked = state.createCaptionsWhenAddingContent,
                            onCheckedChange = {
                                viewModel.onEvent(CreateProjectEvent.CreateCaptionsWhenAddingContentChanged(it))
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
                            BorderStroke(
                                1.dp,
                                SolidColor(if (state.errorMessage == null) Color.Transparent else Color.Red)
                            )
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