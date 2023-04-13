@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name
import kotlin.math.min

@ExperimentalPathApi
class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
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
    private val configManager: ConfigManager
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
                        if (configManager.openLastProjectOnStart && configManager.lastProjectPath.isNotBlank()) {
                            val projectDirectory = Paths.get(configManager.lastProjectPath)
                            _state.value = state.value.copy(projectDirectory = projectDirectory)
                            loadProject(projectDirectory)
                        }
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
                    is HomeEvent.KeywordSelected -> {
                        keywordSelected(event.keywordModel)
                    }
                    is HomeEvent.CaptionContentChanged -> {
                        captionContentChanged(event.value)
                    }
                    is HomeEvent.ShowNextImage -> {
                        imageSelected(if (state.value.dataIndex == state.value.data.lastIndex) 0 else state.value.dataIndex + 1)
                    }
                    is HomeEvent.ShowPrevImage -> {
                        imageSelected(if (state.value.dataIndex == 0) state.value.data.lastIndex else state.value.dataIndex - 1)
                    }
                    is HomeEvent.EditModeClicked -> {
                        editMode(event.enable)
                    }
                    is HomeEvent.CropRectChanged -> {
                        cropRectChanged(event.offset, event.size)
                    }
                    is HomeEvent.MainImageScaleChanged -> {
                        mainImageScale(event.imageSize)
                    }
                    is HomeEvent.ChangeAreaToSize -> {
                        changeAreaToSize(event.targetSize)
                    }
                    is HomeEvent.CropApplyClicked -> {
                        cropApply()
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

    private fun mainImageScale(imageSize: Size) {
        _state.value = state.value.copy(mainImageSize = imageSize)
    }

    private fun editMode(enable: Boolean) {
        _state.value = state.value.copy(
            isEditMode = enable,
            cropOffset = if (enable) state.value.cropOffset else Offset(0f, 0f),
            cropSize = if (enable) state.value.cropSize else Size(
                min(512f, state.value.mainImageSize.width),
                min(512f, state.value.mainImageSize.height)
            )
        )
    }

    private suspend fun cropApply() {
        _log.debug("cropRectChanged: offset = ${state.value.cropOffset}, size = ${state.value.cropSize}")
        val imageModel = state.value.data[state.value.dataIndex]
        val newImageModel = cropResizeImage(imageModel, state.value.cropOffset, state.value.cropSize)
        _state.value = state.value.copy(
            data = state.value.data.map { if (it == imageModel) newImageModel else it },
            isEditMode = false
        )
    }

    private fun changeAreaToSize(targetSize: Float) {
        var x = state.value.cropOffset.x
        var y = state.value.cropOffset.y
        var width = targetSize
        var height = targetSize
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

    private fun cropRectChanged(offset: Offset, size: Size) {
        _state.value = state.value.copy(
            cropOffset = offset,
            cropSize = size
        )
    }

    private fun captionContentChanged(value: String) {
        _state.value = state.value.copy(
            captionContent = value
        )
    }

    private suspend fun imageSelected(index: Int) {
        _log.debug("Selected image: index = $index, name = ${state.value[index].imagePath.name}")
        // Save current keywords to caption file
        val currentKeywordList = splitCaption(state.value.captionContent)
        if (currentKeywordList.isNotEmpty()) {
            writeCaption(state.value[state.value.dataIndex], currentKeywordList)
        }
        // Add missing keywords to list
        _state.value = state.value.copy(
            keywordList = state.value.addMissingKeywords(currentKeywordList)
        )

        // Change selected image
        _state.value = state.value.copy(
            dataIndex = index,
            captionContent = readCaption(state.value[index])
        )
        actualizeCaptions()
    }

    private suspend fun keywordSelected(keywordModel: KeywordModel) {
        _log.debug("Clicked keyword: ${keywordModel.keyword}")

        val activeModel = state.value[state.value.dataIndex]
        val captionList = extractCaptionKeywords(activeModel).toMutableList()

        if (keywordModel.isAdded) {
            captionList.removeIf { it.keyword == keywordModel.keyword }
        } else {
            captionList.add(keywordModel.copy(isAdded = true))
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

    private suspend fun loadProject(projectDirectory: Path) {
        try {
            withContext(dispatcherProvider.default) {
                _state.value = HomeState(
                    projectDirectory = null,
                    isOpenProject = false
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


                _state.value = state.value.copy(
                    projectDirectory = projectDirectory,
                    data = data,
                    dataIndex = dataIndex,
                    keywordList = keywordMap,
                    captionContent = joinCaption(extractCaptionKeywords(data[dataIndex]).map { it.keyword })
                )

                actualizeCaptions()

                configManager.lastProjectPath = projectDirectory.toAbsolutePath().normalize().toString()
            }
            _state.value = state.value.copy(isProjectLoaded = true)
        } catch (e: Exception) {
            _state.value = state.value.copy(isProjectLoaded = false)
            throw e
        }
    }

    private suspend fun actualizeCaptions() {
        if (state.value.hasData) {
            val currentModel = state.value[state.value.dataIndex]
            val captionKeywordList = extractCaptionKeywords(currentModel).map { it.keyword }
            _state.value = state.value.copy(
                keywordList = state.value.keywordList.map {
                    it.copy(isAdded = captionKeywordList.contains(it.keyword))
                }
            )
        }
    }
}