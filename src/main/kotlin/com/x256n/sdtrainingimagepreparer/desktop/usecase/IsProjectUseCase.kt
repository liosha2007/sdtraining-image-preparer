package com.x256n.sdtrainingimagepreparer.desktop.usecase

import com.x256n.sdtrainingimagepreparer.desktop.common.*
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.repository.ProjectConfigRepository
import org.slf4j.LoggerFactory
import java.nio.file.Path


class IsProjectUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val projectConfigRepository: ProjectConfigRepository
) {
    private val _log = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(directoryToCheck: Path): Boolean {
        try {
            projectConfigRepository.check(directoryToCheck)
            return true
        } catch (e: NotAProjectException) {
            _log.debug("The directory does not have project", e)
        } catch (e: CantLoadProjectException) {
            _log.debug("Project checking failed", e)
        }
        return false
    }
}