package com.x256n.sdtrainimagepreparer.desktop.navigation

import com.chrynan.navigation.NavigationContext
import com.x256n.sdtrainimagepreparer.desktop.model.SampleModel

sealed class Destinations : NavigationContext<Destinations> {
    data class Home(val character: SampleModel? = null, val action: Action = Action.Undefined) : Destinations() {
        enum class Action { Undefined, Save, Delete }
    }
    object CreateProject : Destinations()
    object Settings : Destinations()
    object About : Destinations()

    override val initialKey: Destinations
        get() = Home()
}
