@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.common.BaseViewModel
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.component.KoinComponent
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class HomeViewModel(
    private val applicationEventHandler: ApplicationHomeEventHandler,
    private val projectEventHandler: ProjectHomeEventHandler,
    private val imageEventHandler: ImageHomeEventHandler,
    private val captionEventHandler: CaptionHomeEventHandler,
    private val cropEventHandler: CropHomeEventHandler,
) : BaseViewModel<HomeState>(emptyState = HomeState()), KoinComponent {
    private val _uiActionChannel = Channel<UIAction>()
    val uiActionHandler = _uiActionChannel.receiveAsFlow()

    // region Keyboard events

    private fun enterButtonPressed() {
        if (state.value.screenMode == ScreenMode.ImageCrop) {
            sendEvent(HomeEvent.ImageCropApplyClicked)
        }
    }

    private fun escButtonPressed() {
        if (state.value.screenMode == ScreenMode.ImageCrop) {
            _state.value = state.value.copy(
                screenMode = ScreenMode.Default,
                cropOffset = Offset(0f, 0f),
                cropSize = Size(512f, 512f),
                cropActiveType = ActiveType.None,
            )
        }
    }

    private fun deleteButtonPressed() {
        // Deleting image will be processed in screen file. When screenMode is Default event will not be sent here.
    }

    // endregion

    /**
     * This method was made this way just to make 'when' smaller so that IDE will not show warning about too deprecated method
     */
    override suspend fun onEvent(event: HomeEvent) {
        when (event) {
            // region Application events
            is HomeEvent.HomeDisplayed, is HomeEvent.Exit -> {
                applicationEventHandler.handleEvent(
                    event,
                    _state,
                    uiActionChannel = _uiActionChannel,
                    sendEvent = ::sendEvent
                )
            }
            // endregion

            // region Project events
            is HomeEvent.LoadProject, is HomeEvent.CreateProject, is HomeEvent.Open, is HomeEvent.CloseProject, is HomeEvent.DropProject, is HomeEvent.FilesDropped -> {
                projectEventHandler.handleEvent(
                    event,
                    _state,
                    uiActionChannel = _uiActionChannel,
                    sendEvent = ::sendEvent
                )
            }
            // endregion

            // region Image events
            is HomeEvent.DeleteImage, is HomeEvent.ConvertImages, is HomeEvent.ImageSizeChanged, is HomeEvent.ImageSelected, is HomeEvent.ShowNextImage, is HomeEvent.ShowPrevImage, is HomeEvent.SyncImages -> {
                imageEventHandler.handleEvent(
                    event,
                    _state,
                    uiActionChannel = _uiActionChannel,
                    sendEvent = ::sendEvent
                )
            }
            // endregion

            // region Captions events
            is HomeEvent.CreateAllCaptions, is HomeEvent.CaptionReplaceSourceValueChange, is HomeEvent.DeleteAllCaptions, is HomeEvent.CaptionReplaceApplyClicked, is HomeEvent.CaptionReplaceModeClicked, is HomeEvent.CaptionContentChanged, is HomeEvent.KeywordSelected -> {
                captionEventHandler.handleEvent(
                    event,
                    _state,
                    uiActionChannel = _uiActionChannel,
                    sendEvent = ::sendEvent
                )
            }
            // endregion

            // region Captions events
            is HomeEvent.ImageCropModeClicked, is HomeEvent.ImageCropApplyClicked, is HomeEvent.ChangeAreaToSize, is HomeEvent.ChangeAreaToMax, is HomeEvent.CropRectChanged, is HomeEvent.CropActiveTypeChanged -> {
                cropEventHandler.handleEvent(
                    event,
                    _state,
                    uiActionChannel = _uiActionChannel,
                    sendEvent = ::sendEvent
                )
            }
            // endregion

            // region Keyboard
            is HomeEvent.EnterPressed, is HomeEvent.EscPressed, is HomeEvent.DeletePressed -> {
                onKeyboardEvent(event)
            }
            // endregion
            else -> {
                TODO("Not implemented: $event")
            }
        }
    }

    private fun onKeyboardEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.EnterPressed -> enterButtonPressed()
            is HomeEvent.EscPressed -> escButtonPressed()
            is HomeEvent.DeletePressed -> deleteButtonPressed()
            else -> throw DisplayableException("Unexpected application state: bbceb2ae77ac($event)")
        }
    }

    override fun showProgressBar() {
        _state.value = state.value.copy(isLoading = true)
    }

    override fun hideProgressBar() {
        _state.value = state.value.copy(isLoading = false)
    }

    override fun showError(message: String) {
        _state.value = state.value.copy(status = Status.Error(message))
    }

    override fun hideError() {
        _state.value = state.value.copy(status = Status.None)
    }
}