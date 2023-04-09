package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.usecase.CheckProjectUseCase
import com.x256n.sdtrainimagepreparer.desktop.usecase.LoadImageModelsUseCase
import com.x256n.sdtrainimagepreparer.desktop.usecase.ReadCaptionUseCase
import com.x256n.sdtrainimagepreparer.desktop.usecase.RemoveIncorrectThumbnailsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.name

class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val checkProject: CheckProjectUseCase,
    private val loadImageModels: LoadImageModelsUseCase,
    private val readCaption: ReadCaptionUseCase,
    private val removeIncorrectThumbnails: RemoveIncorrectThumbnailsUseCase
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("HomeViewModel")

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    fun onEvent(event: HomeEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            _state.value = state.value.copy(isLoading = true, errorMessage = null)
            try {
                when (event) {
                    is HomeEvent.HomeDisplayed -> {
                        _log.info("HomeDisplayed")
                    }
                    is HomeEvent.OpenProject -> {
                        _state.value = state.value.copy(
                            isOpenProject = true
                        )
                    }
                    is HomeEvent.LoadProject -> {
                        loadProject(event.projectDirectory)
                    }
                    is HomeEvent.ImageSelected -> {
                        imageSelected(event.index)
                    }
                    is HomeEvent.ShowNextImage -> {
                        imageSelected(if (state.value.dataIndex == state.value.data.lastIndex) 0 else state.value.dataIndex + 1)
                    }
                    is HomeEvent.ShowPrevImage -> {
                        imageSelected(if (state.value.dataIndex == 0) state.value.data.lastIndex else state.value.dataIndex - 1)
                    }

                    else -> {
                        TODO("Not implemented: $event")
                    }
                }
            } catch (e: DisplayableException) {
                _state.value = state.value.copy(errorMessage = e.message)
            } catch (e: Exception) {
                _log.error("Unexpected exception!", e)
                _state.value = state.value.copy(errorMessage = "Unexpected exception happened: ${e.message}")
            } finally {
                _state.value = state.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun imageSelected(index: Int) {
        _state.value = state.value.copy(
            dataIndex = index,
            captionContent = readCaption(state.value[index])
        )
        _log.debug("Selected image: index = $index, name = ${state.value[index].imagePath.name}")
    }

    private suspend fun loadProject(projectDirectory: Path) {
        _state.value = state.value.copy(
            projectDirectory = null,
            isOpenProject = false
        )
        checkProject(projectDirectory)

        removeIncorrectThumbnails(projectDirectory)

        loadImageModels(projectDirectory) { model ->
            _state.value = state.value.copy(
                data = state.value.data.toMutableList().apply {
                    add(model)
                    sortBy { it.imageName }
                }
            )
        }

        _state.value = state.value.copy(
            projectDirectory = projectDirectory,
            dataIndex = 0
        )
    }
}