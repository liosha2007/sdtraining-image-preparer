package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
import org.koin.java.KoinJavaComponent
import java.awt.Cursor
import java.nio.file.Path
import kotlin.io.path.name

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun FrameWindowScope.HomeScreen(navigator: Navigator<Destinations>, dest: Destinations) {
//    val log = remember { LoggerFactory.getLogger("HomeScreen") }
    val viewModel by remember {
        KoinJavaComponent.inject<HomeViewModel>(HomeViewModel::class.java)
    }
    MenuBar {
        MainMenu(navigator, viewModel)
    }
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

    if (!state.isLoading) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spaces.small)
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spaces.extraSmall)
            ) {
                var explorerPanelWidth by remember { mutableStateOf(168.dp) }
                var previewPanelSize by remember { mutableStateOf(IntSize.Zero) }
                var tagsPanelWidth by remember { mutableStateOf(168.dp) }
                var captionPanelHeight by remember { mutableStateOf(64.dp) }
                val spacerSize = 10.dp

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(explorerPanelWidth)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        itemsIndexed(state.data) { index, item ->
                            AsyncImage(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onEvent(HomeEvent.ImageSelected(index))
                                    },
                                load = {
                                    pathPainter(item.imagePath)
                                },
                                initialImage = painterResource("icon.ico"),
                                painterFor = { remember { BitmapPainter(it) } },
                                contentDescription = "Image $index"
                            )
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
                        .background(Color.Yellow)
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
                            Image(
                                modifier = Modifier
                                    .fillMaxSize(),
                                painter = BitmapPainter(pathPainter(state.currentModel.imagePath)),
                                contentDescription = state.currentModel.imagePath.name
                            )
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
                            .background(Color.Cyan)
                            .height(captionPanelHeight)
                    ) {
                        Text(
                            text = "Caption"
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
                    Text(
                        text = "Tags"
                    )
                }
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