@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.x256n.sdtrainimagepreparer.desktop.ui.component.pathPainter
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name

@Composable
fun CenterPreviewPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val state by viewModel.state
    val coroutineScope = rememberCoroutineScope()
    var mainImagePainter by remember { mutableStateOf<ImageBitmap?>(null) }

    rememberSaveable(state.dataIndex) {
        if (state.hasData) {
            coroutineScope.launch(Dispatchers.IO) {
                mainImagePainter = pathPainter(state[state.dataIndex].absoluteImagePath)
            }
        }
    }

    Row(
        modifier = modifier
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
}