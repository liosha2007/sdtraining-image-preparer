package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

sealed class HomeEvent {
    object HomeDisplayed : HomeEvent()
    data class LoadProject(val projectDirectory: Path) : HomeEvent()
    object OpenProject : HomeEvent()
    data class ImageSelected(val index: Int) : HomeEvent()

    object ShowNextImage : HomeEvent()
    object ShowPrevImage : HomeEvent()

    data class KeywordSelected(val keywordModel: KeywordModel) : HomeEvent()

    data class CaptionContentChanged(val value: String) : HomeEvent()

    data class EditModeClicked(val enable: Boolean) : HomeEvent()
    object CropApplyClicked : HomeEvent()
    data class ChangeAreaToSize(val targetSize: Float) : HomeEvent()

    data class CropRectChanged(val offset: Offset, val size: Size) : HomeEvent()
    data class MainImageScaleChanged(val imageSize: Size) : HomeEvent()
}
