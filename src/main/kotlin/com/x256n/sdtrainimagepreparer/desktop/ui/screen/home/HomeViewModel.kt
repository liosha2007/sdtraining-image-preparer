@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.name

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
    private val splitCaption: SplitCaptionUseCase
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

    private suspend fun captionContentChanged(value: String) {
        _state.value = state.value.copy(
            captionContent = value
        )
        withContext(dispatcherProvider.default) {
            if (value.contains(',')) {
                val captionContent = value.substring(0, value.lastIndexOf(','))
                if (captionContent.isNotBlank()) {
                    val captionKeywordList = splitCaption(captionContent)
                    _state.value = state.value.copy(
                        keywordList = state.value.addMissingKeywords(captionKeywordList)
                    )
                    writeCaption(state.value[state.value.dataIndex], captionKeywordList)
                }
            }
        }
    }

    private suspend fun imageSelected(index: Int) {
        _log.debug("Selected image: index = $index, name = ${state.value[index].imagePath.name}")
        // Save current keywords to caption file
        val currentKeywordList = splitCaption(state.value.captionContent)
        writeCaption(state.value[state.value.dataIndex], currentKeywordList)
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
        withContext(dispatcherProvider.default) {
            _state.value = state.value.copy(
                projectDirectory = null,
                isOpenProject = false
            )
            checkProject(projectDirectory)

            removeIncorrectThumbnails(projectDirectory)

            val data = loadImageModels(projectDirectory)
            val dataIndex = if (state.value.dataIndex == -1) 0 else state.value.dataIndex
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