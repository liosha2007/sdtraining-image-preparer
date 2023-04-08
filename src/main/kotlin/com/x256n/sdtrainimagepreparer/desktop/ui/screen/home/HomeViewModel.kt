package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.Constants
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
import kotlin.io.path.nameWithoutExtension

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

                else -> {
                    TODO("Not implemented: $event")
                }
            }
            _state.value = state.value.copy(isLoading = false)
        }
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
                    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        return if (!isActive) {
                            FileVisitResult.TERMINATE
                        } else {
                            file?.let { absoluteImagePath ->
                                val relativeImagePath = projectDirectory.relativize(absoluteImagePath)
                                val model = ImageModel(
                                    imagePath = absoluteImagePath,//relativeImagePath,
                                    thumbnailPath = Path.of(Constants.PROJECT_DIRECTORY_NAME)
                                        .resolve(Constants.THUMBNAILS_DIRECTORY_NAME)
                                        .resolve(relativeImagePath),
                                    captionPath = projectDirectory.resolve("${relativeImagePath.nameWithoutExtension}.txt")
                                )
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