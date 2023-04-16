package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.createproject

data class CreateProjectState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val imageDirectory: String? = "D:\\kotlin\\sdtraining-image-preparer\\test-project",
    val overrideExistingProject: Boolean = false,

    val captionExtension: String = "txt",
    val mergeExistingCaptionFiles: Boolean = true,
    val createCaptionsWhenAddingContent: Boolean = true,
    val targetImageResolution: Int = 512,
    val targetImageFormat: String = "png",

    val isProjectCreated: Boolean = false,
)