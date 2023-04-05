package com.x256n.sdtrainimagepreparer.desktop.screen.config

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.DoSampleModelUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class ConfigViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: DoSampleModelUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("ConfigViewModel")

    private val _state = mutableStateOf(ConfigState())
    val state: State<ConfigState> = _state


    fun onEvent(event: ConfigEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is ConfigEvent.ConfigDisplayed -> {
                    _log.info(doSampleModel())
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
        }
    }
}