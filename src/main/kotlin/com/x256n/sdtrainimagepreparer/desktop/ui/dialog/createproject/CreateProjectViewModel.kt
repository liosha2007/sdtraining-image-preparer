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
                    _state.value = state.value.copy(
                        targetImageResolution = event.targetImageResolution
                    )
                }
                is CreateProjectEvent.MergeExistingCaptionFiles -> {
                    _state.value = state.value.copy(
                        isMergeExistingCaptionFiles = event.value
                    )
                }
                is CreateProjectEvent.MergeExistingTxtFiles -> {
                    _state.value = state.value.copy(
                        isMergeExistingTxtFiles = event.value
                    )
                }
                is CreateProjectEvent.OverrideExistingProject -> {
                    _state.value = state.value.copy(
                        isOverrideExistingProject = event.value
                    )
                }
                is CreateProjectEvent.CreateProject -> {
                    if (state.value.imageDirectory != null) {
                        withContext(dispatcherProvider.default) {
                            try {
                                initializeProject(
                                    state.value.imageDirectory!!,
                                    state.value.isOverrideExistingProject,
                                    state.value.captionExtension,
                                    state.value.targetImageResolution,
                                    state.value.isMergeExistingCaptionFiles,
                                    state.value.isMergeExistingTxtFiles
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