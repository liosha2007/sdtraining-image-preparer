package com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.deletecaptions

sealed class DeleteCaptionsConfirmationEvent {
    object DeleteCaptionsConfirmationDisplayed : DeleteCaptionsConfirmationEvent()
    data class DeleteOnlyEmptyChanged(val isDeleteOnlyEmpty: Boolean): DeleteCaptionsConfirmationEvent()
}
