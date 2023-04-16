package com.x256n.sdtrainimagepreparer.desktop.navigation

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chrynan.navigation.NavigationContext
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeEvent
import java.nio.file.Path

sealed class Destinations : NavigationContext<Destinations> {
    data class Home(val action: Action = Action.Nothing) : Destinations() {
        sealed class Action {
            object Nothing : Action()
            data class LoadProject(val projectDirectory: Path) : Action()
            data class YesCancelDialogResult(val targetEvent: Any) : Action()
        }
    }

    object CreateProject : Destinations()
    object Settings : Destinations()
    data class YesCancel(
        val title: String = "Confirmation dialog",
        val message: String,
        val width: Dp = 420.dp,
        val height: Dp = 160.dp,
        val targetDest: Destinations) : Destinations()
    object About : Destinations()

    override val initialKey: Destinations
        get() = Home(action = Home.Action.Nothing)
}
