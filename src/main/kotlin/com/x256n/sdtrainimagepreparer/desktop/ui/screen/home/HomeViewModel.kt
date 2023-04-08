package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.usecase.InitializeProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class HomeViewModel(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val doSampleModel: InitializeProjectUseCase,
) : KoinComponent {
    private val _log = LoggerFactory.getLogger("HomeViewModel")

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    fun onEvent(event: HomeEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            _state.value = state.value.copy(isLoading = true, errorMessage = null)
            when (event) {
                is HomeEvent.HomeDisplayed -> {
                    _log.info("HomeDisplayed")
                }
                is HomeEvent.LoadProject -> {
                    _state.value = state.value.copy(
                        projectDirectory = event.projectDirectory
                    )
                }

                else -> {
                    TODO("Not implemented: $event")
                }
            }
            _state.value = state.value.copy(isLoading = false)
        }
    }
}