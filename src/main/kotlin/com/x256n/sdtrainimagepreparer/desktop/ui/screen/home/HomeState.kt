package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import java.nio.file.Path

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val projectDirectory: Path? = null,
    val isOpenProject: Boolean = false,
    val data: List<ImageModel> = emptyList(),
    val dataIndex: Int = 0
) {
    val currentModel get() = data[dataIndex]
}