package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

sealed class CreateProjectEvent {
    object CreateProjectDisplayed : CreateProjectEvent()

    data class ImagesDirectoryChanged(val path: String) : CreateProjectEvent()
    data class CaptionExtensionsChanged(val captionExtension: String) : CreateProjectEvent()
    data class OverrideExistingProject(val value: Boolean) : CreateProjectEvent()
    data class MergeExistingCaptionFiles(val value: Boolean) : CreateProjectEvent()
    data class CreateCaptionsWhenAddingContentChanged(val value: Boolean) : CreateProjectEvent()
    data class TargetImageResolutionChanged(val value: String) : CreateProjectEvent()
    data class TargetImageFormatChanged(val value: String) : CreateProjectEvent()

    object CreateProject : CreateProjectEvent()
}
