package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.usecase.InitializeProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class SettingsViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("SettingsViewModel")

    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state


    fun onEvent(event: SettingsEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is SettingsEvent.SettingsDisplayed -> {
                    _state.value = state.value.copy(
                        isDebugMode = configManager.isDebugMode,
                        thumbnailsWidth = configManager.thumbnailsWidth,
                        thumbnailsFormat = configManager.thumbnailsFormat,
                        keywordsDelimiter = configManager.keywordsDelimiter,
                        openLastProjectOnStart = configManager.openLastProjectOnStart,
                        supportedImageFormats = configManager.supportedImageFormats.joinToString(","),
                        supportedCaptionExtensions = configManager.supportedCaptionExtensions.joinToString(","),
                    )
                }
                is SettingsEvent.IsDebugMode -> {
//                    _state.value = state.value.copy(
//                        isDebugMode = event.value
//                    )
                }
                is SettingsEvent.ThumbnailsWidth -> {
                    if (event.value.matches(Regex("\\d+"))) {
                        configManager.thumbnailsWidth = event.value.toInt()
                        _state.value = state.value.copy(
                            thumbnailsWidth = event.value.toInt()
                        )
                    }
                }
                is SettingsEvent.ThumbnailsFormat -> {
                    if (event.value.matches(Regex("[\\w,]+"))) {
                        configManager.thumbnailsFormat = event.value
                        _state.value = state.value.copy(
                            thumbnailsFormat = event.value
                        )
                    }
                }
                is SettingsEvent.KeywordsDelimiter -> {
                    configManager.keywordsDelimiter = event.value
                    _state.value = state.value.copy(
                        keywordsDelimiter = event.value
                    )
                }
                is SettingsEvent.OpenLastProjectOnStart -> {
                    configManager.openLastProjectOnStart = event.value
                    _state.value = state.value.copy(
                        openLastProjectOnStart = event.value
                    )
                }
                is SettingsEvent.SupportedImageFormats -> {
                    if (event.value.matches(Regex("[\\w,]+"))) {
                        configManager.supportedImageFormats = event.value.split(",")
                        _state.value = state.value.copy(
                            supportedImageFormats = event.value
                        )
                    }
                }
                is SettingsEvent.SupportedCaptionExtensions -> {
                    if (event.value.matches(Regex("[\\w,]+"))) {
                        configManager.supportedCaptionExtensions = event.value.split(",")
                        _state.value = state.value.copy(
                            supportedCaptionExtensions = event.value
                        )
                    }
                }

                else -> {
                    _log.warn("Unknown event: $event")
                }
            }
        }
    }
}