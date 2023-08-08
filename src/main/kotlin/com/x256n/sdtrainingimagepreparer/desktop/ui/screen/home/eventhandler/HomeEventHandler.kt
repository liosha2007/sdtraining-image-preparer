package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.UIAction
import kotlinx.coroutines.channels.Channel

fun interface HomeEventHandler {
    suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    )
}