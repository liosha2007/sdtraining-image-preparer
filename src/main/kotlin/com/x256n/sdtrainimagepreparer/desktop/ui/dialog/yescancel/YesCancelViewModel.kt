package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.yescancel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.InitializeProjectUseCase
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class YesCancelViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger(this::class.java)

    private val _state = mutableStateOf(YesCancelState())
    val state: State<YesCancelState> = _state


    fun onEvent(event: YesCancelEvent) {
        when (event) {
            is YesCancelEvent.YesCancelDisplayed -> {
                _log.debug("YesCancelDisplayed")
            }

            else -> {
                TODO("Not implemented: $event")
            }
        }
    }
}