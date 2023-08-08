@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.model.KeywordModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.*
import com.x256n.sdtrainingimagepreparer.desktop.usecase.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name

class ImageHomeEventHandler(
    private val checkProject: CheckProjectValidUseCase,
    private val loadImageModels: LoadImageModelsUseCase,
    private val removeIncorrectThumbnails: RemoveIncorrectThumbnailsUseCase,
    private val extractCaptionKeywords: ExtractCaptionKeywordsUseCase,
    private val joinCaption: JoinCaptionUseCase,
    private val deleteImage: DeleteImageUseCase,
    private val convertImage: ConvertImageUseCase,
    private val splitCaption: SplitCaptionUseCase,
    private val writeCaption: WriteCaptionUseCase,
) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        when (event) {
            is HomeEvent.DeleteImage -> deleteSelectedImage(state)
            is HomeEvent.ConvertImages -> convertAllImages(state)
            is HomeEvent.ImageSizeChanged -> changeImageSizeOnScreen(event, state)
            is HomeEvent.ImageSelected -> changeSelectedImage(event, state)
            is HomeEvent.ShowNextImage -> showNextImage(state, sendEvent)
            is HomeEvent.ShowPrevImage -> showPrevImage(state, sendEvent)
            is HomeEvent.SyncImages -> syncImages(state)
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private suspend fun deleteSelectedImage(state: MutableState<HomeState>) {
        val selectedImageModel = state.value.selectedImageModel
        val dataIndex = state.value.dataIndex

        deleteImage(selectedImageModel)

        state.value = state.value.copy(
            data = state.value.data.toMutableList().apply {
                this.removeAt(dataIndex)
            },
            dataIndex = if (dataIndex > state.value.data.lastIndex) state.value.data.lastIndex else dataIndex
        )
    }

    private suspend fun convertAllImages(state: MutableState<HomeState>) {
        state.value = state.value.copy(
            data = state.value.data.map {
                convertImage(it)
            },
            status = Status.Info("Images converted successfully.")
        )
    }

    private fun changeImageSizeOnScreen(event: HomeEvent.ImageSizeChanged, state: MutableState<HomeState>) {
        state.value = state.value.copy(
            realImageSize = event.imageSize,
            imageScale = event.imageScale
        )
    }

    private suspend fun changeSelectedImage(event: HomeEvent.ImageSelected, state: MutableState<HomeState>) {
        val index = event.index
        _log.debug("Selected image: index = $index, name = ${state.value.selectedImageModel.imagePath.name}")
        // Save current keywords to caption file
        val currentModelKeywordList = splitCaption(state.value.captionContent)
        if (currentModelKeywordList.isNotEmpty()) {
            writeCaption(state.value.selectedImageModel, currentModelKeywordList)
        }
        // Add missing keywords to right panel
        val actualRightPanelKeywordList = state.value.keywordList.map { it.copy(isAdded = false) }.toMutableList()
        actualRightPanelKeywordList.addAll(currentModelKeywordList.map { KeywordModel(it, 1) })

        val currentCaptionKeywords = extractCaptionKeywords(state.value.data[index])

        // Change selected image
        state.value = state.value.copy(
            screenMode = ScreenMode.Default,
            dataIndex = index,
            captionContent = joinCaption(currentCaptionKeywords.map { it.keyword }),
            keywordList = actualRightPanelKeywordList.toSet().map {
                it.copy(isAdded = currentCaptionKeywords.contains(it))
            }
        )
    }

    private fun showNextImage(state: MutableState<HomeState>, sendEvent: (HomeEvent) -> Unit) {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == state.value.data.lastIndex) 0 else state.value.dataIndex + 1))
    }

    private fun showPrevImage(state: MutableState<HomeState>, sendEvent: (HomeEvent) -> Unit) {
        sendEvent(HomeEvent.ImageSelected(if (state.value.dataIndex == 0) state.value.data.lastIndex else state.value.dataIndex - 1))
    }

    private suspend fun syncImages(state: MutableState<HomeState>) {
        state.value.projectDirectory?.let { projectDirectory ->
            checkProject(projectDirectory)

            removeIncorrectThumbnails(projectDirectory)

            val data = loadImageModels(projectDirectory)
            val dataIndex = if (state.value.dataIndex == -1) 0 else state.value.dataIndex

            state.value = state.value.copy(
                projectDirectory = projectDirectory,
                data = data,
                dataIndex = dataIndex,
            )
        }
    }

}