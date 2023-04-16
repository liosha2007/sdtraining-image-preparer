package com.x256n.sdtrainingimagepreparer.desktop.model

import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.common.Constants
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

data class ImageModel(
    val projectDirectory: Path,
    val imagePath: Path,
    var imageSize: Size = Size(0f, 0f),
    val captionExtension: String,
) {
    val thumbnailPath
        get() =
            projectDirectory
                .resolve(Constants.PROJECT_DIRECTORY_NAME)
                .resolve(Constants.THUMBNAILS_DIRECTORY_NAME)
                .resolve(imagePath)

    val captionPath
        get() =
            projectDirectory
                .resolve(imagePath.parent ?: Path.of(""))
                .resolve("${imagePath.nameWithoutExtension}.$captionExtension")

    val absoluteImagePath
        get() =
            projectDirectory
                .resolve(imagePath)

    val imageName
        get() = imagePath.name
}
