package com.x256n.sdtrainingimagepreparer.desktop.usecase

import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainingimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory


class SplitCaptionUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    operator fun invoke(captionContent: String): List<String> {
        try {
            return captionRepository.split(captionContent)
        } catch (e: Exception) {
            _log.error("Can't split caption content to keywords", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}