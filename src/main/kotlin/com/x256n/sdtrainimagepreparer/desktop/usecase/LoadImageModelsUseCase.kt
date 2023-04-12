package com.x256n.sdtrainimagepreparer.desktop.usecase

import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ThumbnailsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.walk


@ExperimentalPathApi
class LoadImageModelsUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val thumbnailsRepository: ThumbnailsRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(projectDirectory: Path): List<ImageModel> {
        try {
            val projectConfig = projectConfigRepository.load(projectDirectory)
            return coroutineScope {
                val coroutineScope = this
                return@coroutineScope projectDirectory.walk()
                    .filter {
                        configManager.supportedImageFormats.contains(it.extension)
                                && !it.startsWith(projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME))
                    }
                    .sortedBy { it.name }
                    .map { absoluteImagePath ->
                        val relativeImagePath = projectDirectory.relativize(absoluteImagePath)

                        val model = ImageModel(
                            projectDirectory = projectDirectory,
                            imagePath = relativeImagePath,
                            captionExtension = projectConfig.captionExtension,
                        )

                        coroutineScope.launch(dispatcherProvider.default) {

                            val thumbnailExist = thumbnailsRepository.exist(model)
                            if (thumbnailExist) {
                                val metadata = thumbnailsRepository.readMetadata(model)
                                model.imageSize = metadata
                            } else {
                                val thumbnailsDirectory = model.thumbnailPath.parent
                                runInterruptible(dispatcherProvider.io) {
                                    synchronized(projectConfig) {
                                        if (Files.notExists(thumbnailsDirectory) || !Files.isDirectory(thumbnailsDirectory)) {
                                            Files.createDirectory(thumbnailsDirectory)
                                        }
                                    }
                                }

                                val originalImage = runInterruptible(dispatcherProvider.io) {
                                    ImageIO.read(absoluteImagePath.toFile())
                                }
                                model.imageSize = Size(originalImage.width.toFloat(), originalImage.height.toFloat())

                                try {
                                    thumbnailsRepository.create(model, originalImage)
                                    thumbnailsRepository.writeMetadata(model, model.imageSize)
                                } catch (e: java.lang.Exception) {
                                    _log.error("Can't create thumbnail image", e)
                                    throw DisplayableException("Image preview can't be created")
                                }
                            }
                        }
                        model
                    }.toList()
            }
        } catch (e: Exception) {
            _log.error("Can't load images", e)
            throw DisplayableException("Images can't be loaded")
        }
    }
}