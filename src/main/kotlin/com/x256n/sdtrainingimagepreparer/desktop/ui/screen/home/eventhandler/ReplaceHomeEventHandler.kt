package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.ScreenMode
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.UIAction
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory

class ReplaceHomeEventHandler(

) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {
        when (event) {
            is HomeEvent.CaptionReplaceSourceValueChange -> sourceValueChange(event, state)
            is HomeEvent.CaptionReplaceModeClicked -> toggleReplaceMode(event, state)
            is HomeEvent.CaptionReplaceApplyClicked -> applyReplace(event, state)
            else -> throw DisplayableException("Unexpected application state: 5fe5b5f53764($event)")
        }
    }

    private fun sourceValueChange(event: HomeEvent.CaptionReplaceSourceValueChange, state: MutableState<HomeState>) {
        _log.debug("It would be useful to highlight text if it is present in keywords list on the bottom of the screen")
    }

    private fun toggleReplaceMode(event: HomeEvent.CaptionReplaceModeClicked, state: MutableState<HomeState>) {
        state.value = state.value.copy(
            screenMode = if (event.enable) ScreenMode.CaptionReplace else ScreenMode.Default
        )
    }

    private fun applyReplace(event: HomeEvent.CaptionReplaceApplyClicked, state: MutableState<HomeState>) {
        _log.debug("captionReplace: $event")
    }
}