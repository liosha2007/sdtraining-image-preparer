package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import java.nio.file.Path

sealed class HomeEvent {
    object HomeDisplayed : HomeEvent()
    data class LoadProject(val projectDirectory: Path) : HomeEvent()
    object OpenProject : HomeEvent()
    data class ImageSelected(val index: Int) : HomeEvent()
}
