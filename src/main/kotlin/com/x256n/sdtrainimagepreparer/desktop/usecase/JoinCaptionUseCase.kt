package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory


class JoinCaptionUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    operator fun invoke(captionKeywords: List<String>): String {
        try {
            return captionRepository.join(captionKeywords)
        } catch (e: Exception) {
            _log.error("Can't join keywords to caption content", e)
            throw DisplayableException("Unexpected error: ${e.message}")
        }
    }
}