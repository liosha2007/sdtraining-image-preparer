package com.x256n.sdtrainimagepreparer.desktop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfig(
    val captionExtension: String = "txt",
    val mergeExistingCaptionFiles: Boolean = true,
    val mergeExistingTxtFiles: Boolean = true,
    val targetImageResolution: Int = 512,
    val supportedImageFormats: List<String> = arrayListOf("png", "jpg")
)