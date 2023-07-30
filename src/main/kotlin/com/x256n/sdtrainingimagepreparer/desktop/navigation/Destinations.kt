package com.x256n.sdtrainingimagepreparer.desktop.navigation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chrynan.navigation.NavigationContext
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import java.nio.file.Path

sealed class Destinations : NavigationContext<Destinations> {
    data class Home(val action: Action = Action.Nothing) : Destinations() {
        sealed class Action {
            object Nothing : Action()
            data class LoadProject(val projectDirectory: Path) : Action()
            data class YesCancelDialogResult(val targetEvent: Any) : Action()
            data class YesNoCancelDialogResult(val isYes: Boolean, val targetYesEvent: Any, val targetNoEvent: Any) : Action()
            data class DeleteCaptionsConfirmationDialogResult(val isDeleteOnlyEmpty: Boolean) : Action()
        }
    }

    data class CreateProject(val path: Path? = null) : Destinations()
    object Settings : Destinations()
    data class YesCancel(
        val title: String = "Confirmation dialog",
        val message: String,
        val width: Dp = 420.dp,
        val height: Dp = 160.dp,
        val targetDest: Destinations) : Destinations()
    data class YesNoCancel(
        val title: String = "Confirmation dialog",
        val message: String,
        val width: Dp = 420.dp,
        val height: Dp = 160.dp,
        val targetDestCreator: (isYes: Boolean) -> Destinations) : Destinations()
    object DeleteCaptionsConfirmation : Destinations()
    object About : Destinations()

    override val initialKey: Destinations
        get() = Home(action = Home.Action.Nothing)
}
