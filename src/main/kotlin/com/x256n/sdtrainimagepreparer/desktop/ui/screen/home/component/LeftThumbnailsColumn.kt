@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainimagepreparer.desktop.ui.component.AsyncImage
import com.x256n.sdtrainimagepreparer.desktop.ui.component.pathPainter
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlin.io.path.ExperimentalPathApi

@Composable
fun LeftThumbnailsPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel, lazyState: LazyListState) {
    val state by viewModel.state
    Column(
        modifier = modifier
    ) {
        var thumbnailSize by remember { mutableStateOf(IntSize.Zero) }
        LazyColumn(
            modifier = Modifier
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

                Column(
                    modifier = modifier
                        .padding(MaterialTheme.spaces.extraSmall)
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
                        text = "${item.imageSize.width} x ${item.imageSize.height}",
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
}