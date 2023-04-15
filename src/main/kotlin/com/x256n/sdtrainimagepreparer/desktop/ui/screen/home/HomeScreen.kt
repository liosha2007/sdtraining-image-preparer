package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.chrynan.navigation.ExperimentalNavigationApi
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.component.MainMenu
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.component.*
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import java.awt.Cursor
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun FrameWindowScope.HomeScreen(navigator: Navigator<Destinations>, dest: Destinations) {
    val log = remember { LoggerFactory.getLogger("HomeScreen") }
    val viewModel by remember {
        KoinJavaComponent.inject<HomeViewModel>(HomeViewModel::class.java)
    }
    MenuBar {
        MainMenu(navigator, viewModel)
    }
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state

    rememberSaveable(dest) {
        if (dest is Destinations.Home && dest.action is Destinations.Home.Action.LoadProject) {
            viewModel.sendEvent(HomeEvent.LoadProject(dest.action.projectDirectory))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendEvent(HomeEvent.HomeDisplayed)
    }

    DirectoryPicker(state.isShowChooseProjectDirectoryDialog) { projectDirectory ->
        projectDirectory?.let {
            viewModel.sendEvent(HomeEvent.LoadProject(Path.of(it)))
        }
    }

    val spacerSize = 4.dp
    var explorerPanelWidth by remember { mutableStateOf(168.dp) }
    var previewPanelSize by remember { mutableStateOf(IntSize.Zero) }
    var tagsPanelWidth by remember { mutableStateOf(168.dp) }
    var captionPanelHeight by remember { mutableStateOf(64.dp) }
    val lazyDataState = rememberLazyListState()
    val lazyKeywordState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent {
                return@onKeyEvent if (it.key == Key.Tab) {
                    log.debug("Key event: ${it.key}, shift: ${it.isShiftPressed}")
                    if (it.isShiftPressed) {
                        viewModel.sendEvent(HomeEvent.ShowPrevImage)
                    } else {
                        viewModel.sendEvent(HomeEvent.ShowNextImage)
                    }
                    coroutineScope.launch {
                        lazyDataState.animateScrollToItem(state.dataIndex)
                    }
                    true
                } else false
            }
    ) {

        HeaderToolsPanel(
            modifier = Modifier
                .height(24.dp),
            viewModel = viewModel
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
                viewModel = viewModel,
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
                    viewModel = viewModel
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
                    viewModel = viewModel
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
                viewModel = viewModel,
                lazyState = lazyKeywordState
            )
        }
        FootherStatusPanel(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .height(18.dp),
            viewModel = viewModel
        )

    }
}