@file:OptIn(
    ExperimentalPathApi::class, ExperimentalPathApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTextApi::class, ExperimentalPathApi::class, ExperimentalPathApi::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeUiApi::class
)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x256n.sdtrainingimagepreparer.desktop.ui.component.pathPainter
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.ScreenMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.math.round

@Composable
fun CenterPreviewPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel, isShiftPressed: Boolean) {
    val log = remember { LoggerFactory.getLogger("CenterPreviewPanel") }
    val state by viewModel.state
    val coroutineScope = rememberCoroutineScope()
    var mainImagePainter by remember { mutableStateOf<ImageBitmap?>(null) }
    val textMeasure = rememberTextMeasurer()

    rememberSaveable(state.dataIndex, state.data) {
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

                mainImagePainter?.let { image ->
                    var previewSize by remember { mutableStateOf(IntSize.Zero) }

                    // size is changing by onSizeChanged
                    val (size, onSizeChanged) = remember { mutableStateOf(IntSize.Zero) }
                    rememberSaveable(/* window size was changed */size, /* image was changed */mainImagePainter) {
                        val imageRatio = image.width.toFloat() / image.height
                        var imageWidth = size.width
                        var imageHeight = (imageWidth / imageRatio).toInt()
                        if (imageHeight > size.height) {
                            imageHeight = size.height
                            imageWidth = (imageHeight * imageRatio).toInt()
                        }
                        previewSize = IntSize(imageWidth, imageHeight)
                        viewModel.sendEvent(
                            HomeEvent.ImageSizeChanged(
                                imageSize = Size(
                                    image.width.toFloat(),
                                    image.height.toFloat()
                                ),
                                imageScale = image.width.toFloat() / imageWidth
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged(onSizeChanged),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(previewSize.width.dp, previewSize.height.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .background(Color.Green)
                                    .fillMaxSize(),
                                painter = BitmapPainter(image),
                                contentDescription = state[state.dataIndex].imagePath.name,
                                contentScale = ContentScale.Fit
                            )
                            if (state.screenMode == ScreenMode.ResizeCrop) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .onPointerEvent(PointerEventType.Press) {
                                            viewModel.sendEvent(HomeEvent.CropActiveTypeChanged(this.currentEvent.changes.first().position))
                                        }
                                        .onPointerEvent(PointerEventType.Release) {
                                            viewModel.sendEvent(HomeEvent.CropActiveTypeChanged(null))
                                        }
                                        .onDrag { offset ->
                                            viewModel.sendEvent(
                                                HomeEvent.CropRectChanged(
                                                    offset = offset,
                                                    isShiftPressed = isShiftPressed
                                                )
                                            )
                                        },
                                ) {
                                    val scaledAreaRect = Rect(
                                        offset = state.scaledCropOffset,
                                        size = state.scaledCropSize
                                    )
                                    drawRect( // Full rect
                                        color = Color.Yellow.copy(alpha = 0.5f),
                                        topLeft = scaledAreaRect.topLeft, // values in pixels
                                        size = scaledAreaRect.size // values in pixels
                                    )
                                    drawRect( // top left
                                        color = Color.Red.copy(alpha = 0.5f),
                                        topLeft = scaledAreaRect.topLeft, // values in pixels
                                        size = Size(10f, 10f) // values in pixels
                                    )
                                    drawRect( // top right
                                        color = Color.Red.copy(alpha = 0.5f),
                                        topLeft = scaledAreaRect.topRight.copy(x = scaledAreaRect.topRight.x - 10f), // values in pixels
                                        size = Size(10f, 10f) // values in pixels
                                    )
                                    drawRect( // bottom left
                                        color = Color.Red.copy(alpha = 0.5f),
                                        topLeft = scaledAreaRect.bottomLeft.copy(y = scaledAreaRect.bottomLeft.y - 10f), // values in pixels
                                        size = Size(10f, 10f) // values in pixels
                                    )
                                    drawRect( // bottom right
                                        color = Color.Red.copy(alpha = 0.5f),
                                        topLeft = Offset(
                                            x = scaledAreaRect.bottomRight.x - 10f,
                                            y = scaledAreaRect.bottomRight.y - 10f
                                        ), // values in pixels
                                        size = Size(10f, 10f) // values in pixels
                                    )
                                    drawText(
                                        textMeasurer = textMeasure,
                                        text = "${round(state.cropSize.width).toInt()} x ${round(state.cropSize.height).toInt()}",
                                        style = TextStyle(
                                            fontSize = 8.sp,
                                            color = if (state.cropSize.width < 512 || state.cropSize.height < 512) Color.Red else Color.Black
                                        ),
                                        topLeft = Offset(scaledAreaRect.left + 10, scaledAreaRect.top + 10),
                                        maxSize = IntSize(
                                            scaledAreaRect.width.toInt(),
                                            scaledAreaRect.height.toInt()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}