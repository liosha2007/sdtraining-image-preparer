@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.pathPainter
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.simpleVerticalScrollbar
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import kotlin.io.path.ExperimentalPathApi

@Composable
fun LeftThumbnailsPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel, lazyState: LazyListState) {
    val log = remember { LoggerFactory.getLogger("LeftThumbnailsPanel") }
    val state by viewModel.state
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
    ) {
        var thumbnailSize by remember { mutableStateOf(IntSize.Zero) }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .simpleVerticalScrollbar(state = lazyState, isShowAlways = true, width = 1.dp, color = Color.Gray)
                .fillMaxSize()
                .onSizeChanged {
                    thumbnailSize = it
                },
            state = lazyState
        ) {
            itemsIndexed(state.data) { index, item ->
                var modifier: Modifier = Modifier
                var textColor = Color.Black
                if (index == state.dataIndex) {
                    modifier = Modifier
                        .background(Color.DarkGray)
                    textColor = Color.White
                }

                var thumbnailPainter by remember { mutableStateOf<ImageBitmap?>(null) }
                rememberSaveable(/* Image cropped */state.data, /* Thumbnails scrolled */
                    lazyState.firstVisibleItemIndex
                ) {
                    try {
                        coroutineScope.launch(Dispatchers.Main) {
                            thumbnailPainter = withContext(Dispatchers.IO) { pathPainter(item.thumbnailPath) }
                        }
                    } catch (e: Exception) {
                        log.error("Can't load thumbnail image: '${item.thumbnailPath}'", e)
                        thumbnailPainter = null
                    }
                }

                Column(
                    modifier = modifier
                        .padding(MaterialTheme.spaces.extraSmall)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (thumbnailPainter != null) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((thumbnailSize.width / 1.7).dp)
                                .clickable {
                                    viewModel.sendEvent(HomeEvent.ImageSelected(index))
                                },
                            painter = BitmapPainter(thumbnailPainter!!),
                            contentDescription = "Image $index",
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((thumbnailSize.width / 1.7).dp)
                                .clickable {
                                    viewModel.sendEvent(HomeEvent.ImageSelected(index))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                            )
                        }
                    }
                    Text(
                        modifier = Modifier,
                        text = item.imageName,
                        fontSize = 8.sp,
                        color = textColor
                    )
                    Text(
                        modifier = Modifier,
                        text = "${item.imageSize.width.toInt()} x ${item.imageSize.height.toInt()}",
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
        Text(
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 1.dp),
            text = "Images count: ${state.data.size}",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = MaterialTheme.typography.body2.fontSize,
            color = Color.DarkGray
        )
    }
}