package com.x256n.sdtrainimagepreparer.desktop.model

import java.nio.file.Path

data class ImageModel(
    val imagePath: Path,
    val thumbnailPath: Path,
    val captionPath: Path
)
