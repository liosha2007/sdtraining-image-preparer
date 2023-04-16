@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.common.BaseViewModel
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.*
import org.koin.core.component.KoinComponent
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.math.min
import kotlin.system.exitProcess

@ExperimentalPathApi
class HomeViewModel(
    private val checkProject: CheckProjectUseCase,
    private val loadImageModels: LoadImageModelsUseCase,
    private val readCaption: ReadCaptionUseCase,
    private val writeCaption: WriteCaptionUseCase,
    private val removeIncorrectThumbnails: RemoveIncorrectThumbnailsUseCase,
    private val extractCaptionKeywords: ExtractCaptionKeywordsUseCase,
    private val joinCaption: JoinCaptionUseCase,
    private val splitCaption: SplitCaptionUseCase,
    private val cropResizeImage: CropResizeImageUseCase,
    private val createNewAndMergeExistingCaptions: CreateNewAndMergeExistingCaptionsUseCase,
    private val configManager: ConfigManager,
    private val dropProject: DropProjectUseCase,
) : BaseViewModel<HomeState>(emptyState = HomeState()), KoinComponent {

    // region Application events

    private fun initHomeScreen() {
        _log.info("HomeDisplayed")
        if (configManager.openLastProjectOnStart && configManager.lastProjectPath.isNotBlank()) {
            val projectDirectory = Paths.get(configManager.lastProjectPath)
            _state.value = state.value.copy(projectDirectory = projectDirectory)
            sendEvent(HomeEvent.LoadProject(projectDirectory))
        }
    }

    private fun exitApplication(event: HomeEvent.Exit) {
//        if (event.isConfirmed) {
        exitProcess(0)
//        }
    }

    // endregion

    // region Project events

    private suspend fun loadProject(event: HomeEvent.LoadProject) {
        state.value.projectDirectory?.let {
            // state.value.projectDirectory == null means there is no opened project
            sendEvent(HomeEvent.CloseProject)
        }

        val projectDirectory = event.projectDirectory

        _state.value = HomeState(
            isShowChooseProjectDirectoryDialog = false
        )
        checkProject(projectDirectory)

        removeIncorrectThumbnails(projectDirectory)

        val data = loadImageModels(projectDirectory)
        val dataIndex = if (state.value.dataIndex == -1) 0 else state.value.dataIndex

        createNewAndMergeExistingCaptions(projectDirectory, data)

        val keywordMap = data.map { extractCaptionKeywords(it) }
            .flatten()
            .toSet()
            .toList()

        val currentCaptionKeywords = extractCaptionKeywords(data[dataIndex])

        _state.value = state.value.copy(
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

    private fun showOpenProjectDialog() {
        _state.value = state.value.copy(
            isShowChooseProjectDirectoryDialog = true
        )
    }

    private fun closeProject() {
        _state.value = HomeState()
    }

    private suspend fun closeAndDropProject() {
        state.value.projectDirectory?.let { projectDirectory ->
            sendEvent(HomeEvent.CloseProject)
            dropProject(projectDirectory)
        }
    }

    // endregion

    // region Image events

    private fun deleteImage() {
        TODO("Not yet implemented")
    }

    private fun changeImageSizeOnScreen(event: HomeEvent.ImageSizeChanged) {
        _state.value = state.value.copy(mainImageSize = event.imageSize)
    }

    private suspend fun changeSelectedImage(event: HomeEvent.ImageSelected) {
        val index = event.index
        _log.debug("Selected image: index = $index, name = ${state.value.selectedImageModel.imagePath.name}")
        // Save current keywords to caption file
        val currentKeywordList = splitCaption(state.value.captionContent)
        if (currentKeywordList.isNotEmpty()) {
            writeCaption(state.value[state.value.dataIndex], currentKeywordList)
        }
        // Add missing keywords to list
        _state.value = state.value.copy(
            keywordList = state.value.addMissingKeywords(currentKeywordList)
        )

        val currentCaptionKeywords = extractCaptionKeywords(state.value.selectedImageModel)

        // Change selected image
        _state.value = state.value.copy(
            dataIndex = index,
            captionContent = readCaption(state.value.selectedImageModel),
            keywordList = currentCaptionKeywords.map {
                it.copy(isAdded = currentCaptionKeywords.contains(it))
            }
        )
    }

    private fun showNextImage() {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == state.value.data.lastIndex) 0 else state.value.dataIndex + 1))
    }

    private fun showPrevImage() {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == 0) state.value.data.lastIndex else state.value.dataIndex - 1))
    }

    // endregion

    // region Captions events

    private fun createAllCaptions() {
        TODO("Not yet implemented")
    }

    private fun deleteAllCaptions() {
        TODO("Not yet implemented")
    }

    private fun changeCaptionContent(event: HomeEvent.CaptionContentChanged) {
        _state.value = state.value.copy(
            captionContent = event.value
        )
    }

    private suspend fun selectKeyword(event: HomeEvent.KeywordSelected) {
        _log.debug("Clicked keyword: ${event.keywordModel.keyword}")

        val activeModel = state.value[state.value.dataIndex]
        val captionList = extractCaptionKeywords(activeModel).toMutableList()

        if (event.keywordModel.isAdded) {
            captionList.removeIf { it.keyword == event.keywordModel.keyword }
        } else {
            captionList.add(event.keywordModel.copy(isAdded = true))
        }
        val keywordSet = captionList.map { it.keyword }
        writeCaption(activeModel, keywordSet)

        _state.value = state.value.copy(
            keywordList = state.value.keywordList.map {
                it.copy(isAdded = keywordSet.contains(it.keyword))
            },
            captionContent = joinCaption(keywordSet)
        )
    }

    // endregion

    // region Image tools events

    private fun toggleEditMode(event: HomeEvent.EditModeClicked) {
        _state.value = state.value.copy(
            isEditMode = event.enable,
            cropOffset = if (event.enable) state.value.cropOffset else Offset(0f, 0f),
            cropSize = if (event.enable) state.value.cropSize else Size(
                min(512f, state.value.mainImageSize.width),
                min(512f, state.value.mainImageSize.height)
            )
        )
    }

    private suspend fun cropResizeImage() {
        _log.debug("cropRectChanged: offset = ${state.value.cropOffset}, size = ${state.value.cropSize}")
        val imageModel = state.value.data[state.value.dataIndex]
        val newImageModel = cropResizeImage(imageModel, state.value.cropOffset, state.value.cropSize)
        _state.value = state.value.copy(
            data = state.value.data.map { if (it == imageModel) newImageModel else it },
            isEditMode = false
        )
    }

    private fun changeAreaSize(event: HomeEvent.ChangeAreaToSize) {
        var x = state.value.cropOffset.x
        var y = state.value.cropOffset.y
        var width = event.targetSize
        var height = event.targetSize
        val imageWidth = state.value.mainImageSize.width
        val imageHeight = state.value.mainImageSize.height

        if (width > imageWidth) {
            x = 0f
            width = imageWidth
        } else if (x + width > imageWidth) {
            x = imageWidth - width
        }

        if (height > imageHeight) {
            y = 0f
            height = imageHeight
        } else if (y + height > imageHeight) {
            y = imageHeight - height
        }

        _state.value = state.value.copy(
            cropOffset = Offset(x, y),
            cropSize = Size(width, height)
        )
    }

    private fun changeCropRectangle(event: HomeEvent.CropRectChanged) {
        _state.value = state.value.copy(
            cropOffset = event.offset,
            cropSize = event.size
        )
    }

    // endregion


    /**
     * This method was made this way just to make 'when' smaller so that IDE will not show warning about too deprecated method
     */
    override suspend fun onEvent(event: HomeEvent) {
        when (event) {
            // region Application events
            is HomeEvent.HomeDisplayed, is HomeEvent.Exit -> {
                onApplicationEvent(event)
            }
            // endregion

            // region Project events
            is HomeEvent.LoadProject, is HomeEvent.OpenProject, is HomeEvent.CloseProject, is HomeEvent.DropProject -> {
                onProjectEvent(event)
            }
            // endregion

            // region Image events
            is HomeEvent.DeleteImage, is HomeEvent.ImageSizeChanged, is HomeEvent.ImageSelected, is HomeEvent.ShowNextImage, is HomeEvent.ShowPrevImage -> {
                onImageEvent(event)
            }
            // endregion

            // region Captions events
            is HomeEvent.CreateAllCaptions, is HomeEvent.DeleteAllCaptions, is HomeEvent.CaptionContentChanged, is HomeEvent.KeywordSelected -> {
                onCaptionsEvent(event)
            }
            // endregion

            // region Toolbar events
            is HomeEvent.EditModeClicked, is HomeEvent.CropApplyClicked, is HomeEvent.ChangeAreaToSize, is HomeEvent.CropRectChanged -> {
                onToolbarEvent(event)
            }
            // endregion
            else -> {
                TODO("Not implemented: $event")
            }
        }
    }

    private fun onApplicationEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.HomeDisplayed -> initHomeScreen()
            is HomeEvent.Exit -> exitApplication(event)
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private suspend fun onProjectEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadProject -> loadProject(event)
            is HomeEvent.OpenProject -> showOpenProjectDialog()
            is HomeEvent.CloseProject -> closeProject()
            is HomeEvent.DropProject -> closeAndDropProject()
            else -> throw DisplayableException("Unexpected application state: 24cf91708f36($event)")
        }
    }

    private suspend fun onImageEvent(event: HomeEvent) {

        when (event) {
            is HomeEvent.DeleteImage -> deleteImage()
            is HomeEvent.ImageSizeChanged -> changeImageSizeOnScreen(event)
            is HomeEvent.ImageSelected -> changeSelectedImage(event)
            is HomeEvent.ShowNextImage -> showNextImage()
            is HomeEvent.ShowPrevImage -> showPrevImage()
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private suspend fun onCaptionsEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CreateAllCaptions -> createAllCaptions()
            is HomeEvent.DeleteAllCaptions -> deleteAllCaptions()
            is HomeEvent.CaptionContentChanged -> changeCaptionContent(event)
            is HomeEvent.KeywordSelected -> selectKeyword(event)
            else -> throw DisplayableException("Unexpected application state: 44f67cf5f537($event)")
        }
    }

    private suspend fun onToolbarEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.EditModeClicked -> toggleEditMode(event)
            is HomeEvent.CropApplyClicked -> cropResizeImage()
            is HomeEvent.ChangeAreaToSize -> changeAreaSize(event)
            is HomeEvent.CropRectChanged -> changeCropRectangle(event)
            else -> throw DisplayableException("Unexpected application state: 7c0e742c3764($event)")
        }
    }

    override fun showProgressBar() {
        _state.value = state.value.copy(isLoading = true)
    }

    override fun hideProgressBar() {
        _state.value = state.value.copy(isLoading = false)
    }

    override fun showError(message: String) {
        _state.value = state.value.copy(errorMessage = message)
    }

    override fun hideError() {
        _state.value = state.value.copy(errorMessage = null)
    }
}