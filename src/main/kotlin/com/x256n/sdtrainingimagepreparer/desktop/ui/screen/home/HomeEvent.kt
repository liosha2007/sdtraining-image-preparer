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
    data class CreateProject(val projectDirectory: Path? = null) : HomeEvent()
    data class LoadProject(val projectDirectory: Path) : HomeEvent()
    data class Open(val path: Path? = null) : HomeEvent()
    object CloseProject : HomeEvent()
    object DropProject : HomeEvent()
    // endregion

    // region Image events
    object DeleteImage : HomeEvent()
    object ConvertImages : HomeEvent()
    data class ImageSizeChanged(val imageSize: Size, val imageScale: Float) : HomeEvent()
    data class FilesDropped(val filesList: List<String>) : HomeEvent()
    data class ImageSelected(val index: Int) : HomeEvent()
    object ShowNextImage : HomeEvent()
    object ShowPrevImage : HomeEvent()
    object SyncImages : HomeEvent()
    // endregion

    // region Captions events
    object CreateAllCaptions : HomeEvent()
    data class DeleteAllCaptions(val isDeleteOnlyEmpty: Boolean) : HomeEvent()
    data class CaptionContentChanged(val value: String) : HomeEvent()
    data class KeywordSelected(val keywordModel: KeywordModel) : HomeEvent()
    // endregion

    // region Image tools events
    data class EditModeClicked(val enable: Boolean) : HomeEvent()
    object CropApplyClicked : HomeEvent()
    data class ChangeAreaToSize(val targetSize: Float) : HomeEvent()
    object ChangeAreaToMax : HomeEvent()
    data class CropRectChanged(val offset: Offset, val isShiftPressed: Boolean) : HomeEvent()
    data class CropActiveTypeChanged(val position: Offset?) : HomeEvent()
    // endregion

    // region Keyboard events
    object EnterPressed : HomeEvent()
    object EscPressed : HomeEvent()
    object DeletePressed : HomeEvent()
    // endregion
}

sealed interface UIAction {
    data class CreateNewProjectYesNo(val path: Path): UIAction
    data class CreateProject(val path: Path? = null): UIAction
    object ChooseProjectDirectoryDialog: UIAction
}