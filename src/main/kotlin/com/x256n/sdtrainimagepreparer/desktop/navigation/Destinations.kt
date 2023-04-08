package com.x256n.sdtrainimagepreparer.desktop.navigation

import com.chrynan.navigation.NavigationContext
import com.x256n.sdtrainimagepreparer.desktop.model.ProjectConfig
import java.nio.file.Path

sealed class Destinations : NavigationContext<Destinations> {
    data class Home(val projectDirectory: Path? = null) : Destinations()
    object CreateProject : Destinations()
    object Settings : Destinations()
    object About : Destinations()

    override val initialKey: Destinations
        get() = Home()
}
