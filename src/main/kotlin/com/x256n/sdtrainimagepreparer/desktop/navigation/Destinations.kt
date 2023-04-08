package com.x256n.sdtrainimagepreparer.desktop.navigation

import com.chrynan.navigation.NavigationContext
import java.nio.file.Path

sealed class Destinations : NavigationContext<Destinations> {
    data class Home(val action: Action = Action.Nothing) : Destinations() {
        sealed class Action {
            object Nothing : Action()
            data class LoadProject(val projectDirectory: Path) : Action()
        }
    }

    object CreateProject : Destinations()
    object Settings : Destinations()
    object About : Destinations()

    override val initialKey: Destinations
        get() = Home(action = Home.Action.LoadProject(Path.of("D:\\kotlin\\sdtrain-image-preparer\\test-project")))
}
