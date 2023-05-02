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
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension


class CropResizeImageUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository,
    private val thumbnailsRepository: ThumbnailsRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    operator suspend fun invoke(imageModel: ImageModel, offset: Offset, size: Size): ImageModel {
        try {
            val projectConfig = projectConfigRepository.load(imageModel.projectDirectory)
            val oldAbsoluteImagePath = imageModel.absoluteImagePath
            val sourceImage = runInterruptible {
                ImageIO.read(oldAbsoluteImagePath.toFile())
            }
            val croppedImage = sourceImage.cropImage(offset, size)
            val resultImage =
                if (croppedImage.width != projectConfig.targetImageResolution || croppedImage.height != projectConfig.targetImageResolution) {
                    croppedImage.resizeImage(projectConfig.targetImageResolution, projectConfig.targetImageResolution)
                } else croppedImage

            val areFormatDifferent = oldAbsoluteImagePath.extension != projectConfig.targetImageFormat
            val newImageModel = if (areFormatDifferent) {
                imageModel.copy(
                    imagePath = Paths.get("${imageModel.imagePath.nameWithoutExtension}.${projectConfig.targetImageFormat}")
                )
            } else {
                imageModel
            }
            runInterruptible {
                ImageIO.write(
                    resultImage,
                    projectConfig.targetImageFormat,
                    newImageModel.absoluteImagePath.toFile()
                )
                if (areFormatDifferent) {
                    Files.delete(oldAbsoluteImagePath)
                }
            }

            val resultImageSize = Size(resultImage.width.toFloat(), resultImage.height.toFloat())

            thumbnailsRepository.delete(imageModel)
            thumbnailsRepository.create(newImageModel, resultImage)
            thumbnailsRepository.writeMetadata(newImageModel, resultImageSize)

            return newImageModel.copy(
                imageSize = resultImageSize
            )
        } catch (e: Exception) {
            _log.error("Can't crop image", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}