package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.CheckProjectUseCase
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension
import kotlin.io.path.name

class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val checkProject: CheckProjectUseCase
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("HomeViewModel")

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var loadImagesJob: Job? = null
    private val thumbnailsJob: MutableList<Job> = mutableListOf()

    fun onEvent(event: HomeEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            _state.value = state.value.copy(isLoading = true, errorMessage = null)
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

                else -> {
                    TODO("Not implemented: $event")
                }
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }

    private fun imageSelected(index: Int) {
        _state.value = state.value.copy(dataIndex = index)
        _log.debug("Selected image: index = $index, name = ${state.value.currentModel.imagePath.name}")
    }

    private suspend fun loadProject(projectDirectory: Path) {
        _state.value = state.value.copy(
            projectDirectory = null,
            isOpenProject = false
        )
        checkProject(projectDirectory)
        coroutineScope {
            loadImagesJob?.cancel()
            loadImagesJob = launch {
                Files.walkFileTree(projectDirectory, object : SimpleFileVisitor<Path>() {
                    val supportedFormats = arrayListOf("png", "jpg")
                    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        return if (!isActive) {
                            FileVisitResult.TERMINATE
                        } else if (file != null && !supportedFormats.contains(file.extension)) {
                            FileVisitResult.CONTINUE
                        } else {
                            file?.let { absoluteImagePath ->
                                val relativeImagePath = projectDirectory.relativize(absoluteImagePath)
                                val model = ImageModel(
                                    projectDirectory = projectDirectory,
                                    imagePath = relativeImagePath
                                )
                                if (Files.notExists(model.captionPath)) {
                                    Files.writeString(model.captionPath, "")
                                } else {
                                    model.captionContent = Files.readString(model.captionPath)
                                }
                                thumbnailsJob.add(
                                    launch {
                                        val fullThumbnailPath = projectDirectory.resolve(model.thumbnailPath)
                                        if (Files.notExists(fullThumbnailPath)) {
                                            if (Files.notExists(fullThumbnailPath.parent)) {
                                                Files.createDirectory(fullThumbnailPath.parent)
                                            }
                                            _log.debug("Create thumbnail")
                                            // Create thumbnail - fullThumbnailPath
                                        }
                                    }
                                )
                                _state.value = state.value.copy(
                                    data = state.value.data.toMutableList().apply {
                                        add(model)
                                    }
                                )
                            }
                            FileVisitResult.CONTINUE
                        }
                    }

                    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        return if (projectDirectory == dir) {
                            FileVisitResult.CONTINUE
                        } else {
                            FileVisitResult.SKIP_SUBTREE
                        }
                    }
                })
            }
        }

        _state.value = state.value.copy(projectDirectory = projectDirectory)
    }
}