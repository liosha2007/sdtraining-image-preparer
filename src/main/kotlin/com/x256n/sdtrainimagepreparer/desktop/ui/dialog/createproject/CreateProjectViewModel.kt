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

    private val _state = mutableStateOf(CreateProjectState())
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
                    val isCaptionExtensionIncorrect =
                        event.captionExtension.isBlank() || event.captionExtension.trim() == "." || event.captionExtension.contains(
                            Regex("([/\\\\:*\"<>|])")
                        )
                    _state.value = state.value.copy(
                        captionExtension = if (!isCaptionExtensionIncorrect && !event.captionExtension.startsWith('.')) ".${event.captionExtension}" else event.captionExtension,
                        errorMessage = if (isCaptionExtensionIncorrect) "Caption files extension must not be empty!" else null,
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