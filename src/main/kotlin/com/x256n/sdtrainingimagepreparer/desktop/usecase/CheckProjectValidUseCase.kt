package com.x256n.sdtrainingimagepreparer.desktop.usecase

import com.x256n.sdtrainingimagepreparer.desktop.common.*
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory
import java.nio.file.Path


class CheckProjectValidUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(projectDirectory: Path) {
        try {
            projectConfigRepository.check(projectDirectory)
        } catch (e: NotAProjectException) {
            _log.error("The directory does not have project", e)
            throw DisplayableException("The directory is not a project")
        } catch (e: CantLoadProjectException) {
            _log.error("Project checking failed", e)
            throw DisplayableException("The project is broken")
        }
    }
}