package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home

import java.nio.file.Path

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val projectDirectory: Path? = Path.of("D:\\kotlin\\sdtrain-image-preparer\\test-project"),
)