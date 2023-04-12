package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.Constants
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension


class RemoveIncorrectThumbnailsUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(projectDirectory: Path) {
        try {
            val projectConfig = projectConfigRepository.load(projectDirectory)
            val thumbnailsDirectory = projectDirectory
                .resolve(Constants.PROJECT_DIRECTORY_NAME)
                .resolve(Constants.THUMBNAILS_DIRECTORY_NAME)

            runInterruptible(dispatcherProvider.io) {
                if (Files.exists(thumbnailsDirectory) && Files.isDirectory(thumbnailsDirectory)) {
                    Files.walkFileTree(thumbnailsDirectory, object : SimpleFileVisitor<Path>() {
                        override fun visitFile(absoluteThumbnailPath: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                            if (absoluteThumbnailPath != null && absoluteThumbnailPath.extension == configManager.thumbnailsFormat) {
                                CoroutineScope(dispatcherProvider.default).launch {
                                    val sourceImage = projectDirectory.resolve(thumbnailsDirectory.relativize(absoluteThumbnailPath))
                                    val isSourceNotExist = runInterruptible(dispatcherProvider.io) {
                                        Files.notExists(sourceImage)
                                    }
                                    if (isSourceNotExist) {
                                        runInterruptible {
                                            _log.debug("Deleting thumbnail (source image does not exist): '$absoluteThumbnailPath'")
                                            Files.delete(absoluteThumbnailPath)
                                        }
                                    } else {
                                        val imageLastModifiedTime = runInterruptible(dispatcherProvider.io) {
                                            Files.getLastModifiedTime(sourceImage)
                                        }
                                        val thumbnailLastModifiedTime = runInterruptible(dispatcherProvider.io) {
                                            Files.getLastModifiedTime(absoluteThumbnailPath)
                                        }
                                        if (imageLastModifiedTime.toMillis() > thumbnailLastModifiedTime.toMillis()) {
                                            runInterruptible {
                                                _log.debug("Deleting thumbnail (source image was changed): '$absoluteThumbnailPath'")
                                                Files.delete(absoluteThumbnailPath)
                                            }
                                        }
                                    }
                                }
                            }
                            return FileVisitResult.CONTINUE
                        }
                    })
                }
            }
        } catch (e: Exception) {
            _log.error("Can't remove incorrect thumbnails", e)
            throw DisplayableException("Incorrect thumbnails can't be removed")
        }
    }
}