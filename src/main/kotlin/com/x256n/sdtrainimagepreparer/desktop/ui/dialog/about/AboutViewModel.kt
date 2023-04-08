package com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.usecase.InitializeProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class AboutViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("AboutViewModel")

    private val _state = mutableStateOf(AboutState())
    val state: State<AboutState> = _state


    fun onEvent(event: AboutEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            when (event) {
                is AboutEvent.AboutDisplayed -> {
//                    _log.info(doSampleModel())
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
        }
    }
}