package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.deletecaptions

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.usecase.InitializeProjectUseCase
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class DeleteCaptionsConfirmationViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger(this::class.java)

    private val _state = mutableStateOf(DeleteCaptionsConfirmationState())
    val state: State<DeleteCaptionsConfirmationState> = _state


    fun onEvent(event: DeleteCaptionsConfirmationEvent) {
        when (event) {
            is DeleteCaptionsConfirmationEvent.DeleteCaptionsConfirmationDisplayed -> {
                _log.debug("DeleteCaptionsConfirmationDisplayed")
            }
            is DeleteCaptionsConfirmationEvent.DeleteOnlyEmptyChanged -> {
                _state.value = state.value.copy(
                    isDeleteOnlyEmpty = event.isDeleteOnlyEmpty
                )
            }

            else -> {
                TODO("Not implemented: $event")
            }
        }
    }
}