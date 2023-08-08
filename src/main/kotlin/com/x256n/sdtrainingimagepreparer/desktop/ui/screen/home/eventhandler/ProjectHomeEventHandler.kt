@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.UIAction
import com.x256n.sdtrainingimagepreparer.desktop.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi

class ProjectHomeEventHandler(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val checkProject: CheckProjectValidUseCase,
    private val loadImageModels: LoadImageModelsUseCase,
    private val isProject: IsProjectUseCase,
    private val createNewAndMergeExistingCaptions: CreateNewAndMergeExistingCaptionsUseCase,
    private val removeIncorrectThumbnails: RemoveIncorrectThumbnailsUseCase,
    private val extractCaptionKeywords: ExtractCaptionKeywordsUseCase,
    private val dropProject: DropProjectUseCase,
    private val joinCaption: JoinCaptionUseCase,
) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        when (event) {
            is HomeEvent.LoadProject -> loadProject(event, state, sendEvent)
            is HomeEvent.CreateProject -> createProject(event, uiActionChannel)
            is HomeEvent.Open -> showOpenProjectDialog(uiActionChannel)
            is HomeEvent.CloseProject -> closeProject(state)
            is HomeEvent.DropProject -> closeAndDropProject(state, sendEvent)
            is HomeEvent.FilesDropped -> handleDragAndDrop(event, state, uiActionChannel)
            else -> throw DisplayableException("Unexpected application state: 24cf91708f36($event)")
        }
    }

    private suspend fun loadProject(
        event: HomeEvent.LoadProject,
        state: MutableState<HomeState>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        state.value.projectDirectory?.let {
            // state.value.projectDirectory == null means there is no opened project
            sendEvent(HomeEvent.CloseProject)
        }

        val projectDirectory = event.projectDirectory

        checkAndOpenProject(projectDirectory, state)
    }

    private suspend fun createProject(event: HomeEvent.CreateProject, uiActionChannel: Channel<UIAction>) {
        uiActionChannel.send(UIAction.CreateProject(event.projectDirectory))
    }

    private suspend fun checkAndOpenProject(projectDirectory: Path, state: MutableState<HomeState>) {
        checkProject(projectDirectory)

        removeIncorrectThumbnails(projectDirectory)

        val data = loadImageModels(projectDirectory)
        val dataIndex = if (state.value.dataIndex == -1) 0 else state.value.dataIndex

        createNewAndMergeExistingCaptions(projectDirectory, data)

        val keywordMap = data.map { extractCaptionKeywords(it) }
            .flatten()
            .toSet()
            .toList()

        val currentCaptionKeywords = if (data.isNotEmpty()) extractCaptionKeywords(data[dataIndex]) else emptyList()

        state.value = state.value.copy(
            projectDirectory = projectDirectory,
            data = data,
            dataIndex = dataIndex,
            keywordList = keywordMap.map {
                it.copy(isAdded = currentCaptionKeywords.contains(it))
            },
            captionContent = joinCaption(currentCaptionKeywords.map { it.keyword })
        )

        configManager.lastProjectPath = projectDirectory.toAbsolutePath().normalize().toString()
    }

    private suspend fun showOpenProjectDialog(uiActionChannel: Channel<UIAction>) {
        uiActionChannel.send(UIAction.ChooseProjectDirectoryDialog)
    }

    private fun closeProject(state: MutableState<HomeState>) {
        state.value = HomeState()
    }

    private suspend fun closeAndDropProject(state: MutableState<HomeState>, sendEvent: (HomeEvent) -> Unit) {
        state.value.projectDirectory?.let { projectDirectory ->
            sendEvent(HomeEvent.CloseProject)
            dropProject(projectDirectory)
        }
    }

    private suspend fun handleDragAndDrop(
        event: HomeEvent.FilesDropped,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>
    ) {
        withContext(dispatcherProvider.io) {
            val filePath = event.filesList.map { Paths.get(URI(it)) }
            if (filePath.size == 1 && Files.isDirectory(filePath.first())) {
                if (isProject(filePath.first())) {
                    checkAndOpenProject(filePath.first(), state)
                } else {
                    uiActionChannel.send(UIAction.CreateProject(filePath.first()))
                }
                // Is directory
//                if (isProject(filePath.first())) {
//                    checkAndOpenProject(filePath.first())
//                } else {
//                    _uiActionHandler.send(UIAction.CreateNewProjectYesNo(path = filePath.first()))
//                }
            } else if (filePath.isNotEmpty()) {
                val directoryPath = filePath.first().parent
                if (isProject(directoryPath)) {
                    checkAndOpenProject(directoryPath, state)
                } else {
                    uiActionChannel.send(UIAction.CreateProject(directoryPath))
                }
//                if (isProject(filePath.first().parent)) {
//                    TODO("Ask for opening as project, otherwise open as list of files")
//                } else {
//                    TODO("Ask for creating new project, otherwise open as list of files")
//                }
            }
        }
    }

}