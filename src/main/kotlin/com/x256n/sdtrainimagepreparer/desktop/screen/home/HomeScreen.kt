package com.x256n.sdtrainimagepreparer.desktop.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import java.awt.Cursor

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigator: Navigator<Destinations>) {
//    val LOG = remember { LoggerFactory.getLogger("HomeScreen") }
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.HomeDisplayed)
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