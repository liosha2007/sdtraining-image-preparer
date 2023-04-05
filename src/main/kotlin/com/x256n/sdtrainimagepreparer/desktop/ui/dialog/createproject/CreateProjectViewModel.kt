package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.Constants
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.DoSampleModelUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class CreateProjectViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: DoSampleModelUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("CreateProjectViewModel")

    private val _state = mutableStateOf(CreateProjectState(isMergeExistingCaptionFiles = true, isMergeExistingTxtFiles = true))
    val state: State<CreateProjectState> = _state


    fun onEvent(event: CreateProjectEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is CreateProjectEvent.CreateProjectDisplayed -> {
                    _log.info(doSampleModel())
                }
                is CreateProjectEvent.ImagesDirectoryChanged -> {
                    val newImagesDirectory = event.path.ifBlank { null }
                    val isDirectoryNotExist = newImagesDirectory == null || Files.notExists(Path.of(newImagesDirectory))
                    _state.value = state.value.copy(
                        imageDirectory = event.path.ifBlank { null },
                        errorMessage = if (isDirectoryNotExist) "Directory does not exist!" else null,
                        isOverrideExistingProject = !isDirectoryNotExist && Files.exists(
                            Path.of(newImagesDirectory!!).resolve(Constants.PROJECT_DIRECTORY_NAME)
                        )
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
                is CreateProjectEvent.CreateProject -> {
                    // TODO:
                    TODO("Not implemented")
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
        }
    }
}