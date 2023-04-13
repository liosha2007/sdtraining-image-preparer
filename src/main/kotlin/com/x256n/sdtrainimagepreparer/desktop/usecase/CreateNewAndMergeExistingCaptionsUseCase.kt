package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import com.x256n.sdtrainimagepreparer.desktop.repository.CaptionRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText


class CreateNewAndMergeExistingCaptionsUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository,
    private val captionRepository: CaptionRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(projectDirectory: Path, modelList: List<ImageModel>) {
        val supportedCaptionExtensions = configManager.supportedCaptionExtensions
        val projectConfig = projectConfigRepository.load(projectDirectory)

        if (!projectConfig.createCaptionsWhenAddingContent) {
            modelList.forEach { model ->
                if (!captionRepository.exist(model)) {
                    captionRepository.write(model)
                }
            }
        }

        if (projectConfig.mergeExistingCaptionFiles) {
            modelList.forEach { model ->
                supportedCaptionExtensions
                    .filter { it != projectConfig.captionExtension } // Do not merge with itself
                    .forEach { captionExtension ->
                        val potentialCaptionPath =
                            model.captionPath.parent.resolve(model.captionPath.nameWithoutExtension + "." + captionExtension)
                        try {
                            val potentialCaptionFileExist = withContext(dispatcherProvider.io) {
                                Files.exists(potentialCaptionPath)
                            }
                            if (potentialCaptionFileExist) {
                                _log.debug("Caption file (to be merged) exist: $potentialCaptionPath")
                                val otherCaptionContent = potentialCaptionPath.readText(StandardCharsets.UTF_8)
                                val otherCaptionKeywords = captionRepository.split(otherCaptionContent)

                                val captionContent = if (captionRepository.exist(model)) {
                                    captionRepository.read(model)
                                } else ""

                                val captionKeywords = captionRepository.split(captionContent)
                                val resultKeywords = mutableSetOf<String>().apply {
                                    addAll(captionKeywords)
                                    addAll(otherCaptionKeywords)
                                }

                                val resultContent = captionRepository.join(resultKeywords.toList())
                                _log.debug("Saving data to caption file, count of merged keywords: ${resultKeywords.size}")
                                captionRepository.write(model, resultContent)

                                _log.debug("Deleting merged caption file: $potentialCaptionPath")
                                withContext(dispatcherProvider.io) {
                                    Files.delete(potentialCaptionPath)
                                }
                            }
                        } catch (e: Exception) {
                            _log.error("Can't merge caption file $potentialCaptionPath", e)
                        }
                    }
            }
        }
    }
}