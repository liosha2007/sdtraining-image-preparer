package com.x256n.sdtrainimagepreparer.desktop.model

import com.x256n.sdtrainimagepreparer.desktop.common.Constants
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

data class ImageModel(
    val projectDirectory: Path,
    val imagePath: Path,
    var captionContent: String = "",
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
                .resolve("${imagePath.nameWithoutExtension}.txt")
    val absoluteImagePath
        get() =
            projectDirectory
                .resolve(imagePath)
}
