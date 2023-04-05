package com.x256n.sdtrainimagepreparer.desktop.usecase

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.SampleModelRepository
import org.slf4j.LoggerFactory


class DoSampleModelUseCase(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager,
    private val sampleModelRepository: SampleModelRepository
) {
    private val _log = LoggerFactory.getLogger("MakeScreenshotUseCase")

    suspend operator fun invoke(): String {
        return "Hi from UseCase!"
    }
}