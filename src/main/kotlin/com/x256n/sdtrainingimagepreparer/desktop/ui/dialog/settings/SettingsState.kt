package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings

data class SettingsState(
    val errorMessage: String? = null,
    val isDebugMode: Boolean = false,
    val thumbnailsWidth: Int = 168,
    val thumbnailsFormat: String = "png",
    val keywordsDelimiter: String = ",",
    val openLastProjectOnStart: Boolean = true,
    val supportedImageFormats: String = "png,jpg",
    val supportedCaptionExtensions: String = "txt,caption",
)
