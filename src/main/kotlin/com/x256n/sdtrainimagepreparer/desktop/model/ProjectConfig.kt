package com.x256n.sdtrainimagepreparer.desktop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfig(
    val captionExtension: String = "txt",
    val mergeExistingCaptionFiles: Boolean = true,
    val mergeExistingTxtFiles: Boolean = true,
    val supportedImageFormats: List<String> = arrayListOf("png", "jpg")
)