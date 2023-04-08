package com.x256n.sdtrainimagepreparer.desktop.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectConfig(
    val captionExtension: String,
    val mergeExistingCaptionFiles: Boolean,
    val mergeExistingTxtFiles: Boolean
)