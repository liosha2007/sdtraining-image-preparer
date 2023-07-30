package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainingimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

data class HomeState(
    val isLoading: Boolean = false,
    val status: Status = Status.None,
    val projectDirectory: Path? = null,
    val data: List<ImageModel> = emptyList(),
    val dataIndex: Int = -1,
    val captionContent: String = "",
    val keywordList: List<KeywordModel> = emptyList(),
    val screenMode: ScreenMode = ScreenMode.Default,

    // How the image was changed to be displayed
    val imageScale: Float = 1f,

    val realImageSize: Size = Size(0f, 0f),
    val cropOffset: Offset = Offset(0f, 0f),
    val cropSize: Size = Size(512f, 512f),
    val cropActiveType: ActiveType = ActiveType.None
) {
    operator fun get(index: Int): ImageModel {
        return data[index]
    }

    val hasData get() = data.isNotEmpty()

    val selectedImageModel get() = data[dataIndex]

    val hasKeywords get() = keywordList.isNotEmpty()

    val imageDetails
        get() =
            if (hasData) "${this[dataIndex].imageSize.width.toInt()} x ${this[dataIndex].imageSize.height.toInt()} - ${this[dataIndex].imageName}" else ""

    val isProjectLoaded get() = projectDirectory != null

    val scaledCropOffset
        get() = cropOffset.copy(
            x = cropOffset.x / imageScale,
            y = cropOffset.y / imageScale
        )

    val scaledCropSize
        get() = cropSize.copy(
            width = cropSize.width / imageScale,
            height = cropSize.height / imageScale
        )
}

sealed class ScreenMode {
    object Default: ScreenMode()
    object ResizeCrop: ScreenMode()
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

sealed class Status(val text: String) {
    object None: Status("")
    class Error(data: String): Status(data)
    class Info(data: String): Status(data)
}