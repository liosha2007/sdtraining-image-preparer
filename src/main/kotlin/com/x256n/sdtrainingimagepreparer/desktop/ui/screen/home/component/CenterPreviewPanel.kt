@file:OptIn(
    ExperimentalPathApi::class, ExperimentalPathApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTextApi::class, ExperimentalPathApi::class
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
    var mainImageScale by remember { mutableStateOf(1f) }
    var activeType by remember { mutableStateOf<ActiveType>(ActiveType.None) }
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

                    val (size, onSizeChanged) = remember { mutableStateOf(IntSize.Zero) }
                    rememberSaveable(size) {
                        val imageRatio = image.width.toFloat() / image.height
                        var imageWidth = size.width
                        var imageHeight = (imageWidth / imageRatio).toInt()
                        if (imageHeight > size.height) {
                            imageHeight = size.height
                            imageWidth = (imageHeight * imageRatio).toInt()
                        }
                        previewSize = IntSize(imageWidth, imageHeight)
                        mainImageScale = image.width.toFloat() / imageWidth
                        viewModel.sendEvent(
                            HomeEvent.ImageSizeChanged(
                                imageSize = Size(
                                    image.width.toFloat(),
                                    image.height.toFloat()
                                )
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
                            if (state.isEditMode) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .onPointerEvent(PointerEventType.Press) {
                                            val scaledAreaRect = Rect(
                                                left = state.cropOffset.x / mainImageScale,
                                                top = state.cropOffset.y / mainImageScale,
                                                right = (state.cropOffset.x + state.cropSize.width) / mainImageScale,
                                                bottom = (state.cropOffset.y + state.cropSize.height) / mainImageScale,
                                            )
                                            activeType =
                                                ActiveType.getActiveType(
                                                    this.currentEvent.changes.first().position,
                                                    scaledAreaRect
                                                )

                                        }
                                        .onPointerEvent(PointerEventType.Release) {
                                            activeType = ActiveType.None
                                        }
                                        .onDrag { offset ->
                                            val scaledAreaRect = Rect(
                                                left = state.cropOffset.x / mainImageScale,
                                                top = state.cropOffset.y / mainImageScale,
                                                right = (state.cropOffset.x + state.cropSize.width) / mainImageScale,
                                                bottom = (state.cropOffset.y + state.cropSize.height) / mainImageScale,
                                            )
                                            var newAreaRectangle = when (activeType) {
                                                is ActiveType.LeftTop -> scaledAreaRect.copy(
                                                    left = scaledAreaRect.left + if (isShiftPressed) offset.x else (offset.x + offset.y) / 2,
                                                    top = scaledAreaRect.top + if (isShiftPressed) offset.y else (offset.x + offset.y) / 2
                                                )
                                                is ActiveType.RightTop -> scaledAreaRect.copy(
                                                    right = scaledAreaRect.right + if (isShiftPressed) offset.x else (offset.x + offset.y) / 2,
                                                    top = scaledAreaRect.top + if (isShiftPressed) offset.y else (offset.x + offset.y) / 2
                                                )
                                                is ActiveType.RightBottom -> scaledAreaRect.copy(
                                                    right = scaledAreaRect.right + if (isShiftPressed) offset.x else (offset.x + offset.y) / 2,
                                                    bottom = scaledAreaRect.bottom + if (isShiftPressed) offset.y else (offset.x + offset.y) / 2
                                                )
                                                is ActiveType.LeftBottom -> scaledAreaRect.copy(
                                                    left = scaledAreaRect.left + if (isShiftPressed) offset.x else (offset.x + offset.y) / 2,
                                                    bottom = scaledAreaRect.bottom + if (isShiftPressed) offset.y else (offset.x + offset.y) / 2
                                                )
                                                is ActiveType.Center -> scaledAreaRect.copy(
                                                    top = scaledAreaRect.top + offset.y,
                                                    bottom = scaledAreaRect.bottom + offset.y,
                                                    left = scaledAreaRect.left + offset.x,
                                                    right = scaledAreaRect.right + offset.x
                                                )
                                                else -> scaledAreaRect
                                            }
                                            newAreaRectangle =
                                                    // Prevent left corner is on right of right corner and so on
                                                if (newAreaRectangle.right - newAreaRectangle.left < 25) { // left must not be more right
                                                    if (activeType == ActiveType.LeftTop || activeType == ActiveType.LeftBottom) {
                                                        newAreaRectangle.copy(left = newAreaRectangle.right - 25)
                                                    } else {
                                                        newAreaRectangle.copy(right = newAreaRectangle.left + 25)
                                                    }
                                                } else if (newAreaRectangle.bottom - newAreaRectangle.top < 25) { // top must not be more bottom
                                                    if (activeType == ActiveType.LeftTop || activeType == ActiveType.LeftBottom) {
                                                        newAreaRectangle.copy(top = newAreaRectangle.bottom - 25)
                                                    } else {
                                                        newAreaRectangle.copy(bottom = newAreaRectangle.top + 25)
                                                    }
                                                } else newAreaRectangle

                                            newAreaRectangle =
                                                    // Prevent area to be out of image
                                                if (newAreaRectangle.left < 0) { // area must be inside image
                                                    newAreaRectangle.copy(
                                                        left = 0f,
                                                        right = newAreaRectangle.right + (newAreaRectangle.left * -1)
                                                    )
                                                } else if (newAreaRectangle.right > state.mainImageSize.width / mainImageScale) { // area must be inside image
                                                    newAreaRectangle.copy(
                                                        right = state.mainImageSize.width / mainImageScale,
                                                        left = newAreaRectangle.left - (newAreaRectangle.right - state.mainImageSize.width / mainImageScale)
                                                    )
                                                } else if (newAreaRectangle.top < 0) { // area must be inside image
                                                    newAreaRectangle.copy(
                                                        top = 0f,
                                                        bottom = newAreaRectangle.bottom + (newAreaRectangle.top * -1)
                                                    )
                                                } else if (newAreaRectangle.bottom > state.mainImageSize.height / mainImageScale) { // area must be inside image
                                                    newAreaRectangle.copy(
                                                        bottom = state.mainImageSize.height / mainImageScale,
                                                        top = newAreaRectangle.top - (newAreaRectangle.bottom - state.mainImageSize.height / mainImageScale)
                                                    )
                                                } else newAreaRectangle

                                            viewModel.sendEvent(
                                                HomeEvent.CropRectChanged(
                                                    offset = Offset(
                                                        newAreaRectangle.left * mainImageScale,
                                                        newAreaRectangle.top * mainImageScale
                                                    ),
                                                    size = Size(
                                                        (newAreaRectangle.right - newAreaRectangle.left) * mainImageScale,
                                                        (newAreaRectangle.bottom - newAreaRectangle.top) * mainImageScale
                                                    )
                                                )
                                            )
                                        },
                                ) {
                                    val scaledAreaRect = Rect(
                                        left = state.cropOffset.x / mainImageScale,
                                        top = state.cropOffset.y / mainImageScale,
                                        right = (state.cropOffset.x + state.cropSize.width) / mainImageScale,
                                        bottom = (state.cropOffset.y + state.cropSize.height) / mainImageScale,
                                    )
                                    drawRect(
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
                                            y = scaledAreaRect.bottomRight.y - 10
                                        ), // values in pixels
                                        size = Size(10f, 10f) // values in pixels
                                    )
                                    drawText(
                                        textMeasurer = textMeasure,
                                        text = "${round((scaledAreaRect.width) * mainImageScale).toInt()} x ${round((scaledAreaRect.height) * mainImageScale).toInt()}",
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

sealed class ActiveType {
    object None : ActiveType()
    object Center : ActiveType()
    object LeftTop : ActiveType()
    object RightTop : ActiveType()
    object RightBottom : ActiveType()
    object LeftBottom : ActiveType()

    companion object {
        fun getActiveType(cursorPosition: Offset, areaPosition: Rect): ActiveType {
            return if (areaPosition.copy(right = areaPosition.left + 10, bottom = areaPosition.top + 10).contains(cursorPosition)) {
                LeftTop
            } else if (areaPosition.copy(left = areaPosition.right - 10, bottom = areaPosition.top + 10).contains(cursorPosition)) {
                RightTop
            } else if (areaPosition.copy(left = areaPosition.right - 10, top = areaPosition.bottom - 10).contains(cursorPosition)) {
                RightBottom
            } else if (areaPosition.copy(right = areaPosition.left + 10, top = areaPosition.bottom - 10).contains(cursorPosition)) {
                LeftBottom
            } else if (areaPosition.contains(cursorPosition)) {
                return Center
            } else {
                None
            }
        }
    }
}