package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import WinTextField
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.chrynan.navigation.ExperimentalNavigationApi
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainimagepreparer.desktop.ui.component.AsyncImage
import com.x256n.sdtrainimagepreparer.desktop.ui.component.pathPainter
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.component.MainMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import org.slf4j.LoggerFactory
import java.awt.Cursor
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name

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
            viewModel.onEvent(HomeEvent.LoadProject(dest.action.projectDirectory))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.HomeDisplayed)
    }

    DirectoryPicker(state.isOpenProject) { projectDirectory ->
        projectDirectory?.let {
            viewModel.onEvent(HomeEvent.LoadProject(Path.of(it)))
        }
    }

    val spacerSize = 10.dp
    var explorerPanelWidth by remember { mutableStateOf(168.dp) }
    var previewPanelSize by remember { mutableStateOf(IntSize.Zero) }
    var tagsPanelWidth by remember { mutableStateOf(168.dp) }
    var captionPanelHeight by remember { mutableStateOf(64.dp) }
    val lazyDataState = rememberLazyListState()
    val lazyKeywordState = rememberLazyListState()
    var mainImagePainter by remember { mutableStateOf<ImageBitmap?>(null) }

    rememberSaveable(state.dataIndex) {
        if (state.hasData) {
            coroutineScope.launch(Dispatchers.IO) {
                mainImagePainter = pathPainter(state[state.dataIndex].absoluteImagePath)
            }
        }
    }

//    if (!state.isLoading) {
    Column(
        modifier = Modifier
            .padding(MaterialTheme.spaces.small)
            .fillMaxSize()
            .onKeyEvent {
                return@onKeyEvent if (it.key == Key.Tab) {
                    log.debug("Key event: ${it.key}, shift: ${it.isShiftPressed}")
                    if (it.isShiftPressed) {
                        viewModel.onEvent(HomeEvent.ShowPrevImage)
                    } else {
                        viewModel.onEvent(HomeEvent.ShowNextImage)
                    }
                    coroutineScope.launch {
                        lazyDataState.animateScrollToItem(state.dataIndex)
                    }
                    true
                } else false
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(MaterialTheme.spaces.extraSmall)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(explorerPanelWidth)
            ) {
                var thumbnailSize by remember { mutableStateOf(IntSize.Zero) }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            thumbnailSize = it
                        },
                    state = lazyDataState
                ) {
                    itemsIndexed(state.data) { index, item ->
                        var modifier: Modifier = Modifier
                        var textColor = Color.Black
                        if (index == state.dataIndex) {
                            modifier = Modifier
                                .background(Color.DarkGray)
                            textColor = Color.White
                        }

                        Column(
                            modifier = modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((thumbnailSize.width / 1.7).dp)
                                    .clickable {
                                        viewModel.onEvent(HomeEvent.ImageSelected(index))
                                    },
                                load = {
                                    pathPainter(item.thumbnailPath)
                                },
                                initialImage = painterResource("icon.ico"),
                                painterFor = { remember { BitmapPainter(it) } },
                                contentDescription = "Image $index",
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                modifier = Modifier,
                                text = item.imageName,
                                fontSize = 8.sp,
                                color = textColor
                            )
                            Text(
                                modifier = Modifier,
                                text = "${item.imageWidth} x ${item.imageHeight}",
                                fontSize = 10.sp,
                                color = textColor
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(MaterialTheme.spaces.extraSmall)
                            )
                        }
                    }
                }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (state.data.isNotEmpty()) {
                        if (mainImagePainter == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize(),
                                painter = BitmapPainter(mainImagePainter!!),
                                contentDescription = state[state.dataIndex].imagePath.name,
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(captionPanelHeight)
                ) {
                    WinTextField(
                        modifier = Modifier
                            .fillMaxSize(),
                        text = state.captionContent,
                        onValueChange = {
                            viewModel.onEvent(HomeEvent.CaptionContentChanged(it))
                        }
                    )
                }

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

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.Green)
                    .width(tagsPanelWidth)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = lazyKeywordState
                ) {
                    items(state.keywordList) { item ->
                        var modifier: Modifier = Modifier
                        var textColor = Color.Black
                        if (item.isAdded) {
                            modifier = Modifier
                                .background(Color.DarkGray)
                            textColor = Color.White
                        }
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onEvent(HomeEvent.KeywordSelected(item))
                                },
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = MaterialTheme.spaces.medium, vertical = MaterialTheme.spaces.small)
                                    .fillMaxWidth(),
                                text = item.keyword,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = MaterialTheme.spaces.medium),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
            }
            Text(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                fontSize = MaterialTheme.typography.body2.fontSize,
                text = state.statusText
            )
            Text(
                modifier = Modifier
                    .weight(0.7f)
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = MaterialTheme.spaces.medium, vertical = 1.dp),
                fontSize = MaterialTheme.typography.body2.fontSize,
                text = if (state.errorMessage == null) "" else "Error: " + state.errorMessage,
                color = if (state.errorMessage == null) Color.Transparent else Color.Red
            )
        }
    }
}