package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yesnocancel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.usecase.InitializeProjectUseCase
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class YesNoCancelViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger(this::class.java)

    private val _state = mutableStateOf(YesNoCancelState())
    val state: State<YesNoCancelState> = _state


    fun onEvent(event: YesNoCancelEvent) {
        when (event) {
            is YesNoCancelEvent.YesNoCancelDisplayed -> {
                _log.debug("YesNoCancelDisplayed")
            }

            else -> {
                TODO("Not implemented: $event")
            }
        }
    }
}