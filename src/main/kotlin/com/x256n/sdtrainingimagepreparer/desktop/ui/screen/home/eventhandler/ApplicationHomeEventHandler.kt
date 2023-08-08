package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler

import androidx.compose.runtime.MutableState
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeState
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.UIAction
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.system.exitProcess

class ApplicationHomeEventHandler(
    private val configManager: ConfigManager,
) : HomeEventHandler {
    private val _log = LoggerFactory.getLogger(this::class.java)
    override suspend fun handleEvent(
        event: HomeEvent,
        state: MutableState<HomeState>,
        uiActionChannel: Channel<UIAction>,
        sendEvent: (HomeEvent) -> Unit
    ) {

        when (event) {
            is HomeEvent.HomeDisplayed -> initHomeScreen(state, sendEvent)
            is HomeEvent.Exit -> exitApplication(event)
            else -> throw DisplayableException("Unexpected application state: c92a533424d4($event)")
        }
    }

    private fun initHomeScreen(state: MutableState<HomeState>, sendEvent: (HomeEvent) -> Unit) {
        _log.info("HomeDisplayed")
        if (configManager.openLastProjectOnStart && configManager.lastProjectPath.isNotBlank()) {
            val projectDirectory = Paths.get(configManager.lastProjectPath)
            state.value = state.value.copy(projectDirectory = projectDirectory)
            sendEvent(HomeEvent.LoadProject(projectDirectory))
        }
    }

    private fun exitApplication(event: HomeEvent.Exit) {
//        if (event.isConfirmed) {
        exitProcess(0)
//        }
    }
}