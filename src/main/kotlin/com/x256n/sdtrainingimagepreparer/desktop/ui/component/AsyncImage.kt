package com.x256n.sdtrainingimagepreparer.desktop.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Path

@Composable
fun AsyncImage(
    load: suspend () -> ImageBitmap,
    painterFor: @Composable (ImageBitmap) -> Painter,
    initialImage: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: AsyncImageState by produceState<AsyncImageState>(AsyncImageState.Loading) {
        value = withContext(Dispatchers.IO) {
            try {
                AsyncImageState.Done(load())
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                AsyncImageState.Fail
            }
        }
    }
    when (image) {
        is AsyncImageState.Loading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            Image(
                painter = if (image is AsyncImageState.Done) painterFor((image as AsyncImageState.Done).image) else initialImage,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
            )
        }
    }
}

sealed class AsyncImageState {
    object Loading : AsyncImageState()
    data class Done(val image: ImageBitmap) : AsyncImageState()
    object Fail : AsyncImageState()
}

fun pathPainter(path: Path): ImageBitmap =
    path.toFile().inputStream().buffered().use(::loadImageBitmap)