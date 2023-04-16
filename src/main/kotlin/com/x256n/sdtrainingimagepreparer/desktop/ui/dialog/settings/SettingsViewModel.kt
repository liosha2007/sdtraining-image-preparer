package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.usecase.InitializeProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class SettingsViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("SettingsViewModel")

    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state


    fun onEvent(event: SettingsEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is SettingsEvent.SettingsDisplayed -> {
//                    _log.info(doSampleModel())
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
        }
    }
}