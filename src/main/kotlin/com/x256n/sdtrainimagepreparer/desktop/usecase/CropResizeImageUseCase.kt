package com.x256n.sdtrainimagepreparer.desktop.usecase

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ThumbnailsRepository
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import javax.imageio.ImageIO


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
            val sourceImage = runInterruptible {
                ImageIO.read(imageModel.absoluteImagePath.toFile())
            }
            val croppedImage = sourceImage.cropImage(offset, size)
            val resultImage =
                if (croppedImage.width != projectConfig.targetImageResolution || croppedImage.height != projectConfig.targetImageResolution) {
                    croppedImage.resizeImage(projectConfig.targetImageResolution, projectConfig.targetImageResolution)
                } else croppedImage

            runInterruptible { ImageIO.write(resultImage, "png", imageModel.absoluteImagePath.toFile()) }

            val resultImageSize = Size(resultImage.width.toFloat(), resultImage.height.toFloat())

            thumbnailsRepository.delete(imageModel)
            thumbnailsRepository.create(imageModel, resultImage)
            thumbnailsRepository.writeMetadata(imageModel, resultImageSize)

            return imageModel.copy(
                imageSize = resultImageSize
            )
        } catch (e: Exception) {
            _log.error("Can't crop image", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}