package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.*
import com.x256n.sdtrainingimagepreparer.desktop.usecase.CropResizeImageUseCase
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CropHomeEventHandler(
    private val cropResizeImage: CropResizeImageUseCase,
) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        when (event) {
            is HomeEvent.ImageCropModeClicked -> toggleImageCropMode(event, state)
            is HomeEvent.ImageCropApplyClicked -> imageCrop(state)
            is HomeEvent.ChangeAreaToSize -> changeAreaSize(event, state)
            is HomeEvent.ChangeAreaToMax -> changeAreaToMax(state)
            is HomeEvent.CropRectChanged -> changeCropRectangle(event, state)
            is HomeEvent.CropActiveTypeChanged -> changeCropActiveType(event, state)
            else -> throw DisplayableException("Unexpected application state: 7c0e742c3764($event)")
        }
    }


    private fun toggleImageCropMode(event: HomeEvent.ImageCropModeClicked, state: MutableState<HomeState>) {
        state.value = state.value.copy(
            screenMode = if (event.enable) ScreenMode.ImageCrop else ScreenMode.Default,
            cropOffset = if (event.enable) state.value.cropOffset else Offset(0f, 0f),
            cropSize = if (event.enable) state.value.cropSize else Size(
                min(512f, state.value.realImageSize.width),
                min(512f, state.value.realImageSize.height)
            )
        )
    }

    private suspend fun imageCrop(state: MutableState<HomeState>) {
        val imageScale = state.value.imageScale
        val cropOffset = state.value.cropOffset.copy(
            x = state.value.cropOffset.x,
            y = state.value.cropOffset.y,
        )
        val cropSize = state.value.cropSize.copy(
            width = state.value.cropSize.width,
            height = state.value.cropSize.height
        )

        _log.debug("cropResizeImage: offset = $cropOffset, size = $cropSize, scale = $imageScale")

        val imageModel = state.value.data[state.value.dataIndex]
        val newImageModel = cropResizeImage(
            imageModel,
            cropOffset,
            cropSize
        )
        state.value = state.value.copy(
            data = state.value.data.map { if (it == imageModel) newImageModel else it },
            screenMode = ScreenMode.Default
        )
    }

    private fun changeAreaSize(event: HomeEvent.ChangeAreaToSize, state: MutableState<HomeState>) {
        var x = state.value.cropOffset.x
        var y = state.value.cropOffset.y
        var width = event.targetSize
        var height = event.targetSize
        val imageWidth = state.value.realImageSize.width
        val imageHeight = state.value.realImageSize.height

        if (width > imageWidth) {
            x = 0f
            width = imageWidth
        } else if (x + width > imageWidth) {
            x = imageWidth - width
        }

        if (height > imageHeight) {
            y = 0f
            height = imageHeight
        } else if (y + height > imageHeight) {
            y = imageHeight - height
        }

        state.value = state.value.copy(
            cropOffset = Offset(x, y),
            cropSize = Size(width, height)
        )
    }

    private fun changeAreaToMax(state: MutableState<HomeState>) {
        val imageWidth = state.value.realImageSize.width
        val imageHeight = state.value.realImageSize.height

        state.value = state.value.copy(
            cropOffset = Offset(0f, 0f),
            cropSize = Size(min(imageWidth, imageHeight), min(imageWidth, imageHeight))
        )
    }

    private fun changeCropRectangle(event: HomeEvent.CropRectChanged, state: MutableState<HomeState>) {
        val imageScale = state.value.imageScale
        val realImageSize = state.value.realImageSize
        val cropRect = Rect(state.value.cropOffset, state.value.cropSize)
        val activeType = state.value.cropActiveType
        val isShiftPressed = event.isShiftPressed
        val offset = event.offset.copy(
            event.offset.x * imageScale,
            event.offset.y * imageScale
        )

        var cropAreaRect = when (activeType) {
            is ActiveType.LeftTop -> calculateTopBottomDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.left + offsetHorz > 0 && cropRect.top + offsetVert > 0
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        left = cropRect.left + offsetHorz,
                        top = cropRect.top + offsetVert,
                    )
                })

            is ActiveType.RightTop -> calculateBottomTopDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.top + offsetVert > 0 && cropRect.right + offsetHorz < realImageSize.width
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        top = cropRect.top + offsetVert,
                        right = cropRect.right + offsetHorz,
                    )
                })

            is ActiveType.RightBottom -> calculateTopBottomDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.right + offsetHorz < realImageSize.width && cropRect.bottom + offsetVert < realImageSize.height
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        right = cropRect.right + offsetHorz,
                        bottom = cropRect.bottom + offsetVert,
                    )
                })

            is ActiveType.LeftBottom -> calculateBottomTopDiagonalOffsets(offset, isShiftPressed,
                isInsideImage = { offsetHorz, offsetVert ->
                    cropRect.left + offsetHorz > 0 && cropRect.bottom + offsetVert < realImageSize.height
                },
                modifyRect = { offsetHorz, offsetVert ->
                    cropRect.copy(
                        left = cropRect.left + offsetHorz,
                        bottom = cropRect.top + cropRect.height + offsetVert,
                    )
                })

            is ActiveType.Center -> {
                cropRect.copy(
                    left = cropRect.left + offset.x,
                    top = cropRect.top + offset.y,
                    right = cropRect.right + offset.x,
                    bottom = cropRect.bottom + offset.y,
                )
            }

            else -> {
                cropRect
            }
        }

        // Prevent area to be out of image
        cropAreaRect = keepAreaInsideImage(cropAreaRect, realImageSize)

        val scaledMinSize = 25 * imageScale

        // Prevent left corner is on right of right corner and so on
        cropAreaRect = if (cropAreaRect.right - cropAreaRect.left < scaledMinSize) { // left must not be more right
            if (activeType == ActiveType.LeftTop || activeType == ActiveType.LeftBottom) {
                cropAreaRect.copy(left = cropAreaRect.right - scaledMinSize)
            } else {
                cropAreaRect.copy(right = cropAreaRect.left + scaledMinSize)
            }
        } else cropAreaRect

        cropAreaRect = if (cropAreaRect.bottom - cropAreaRect.top < scaledMinSize) { // top must not be more bottom
            if (activeType == ActiveType.LeftTop || activeType == ActiveType.RightTop) {
                cropAreaRect.copy(top = cropAreaRect.bottom - scaledMinSize)
            } else {
                cropAreaRect.copy(bottom = cropAreaRect.top + scaledMinSize)
            }
        } else cropAreaRect

        state.value = state.value.copy(
            cropOffset = cropAreaRect.topLeft,
            cropSize = cropAreaRect.size
        )
    }

    private fun calculateTopBottomDiagonalOffsets(
        offset: Offset, isShiftPressed: Boolean,
        isInsideImage: (offsetHorz: Float, offsetVert: Float) -> Boolean,
        modifyRect: (offsetHorz: Float, offsetVert: Float) -> Rect
    ): Rect {
        var offsetHorz = offset.x
        var offsetVert = offset.y
        if (!isShiftPressed) {
            val vector = (abs(offset.x) + abs(offset.y)) / 2
            if (offset.x < 0 || offset.y < 0) {
                offsetHorz = -vector
                offsetVert = -vector
            } else {
                offsetHorz = vector
                offsetVert = vector
            }
        }
        return if (isInsideImage(offsetHorz, offsetVert)) {
            modifyRect(offsetHorz, offsetVert)
        } else {
            // Keep inside image
            modifyRect(0f, 0f)
        }
    }

    private fun calculateBottomTopDiagonalOffsets(
        offset: Offset, isShiftPressed: Boolean,
        isInsideImage: (offsetHorz: Float, offsetVert: Float) -> Boolean,
        modifyRect: (offsetHorz: Float, offsetVert: Float) -> Rect
    ): Rect {
        var offsetHorz = offset.x
        var offsetVert = offset.y
        if (!isShiftPressed) {
            val vector = (abs(offset.x) + abs(offset.y)) / 2
            if (offset.x < 0) {
                offsetHorz = -vector
                offsetVert = vector
            } else {
                offsetHorz = vector
                offsetVert = -vector
            }
        }
        return if (isInsideImage(offsetHorz, offsetVert)) {
            modifyRect(offsetHorz, offsetVert)
        } else {
            // Keep inside image
            modifyRect(0f, 0f)
        }
    }

    private fun keepAreaInsideImage(
        cropAreaRect: Rect,
        realImageSize: Size
    ): Rect { // Check!! is this method still required
        var fixedRect = cropAreaRect
        // Prevent area to be out of image
        if (fixedRect.left < 0) {
            fixedRect = fixedRect.copy(
                left = 0f,
                right = min(fixedRect.width, realImageSize.width)
            )
        }
        if (fixedRect.top < 0) {
            fixedRect = fixedRect.copy(
                top = 0f,
                bottom = min(fixedRect.height, realImageSize.height)
            )
        }
        if (fixedRect.right > realImageSize.width) {
            fixedRect = fixedRect.copy(
                right = realImageSize.width,
                left = max(0f, realImageSize.width - fixedRect.width)
            )
        }
        if (fixedRect.bottom > realImageSize.height) {
            fixedRect = fixedRect.copy(
                bottom = realImageSize.height,
                top = max(0f, realImageSize.height - fixedRect.height)
            )
        }

        return fixedRect
    }

    private fun changeCropActiveType(event: HomeEvent.CropActiveTypeChanged, state: MutableState<HomeState>) {
        val rectOffset = state.value.cropOffset
        val rectSize = state.value.cropSize
        val imageScale = state.value.imageScale

        val cropActiveType = if (event.position != null) {
            val scaledAreaRect = Rect(
                left = rectOffset.x / imageScale,
                top = rectOffset.y / imageScale,
                right = (rectOffset.x + rectSize.width) / imageScale,
                bottom = (rectOffset.y + rectSize.height) / imageScale,
            )
            ActiveType.getActiveType(
                event.position,
                scaledAreaRect
            )
        } else ActiveType.None

        _log.debug("cropActiveType = $cropActiveType")
        state.value = state.value.copy(cropActiveType = cropActiveType)
    }

}