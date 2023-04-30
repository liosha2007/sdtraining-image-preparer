package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings

sealed class SettingsEvent {
    object SettingsDisplayed : SettingsEvent()
    data class IsDebugMode(val value: Boolean) : SettingsEvent()
    data class ThumbnailsWidth(val value: String) : SettingsEvent()
    data class ThumbnailsFormat(val value: String) : SettingsEvent()
    data class KeywordsDelimiter(val value: String) : SettingsEvent()
    data class OpenLastProjectOnStart(val value: Boolean) : SettingsEvent()
    data class SupportedImageFormats(val value: String) : SettingsEvent()
    data class SupportedCaptionExtensions(val value: String) : SettingsEvent()
}
