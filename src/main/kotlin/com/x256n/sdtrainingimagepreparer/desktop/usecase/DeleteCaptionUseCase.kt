package com.x256n.sdtrainingimagepreparer.desktop.usecase

import com.x256n.sdtrainingimagepreparer.desktop.common.CantSaveCaptionException
import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainingimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainingimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory

class DeleteCaptionUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(model: ImageModel, isDeleteOnlyEmpty: Boolean) {
        try {
            if (captionRepository.exist(model)) {
                val captionContent = captionRepository.read(model)
                if (!isDeleteOnlyEmpty || captionContent.isBlank()) {
                    captionRepository.delete(model)
                }
            }
        } catch (e: CantSaveCaptionException) {
            _log.error("Can't delete caption file", e)
            throw DisplayableException("Caption file can't be deleted")
        }
    }
}