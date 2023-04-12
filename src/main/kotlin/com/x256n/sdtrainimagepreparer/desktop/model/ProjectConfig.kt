package com.x256n.sdtrainimagepreparer.desktop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfig(
    val captionExtension: String = "txt",
    val mergeExistingCaptionFiles: Boolean = true,
    val createCaptionsWhenAddingContent: Boolean = true,
    val targetImageResolution: Int = 512,
    val targetImageFormat: String = "png",
)