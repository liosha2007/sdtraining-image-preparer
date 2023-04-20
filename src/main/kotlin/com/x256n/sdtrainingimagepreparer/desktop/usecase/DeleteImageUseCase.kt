package com.x256n.sdtrainingimagepreparer.desktop.usecase

import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainingimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainingimagepreparer.desktop.repository.ThumbnailsRepository
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import java.nio.file.Files

class DeleteImageUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val thumbnailsRepository: ThumbnailsRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(model: ImageModel) {
        try {
            thumbnailsRepository.delete(model)
            captionRepository.delete(model)
            _log.debug("Deleting image: {}", model.absoluteImagePath)
            runInterruptible { Files.delete(model.absoluteImagePath) }
        } catch (e: Exception) {
            _log.error("Can't delete image", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}