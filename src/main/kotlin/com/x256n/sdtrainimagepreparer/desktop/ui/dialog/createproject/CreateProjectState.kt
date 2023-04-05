package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

data class CreateProjectState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val imageDirectory: String? = null,
    val captionExtension: String = ".txt",
    val isOverrideExistingProject: Boolean = false,
    val isMergeExistingCaptionFiles: Boolean = true,
    val isMergeExistingTxtFiles: Boolean = true
)
