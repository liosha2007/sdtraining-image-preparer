package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

sealed class HomeEvent {
    // region Application events
    object HomeDisplayed : HomeEvent()
    data class Exit(val isConfirmed: Boolean = false) : HomeEvent()
    // endregion

    // region Project events
    data class LoadProject(val projectDirectory: Path) : HomeEvent()
    object OpenProject : HomeEvent()
    object CloseProject : HomeEvent()
    object DropProject : HomeEvent()
    // endregion

    // region Image events
    object DeleteImage : HomeEvent()
    data class ImageSizeChanged(val imageSize: Size) : HomeEvent()
    data class ImageSelected(val index: Int) : HomeEvent()
    object ShowNextImage : HomeEvent()
    object ShowPrevImage : HomeEvent()
    // endregion

    // region Captions events
    object CreateAllCaptions : HomeEvent()
    object DeleteAllCaptions : HomeEvent()
    data class CaptionContentChanged(val value: String) : HomeEvent()
    data class KeywordSelected(val keywordModel: KeywordModel) : HomeEvent()
    // endregion

    // region Image tools events
    data class EditModeClicked(val enable: Boolean) : HomeEvent()
    object CropApplyClicked : HomeEvent()
    data class ChangeAreaToSize(val targetSize: Float) : HomeEvent()
    data class CropRectChanged(val offset: Offset, val size: Size) : HomeEvent()
    // endregion
}
