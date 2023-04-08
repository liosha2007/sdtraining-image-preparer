package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.chrynan.navigation.ExperimentalNavigationApi
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.component.MainMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import java.awt.Cursor
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.inputStream

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
                val spacerWidth = 10.dp

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.Blue)
                        .width(explorerPanelWidth)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        itemsIndexed(state.data) { index, item ->
                            AsyncImage(
                                load = {
                                    pathPainter(item.imagePath)
                                },
                                painterFor = { remember { BitmapPainter(it) } },
                                contentDescription = "Image $index"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxHeight()
                    .width(spacerWidth)
                    .pointerHoverIcon(icon = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            matcher = PointerMatcher.Primary
                        ) {
                            if (previewPanelSize.width.dp > spacerWidth || it.x < 0) {
                                explorerPanelWidth += it.x.dp
                            }
                        }
                    })
                Column(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .fillMaxHeight()
                        .onSizeChanged {
                            previewPanelSize = it
                        }
                        .weight(1f)
                ) {
                    state.projectDirectory?.let {
                        Text(
                            text = it.toString()
                        )
                    }
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

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

fun pathPainter(path: Path): ImageBitmap =
    path.toFile().inputStream().buffered().use(::loadImageBitmap)