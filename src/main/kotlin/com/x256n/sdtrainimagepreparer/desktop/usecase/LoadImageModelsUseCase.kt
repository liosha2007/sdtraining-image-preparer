package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.resizeImage
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.AttributeView
import java.nio.file.attribute.BasicFileAttributes
import javax.imageio.ImageIO
import kotlin.io.path.extension


class LoadImageModelsUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(projectDirectory: Path, callback: (model: ImageModel) -> Unit) {
        try {
            val projectConfig = projectConfigRepository.load(projectDirectory)

            runInterruptible(dispatcherProvider.io) {
                Files.walkFileTree(projectDirectory, object : SimpleFileVisitor<Path>() {
                    override fun visitFile(absoluteImagePath: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        if (absoluteImagePath != null && projectConfig.supportedImageFormats.contains(absoluteImagePath.extension)) {
                            CoroutineScope(dispatcherProvider.default).launch {
                                val relativeImagePath = projectDirectory.relativize(absoluteImagePath)

                                val model = ImageModel(
                                    projectDirectory = projectDirectory,
                                    imagePath = relativeImagePath,
                                    captionExtension = projectConfig.captionExtension,
                                )

                                val thumbnailExist = runInterruptible(dispatcherProvider.io) { Files.exists(model.thumbnailPath) }
                                if (thumbnailExist) {
                                    model.imageWidth = runInterruptible(dispatcherProvider.io) {
                                        val byteBuffer = Files.getAttribute(model.thumbnailPath, "user:image-width")
                                        ByteBuffer.wrap((byteBuffer as ByteArray)).int
                                    }
                                    model.imageHeight = runInterruptible(dispatcherProvider.io) {
                                        val byteBuffer = Files.getAttribute(model.thumbnailPath, "user:image-height")
                                        ByteBuffer.wrap((byteBuffer as ByteArray)).int
                                    }
                                } else {
                                    val thumbnailsDirectory = model.thumbnailPath.parent
                                    runInterruptible(dispatcherProvider.io) {
                                        if (Files.notExists(thumbnailsDirectory) || !Files.isDirectory(thumbnailsDirectory)) {
                                            Files.createDirectory(model.thumbnailPath.parent)
                                        }
                                    }

                                    val originalImage = runInterruptible(dispatcherProvider.io) {
                                        ImageIO.read(absoluteImagePath.toFile())
                                    }
                                    model.imageWidth = originalImage.width
                                    model.imageHeight = originalImage.height

                                    try {
                                        val thumbnailsWidth = configManager.thumbnailsWidth
                                        val ratio = model.imageWidth.toFloat() / model.imageHeight.toFloat()

                                        val resizedImage = originalImage.resizeImage(thumbnailsWidth, (thumbnailsWidth / ratio).toInt())
                                        runInterruptible(dispatcherProvider.io) {
                                            ImageIO.write(resizedImage, "png", model.thumbnailPath.toFile())
                                        }
                                        runInterruptible(dispatcherProvider.io) {
                                            val byteBuffer = ByteBuffer.allocate(4)
                                            byteBuffer.putInt(model.imageWidth)
                                            Files.setAttribute(model.thumbnailPath, "user:image-width", byteBuffer.array())
                                        }
                                        runInterruptible(dispatcherProvider.io) {
                                            val byteBuffer = ByteBuffer.allocate(4)
                                            byteBuffer.putInt(model.imageHeight)
                                            Files.setAttribute(model.thumbnailPath, "user:image-height", byteBuffer.array())
                                        }
                                    } catch (e: java.lang.Exception) {
                                        _log.error("Can't create thumbnail image", e)
                                        throw DisplayableException("Image preview can't be created")
                                    }
                                }

                                withContext(dispatcherProvider.main) {
                                    callback(model)
                                }
                            }
                        }
                        return FileVisitResult.CONTINUE
                    }

                    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                        return if (projectDirectory == dir) {
                            FileVisitResult.CONTINUE
                        } else {
                            FileVisitResult.SKIP_SUBTREE
                        }
                    }
                })
            }
        } catch (e: Exception) {
            _log.error("Can't load images", e)
            throw DisplayableException("Images can't be loaded")
        }
    }
}