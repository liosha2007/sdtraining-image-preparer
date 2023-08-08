package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.ScreenMode
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.UIAction
import com.x256n.sdtrainingimagepreparer.desktop.usecase.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory

class CaptionHomeEventHandler(
    private val extractCaptionKeywords: ExtractCaptionKeywordsUseCase,
    private val joinCaption: JoinCaptionUseCase,
    private val deleteCaption: DeleteCaptionUseCase,
    private val writeCaption: WriteCaptionUseCase,
    private val createCaptionIfNotExist: CreateCaptionIfNotExistUseCase,
) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        when (event) {
            is HomeEvent.CreateAllCaptions -> createAllCaptions(state)
            is HomeEvent.DeleteAllCaptions -> deleteAllCaptions(event, state)
            is HomeEvent.CaptionReplaceModeClicked -> toggleCaptionReplaceMode(event, state)
            is HomeEvent.CaptionReplaceSourceValueChange -> changeReplaceSourceValue(event)
            is HomeEvent.CaptionReplaceApplyClicked -> captionReplace(event)
            is HomeEvent.CaptionContentChanged -> changeCaptionContent(event, state)
            is HomeEvent.KeywordSelected -> selectKeyword(event, state)
            else -> throw DisplayableException("Unexpected application state: 44f67cf5f537($event)")
        }
    }

    private suspend fun createAllCaptions(state: MutableState<HomeState>) {
        state.value.data.forEach { model ->
            createCaptionIfNotExist(model, emptyList())
        }
    }

    private suspend fun deleteAllCaptions(event: HomeEvent.DeleteAllCaptions, state: MutableState<HomeState>) {
        state.value.data.forEach {
            deleteCaption(it, isDeleteOnlyEmpty = event.isDeleteOnlyEmpty)
        }
    }

    private fun toggleCaptionReplaceMode(event: HomeEvent.CaptionReplaceModeClicked, state: MutableState<HomeState>) {
        state.value = state.value.copy(
            screenMode = if (event.enable) ScreenMode.CaptionReplace else ScreenMode.Default
        )
    }

    private fun changeReplaceSourceValue(event: HomeEvent.CaptionReplaceSourceValueChange) {
        _log.debug("It would be useful to highlight text if it is present in keywords list on the bottom of the screen")
    }

    private suspend fun captionReplace(event: HomeEvent.CaptionReplaceApplyClicked) {
//        TODO("Caption Replace")
        _log.debug("captionReplace: $event")
    }

    private fun changeCaptionContent(event: HomeEvent.CaptionContentChanged, state: MutableState<HomeState>) {
        state.value = state.value.copy(
            captionContent = event.value
        )
    }

    private suspend fun selectKeyword(event: HomeEvent.KeywordSelected, state: MutableState<HomeState>) {
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

        state.value = state.value.copy(
            keywordList = state.value.keywordList.map {
                it.copy(isAdded = keywordSet.contains(it.keyword))
            },
            captionContent = joinCaption(keywordSet)
        )
    }
}