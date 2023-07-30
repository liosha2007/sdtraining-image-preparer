@file:OptIn(
    ExperimentalPathApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalNavigationApi::class, ExperimentalPathApi::class, ExperimentalPathApi::class
)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.StackDuplicateContentStrategy
import com.chrynan.navigation.compose.goTo
import com.x256n.sdtrainingimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainingimagepreparer.desktop.navigation.INavigator
import com.x256n.sdtrainingimagepreparer.desktop.navigation.NothingNavigator
import com.x256n.sdtrainingimagepreparer.desktop.theme.DefaultTheme
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yesnocancel.YesNoCancelDialog
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.awt.Cursor
import java.awt.KeyEventDispatcher
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.nio.file.Path
import javax.swing.FocusManager
import javax.swing.JFileChooser
import javax.swing.JRootPane
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun HomeScreen(
    navigator: INavigator<Destinations, StackDuplicateContentStrategy>,
    dest: Destinations,
    state: HomeState,
    uiActionHandler: Flow<UIAction>? = null,
    sendEvent: (HomeEvent) -> Unit = {},
    rootPanel: JRootPane? = null,
) {
    val log = remember { LoggerFactory.getLogger("HomeScreen") }
    val coroutineScope = rememberCoroutineScope()

    rememberSaveable(dest) {
        if (dest is Destinations.Home && dest.action is Destinations.Home.Action.LoadProject) {
            sendEvent(HomeEvent.LoadProject(dest.action.projectDirectory))
        }
        if (dest is Destinations.Home && dest.action is Destinations.Home.Action.YesCancelDialogResult) {
            sendEvent(dest.action.targetEvent as HomeEvent)
        }
        if (dest is Destinations.Home && dest.action is Destinations.Home.Action.YesNoCancelDialogResult) {
            if (dest.action.isYes) {
                sendEvent(dest.action.targetYesEvent as HomeEvent)
            } else {
                sendEvent(dest.action.targetNoEvent as HomeEvent)
            }
        }
        if (dest is Destinations.Home && dest.action is Destinations.Home.Action.DeleteCaptionsConfirmationDialogResult) {
            sendEvent(HomeEvent.DeleteAllCaptions(dest.action.isDeleteOnlyEmpty))
        }
    }

    var showFileChooser by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        sendEvent(HomeEvent.HomeDisplayed)
        uiActionHandler?.collect {
            when (it) {
                is UIAction.CreateNewProjectYesNo -> {
                    navigator.goTo(
                        Destinations.YesNoCancel(
                            message = "Create new project?",
                            targetDestCreator = { isYes ->
                                Destinations.Home(
                                    action = Destinations.Home.Action.YesNoCancelDialogResult(
                                        isYes, HomeEvent.CreateProject(it.path), HomeEvent.LoadProject(it.path)
                                    )
                                )
                            },
                        )
                    )
                }

                is UIAction.CreateProject -> {
                    navigator.goTo(Destinations.CreateProject(it.path))
                }

                is UIAction.ChooseProjectDirectoryDialog -> {
                    showFileChooser = true
                }
            }
        }
    }

    val lazyDataState = rememberLazyListState()
    val isShiftPressed = remember { mutableStateOf(false) }
    if (navigator.currentKey is Destinations.Home) {
        DisposableEffect(Unit) {
            val keyEventDispatcher = KeyEventDispatcher { keyEvent ->
                if (keyEvent.keyCode == KeyEvent.VK_SHIFT) {
                    isShiftPressed.value = keyEvent.id == KeyEvent.KEY_PRESSED
                    true
                } else if (keyEvent.keyCode == KeyEvent.VK_TAB && keyEvent.id != KeyEvent.KEY_PRESSED) {
                    if (keyEvent.isShiftDown) {
                        sendEvent(HomeEvent.ShowPrevImage)
                    } else {
                        sendEvent(HomeEvent.ShowNextImage)
                    }
                    coroutineScope.launch {
                        if (state.dataIndex != -1) {
                            lazyDataState.animateScrollToItem(state.dataIndex)
                        }
                    }
                    true
                } else if (keyEvent.keyCode == KeyEvent.VK_ENTER) {
                    sendEvent(HomeEvent.EnterPressed)
                    true
                } else if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {
                    sendEvent(HomeEvent.EscPressed)
                    true
                } else if (keyEvent.keyCode == KeyEvent.VK_DELETE) {
                    if (state.screenMode == ScreenMode.Default) {
                        navigator.goTo(
                            Destinations.YesCancel(
                                message = "Selected image and caption file will be deleted.\nDelete image and caption file?",
                                targetDest = Destinations.Home(
                                    action = Destinations.Home.Action.YesCancelDialogResult(targetEvent = HomeEvent.DeleteImage)
                                )
                            )
                        )
                    } else {
                        sendEvent(HomeEvent.DeletePressed)
                    }
                    true
                } else if (keyEvent.keyCode == KeyEvent.VK_F5) {
                    sendEvent(HomeEvent.SyncImages)
                    true
                } else false
            }
            FocusManager.getCurrentManager().addKeyEventDispatcher(keyEventDispatcher)
            onDispose {
                FocusManager.getCurrentManager().removeKeyEventDispatcher(keyEventDispatcher)
            }
        }
    }

    if (showFileChooser) {
        JFileChooser(System.getProperty("user.home") ?: "/").apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "Select a folder"
            approveButtonText = "Select"
//            approveButtonToolTipText = "Select current directory as save destination"
            rootPanel?.let { root ->
                val result = showOpenDialog(root)
                if (result == JFileChooser.APPROVE_OPTION) {
                    showFileChooser = false
                    selectedFile?.let {
                        log.error("result = ${it}")
                        sendEvent(HomeEvent.LoadProject(it.toPath()))
                    }
                } else {
                    showFileChooser = false
                }
            }
        }
    }

    val spacerSize = 4.dp
    var explorerPanelWidth by remember { mutableStateOf(168.dp) }
    var previewPanelSize by remember { mutableStateOf(IntSize.Zero) }
    var tagsPanelWidth by remember { mutableStateOf(168.dp) }
    var captionPanelHeight by remember { mutableStateOf(64.dp) }
    val lazyKeywordState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        HeaderToolsPanel(
            modifier = Modifier
                .height(24.dp),
            navigator = navigator,
            state = state,
            sendEvent = sendEvent
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .weight(1f)
        ) {

            LeftThumbnailsPanel(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(explorerPanelWidth),
                state = state,
                sendEvent = sendEvent,
                lazyState = lazyDataState
            )

            Spacer(modifier = Modifier
                .background(Color.Gray)
                .fillMaxHeight()
                .width(spacerSize)
                .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures(
                        matcher = PointerMatcher.Primary
                    ) {
                        if (previewPanelSize.width.dp > spacerSize || it.x < 0) {
                            explorerPanelWidth += it.x.dp
                        }
                    }
                })
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .onSizeChanged {
                        previewPanelSize = it
                    }
                    .weight(1f)
            ) {

                CenterPreviewPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = state,
                    sendEvent = sendEvent,
                    isShiftPressed = isShiftPressed.value
                )

                Spacer(modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .height(spacerSize)
                    .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            matcher = PointerMatcher.Primary
                        ) {
                            if (previewPanelSize.height.dp > spacerSize || it.y > 0) {
                                captionPanelHeight -= it.y.dp
                            }
                        }
                    })

                BottomCaptionPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(captionPanelHeight),
                    state = state,
                    sendEvent = sendEvent
                )

            }
            Spacer(modifier = Modifier
                .background(Color.Gray)
                .fillMaxHeight()
                .width(spacerSize)
                .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures(
                        matcher = PointerMatcher.Primary
                    ) {
                        if (previewPanelSize.width.dp > spacerSize || it.x < 0) {
                            tagsPanelWidth -= it.x.dp
                        }
                    }
                })

            RightKeywordsPanel(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(tagsPanelWidth),
                state = state,
                sendEvent = sendEvent,
                lazyState = lazyKeywordState
            )
        }
        FootherStatusPanel(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .height(18.dp),
            state = state,
        )

    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        DefaultTheme {
            HomeScreen(
                navigator = NothingNavigator(currentKey = Destinations.Home()),
                dest = Destinations.Home(),
                state = HomeState(),
            )
        }
    }
}