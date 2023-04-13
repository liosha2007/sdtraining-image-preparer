package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.CantLoadCaptionException
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.DisplayableException
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.model.KeywordModel
import com.x256n.sdtrainimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory


class ExtractCaptionKeywordsUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(model: ImageModel): List<KeywordModel> {
        try {
            val keywordMap = mutableMapOf<String, Int>()
            val captionContent = captionRepository.read(model)
            captionRepository.split(captionContent)
                .forEach { captionKeyword ->
                    keywordMap[captionKeyword] = keywordMap.getOrDefault(captionKeyword, 0) + 1
                }
            return keywordMap.map {
                KeywordModel(
                    keyword = it.key,
                    usageCount = it.value
                )
            }
                .sortedBy { it.keyword }
                .sortedByDescending { it.usageCount }
        } catch (e: CantLoadCaptionException) {
            _log.error("Can't extract caption keywords", e)
            throw DisplayableException("Caption keywords can't be extracted")
        }
    }
}