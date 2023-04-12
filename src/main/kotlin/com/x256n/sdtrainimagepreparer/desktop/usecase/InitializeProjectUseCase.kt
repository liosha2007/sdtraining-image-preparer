package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ProjectConfig
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory
import java.nio.file.Path


class InitializeProjectUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        imageDirectory: String,
        overrideExistingProject: Boolean,
        captionExtension: String,
        targetImageResolution: Int,
        mergeExistingCaptionFiles: Boolean,
        createCaptionsWhenAddingContent: Boolean,
        targetImageFormat: String,
    ) {
        val model = ProjectConfig(
            captionExtension = captionExtension,
            targetImageResolution = targetImageResolution,
            mergeExistingCaptionFiles = mergeExistingCaptionFiles,
            createCaptionsWhenAddingContent = createCaptionsWhenAddingContent,
            targetImageFormat = targetImageFormat
        )
        val projectDirectory = Path.of(imageDirectory)
        try {
            if (overrideExistingProject) {
                projectConfigRepository.delete(projectDirectory)
            }
            projectConfigRepository.create(projectDirectory, model)
        } catch (e: ProjectDirectoryDoesNotExist) {
            _log.error("Can't save project config", e)
            throw DisplayableException("Project directory does not exist")
        } catch (e: CantDeleteProjectException) {
            _log.error("Can't delete project", e)
            throw DisplayableException("Existing project can't be deleted")
        } catch (e: ProjectAlreadyExist) {
            _log.warn("Project already exist", e)
            throw DisplayableException("Project already exist in the directory")
        }
    }
}