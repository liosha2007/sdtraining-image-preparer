package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
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
                                    captionExtension = projectConfig.captionExtension
                                )
//                            thumbnailsJob.add(
//                                launch {
//                                    val fullThumbnailPath = projectDirectory.resolve(model.thumbnailPath)
//                                    if (Files.notExists(fullThumbnailPath)) {
//                                        if (Files.notExists(fullThumbnailPath.parent)) {
//                                            Files.createDirectory(fullThumbnailPath.parent)
//                                        }
//                                        _log.debug("Create thumbnail")
//                                        // Create thumbnail - fullThumbnailPath
//                                    }
//                                }
//                            )
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