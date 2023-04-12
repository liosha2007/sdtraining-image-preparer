package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.InitializeProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class CreateProjectViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val initializeProject: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("CreateProjectViewModel")

    private val _state = mutableStateOf(CreateProjectState(targetImageResolution = 512))
    val state: State<CreateProjectState> = _state

    fun onEvent(event: CreateProjectEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            _state.value = state.value.copy(isLoading = true, errorMessage = null)
            when (event) {
                is CreateProjectEvent.CreateProjectDisplayed -> {
                    _log.info("CreateProjectDisplayed")
                    _state.value = CreateProjectState()
                }
                is CreateProjectEvent.ImagesDirectoryChanged -> {
                    val newImagesDirectory = event.path.ifBlank { null }
                    val isDirectoryNotExist = newImagesDirectory == null || Files.notExists(Path.of(newImagesDirectory))
                    _state.value = state.value.copy(
                        imageDirectory = event.path.ifBlank { null },
                        errorMessage = if (isDirectoryNotExist) "Directory does not exist!" else null,
                    )
                }
                is CreateProjectEvent.CaptionExtensionsChanged -> {
                    if (event.captionExtension.trim().trim('.').isBlank()) {
                        _state.value = state.value.copy(
                            errorMessage = "Caption files extension must not be empty!"
                        )
                    } else if (event.captionExtension.contains(Regex("([/\\\\:*\"<>|])"))) {
                        _state.value = state.value.copy(
                            errorMessage = "Caption files extension must not contain next chars: /\\:*\"<>|!"
                        )
                    } else {
                        _state.value = state.value.copy(
                            captionExtension = event.captionExtension.trim().trimStart('.'),
                            errorMessage = null,
                        )
                    }
                }
                is CreateProjectEvent.TargetImageResolutionChanged -> {
                    try {
                        val targetResolution = event.value.toInt()
                        if (targetResolution >= 32 && targetResolution <= 8 * 1024) {
                            _state.value = state.value.copy(
                                targetImageResolution = targetResolution
                            )
                        } else {
                            _state.value = state.value.copy(
                                errorMessage = "Target image resolution must be more than 32 and less than ${8 * 1024}"
                            )
                        }
                    } catch (e: NumberFormatException) {
                        _state.value = state.value.copy(
                            errorMessage = "Target image resolution must be number, in pixels"
                        )
                    }
                }
                is CreateProjectEvent.MergeExistingCaptionFiles -> {
                    _state.value = state.value.copy(
                        mergeExistingCaptionFiles = event.value
                    )
                }
                is CreateProjectEvent.CreateCaptionsWhenAddingContentChanged -> {
                    _state.value = state.value.copy(
                        createCaptionsWhenAddingContent = event.value
                    )
                }
                is CreateProjectEvent.TargetImageFormatChanged -> {
                    _state.value = state.value.copy(
                        targetImageFormat = event.value
                    )
                }
                is CreateProjectEvent.OverrideExistingProject -> {
                    _state.value = state.value.copy(
                        overrideExistingProject = event.value
                    )
                }
                is CreateProjectEvent.CreateProject -> {
                    if (state.value.imageDirectory != null) {
                        withContext(dispatcherProvider.default) {
                            try {
                                initializeProject(
                                    captionExtension =state.value.captionExtension,
                                    imageDirectory = state.value.imageDirectory!!,
                                    mergeExistingCaptionFiles = state.value.mergeExistingCaptionFiles,
                                    createCaptionsWhenAddingContent = state.value.createCaptionsWhenAddingContent,
                                    targetImageResolution = state.value.targetImageResolution,
                                    targetImageFormat = state.value.targetImageFormat,
                                    overrideExistingProject = state.value.overrideExistingProject,
                                )
                                _state.value = state.value.copy(
                                    isProjectCreated = true
                                )
                            } catch (e: DisplayableException) {
                                _state.value = state.value.copy(
                                    errorMessage = e.message,
                                )
                            }
                        }
                    }
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }
}