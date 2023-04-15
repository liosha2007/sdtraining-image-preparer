package com.x256n.sdtrainimagepreparer.desktop.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class BaseViewModel<StateType>(
    emptyState: StateType,
    protected val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
) {
    protected val _log = LoggerFactory.getLogger(this::class.java)

    protected val _state = mutableStateOf(emptyState)
    val state: State<StateType> = _state

    fun sendEvent(event: HomeEvent) {
        CoroutineScope(dispatcherProvider.main).launch {
            hideError()
            showProgressBar()

            try {
                _log.trace("start processing event: $event")

                onEvent(event)

                _log.trace("stop processing event: $event")
            } catch (e: DisplayableException) {
                _log.trace("Displayable exception while processing event: $event")

                showError(message = e.message ?: "Unknown error!")
            } catch (e: Exception) {
                _log.trace("Unexpected exception while processing event: $event")

                _log.error("Unexpected exception!", e)
                showError("Unexpected exception: ${e.message}")
            } finally {
                hideProgressBar()
            }
        }
    }

    protected abstract suspend fun onEvent(event: HomeEvent)

    protected abstract fun showProgressBar()
    protected abstract fun hideProgressBar()

    protected abstract fun showError(message: String)
    protected abstract fun hideError()
}