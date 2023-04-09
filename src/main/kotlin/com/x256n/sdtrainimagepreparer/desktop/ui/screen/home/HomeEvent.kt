package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import java.nio.file.Path

sealed class HomeEvent {
    object HomeDisplayed : HomeEvent()
    data class LoadProject(val projectDirectory: Path) : HomeEvent()
    object OpenProject : HomeEvent()
    data class ImageSelected(val index: Int) : HomeEvent()

    object ShowNextImage : HomeEvent()
    object ShowPrevImage : HomeEvent()

    data class KeywordSelected(val keywordModel: KeywordModel) : HomeEvent()

    data class CaptionContentChanged(val value: String) : HomeEvent()
}
