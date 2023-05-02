package com.x256n.sdtrainingimagepreparer.desktop.usecase

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainingimagepreparer.desktop.common.*
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainingimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainingimagepreparer.desktop.repository.ProjectConfigRepository
import com.x256n.sdtrainingimagepreparer.desktop.repository.ThumbnailsRepository
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension


class ConvertImageUseCase(
    private val projectConfigRepository: ProjectConfigRepository,
    private val thumbnailsRepository: ThumbnailsRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    operator suspend fun invoke(imageModel: ImageModel): ImageModel {
        try {
            val projectConfig = projectConfigRepository.load(imageModel.projectDirectory)
            if (imageModel.imagePath.extension == projectConfig.targetImageFormat) {
                return imageModel
            } else {
                val oldAbsoluteImagePath = imageModel.absoluteImagePath
                val sourceImage = runInterruptible {
                    ImageIO.read(oldAbsoluteImagePath.toFile())
                }
                val newImageModel = imageModel.copy(
                    imagePath = Paths.get("${imageModel.imagePath.nameWithoutExtension}.${projectConfig.targetImageFormat}"
                    )
                )
                runInterruptible {
                    ImageIO.write(
                        sourceImage,
                        projectConfig.targetImageFormat,
                        newImageModel.absoluteImagePath.toFile()
                    )
                }
                runInterruptible { Files.delete(oldAbsoluteImagePath) }
                thumbnailsRepository.delete(imageModel)
                thumbnailsRepository.create(newImageModel, sourceImage)
                thumbnailsRepository.writeMetadata(newImageModel, Size(sourceImage.width.toFloat(), sourceImage.height.toFloat()))
                return newImageModel
            }
        } catch (e: Exception) {
            _log.error("Can't crop image", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}