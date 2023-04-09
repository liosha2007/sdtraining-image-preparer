package com.x256n.sdtrainimagepreparer.desktop.repository

import com.x256n.sdtrainimagepreparer.desktop.common.CantLoadCaptionException
import com.x256n.sdtrainimagepreparer.desktop.common.CantSaveCaptionException
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.io.path.writeText

interface CaptionRepository {
    suspend fun save(model: ImageModel, captionContent: String)

    suspend fun load(model: ImageModel): String
}

class CaptionRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider()
) : CaptionRepository {
    private val _log = LoggerFactory.getLogger(this::class.java)

    override suspend fun save(model: ImageModel, captionContent: String) {
        withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            try {
                if (!Files.isRegularFile(captionPath) || !Files.isWritable(captionPath)) {
                    throw CantSaveCaptionException(captionPath)
                } else {
                    runInterruptible(dispatcherProvider.io) {
                        captionPath.writeText(captionContent, StandardCharsets.UTF_8)
                    }
                }
            } catch (e: IOException) {
                _log.error("Can't save caption file", e)
                throw CantSaveCaptionException(captionPath)
            }
        }
    }

    override suspend fun load(model: ImageModel): String {
        return withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            _log.debug("captionPath: $captionPath")
            try {
                if (Files.notExists(captionPath)) {
                    return@withContext ""
                } else if (!Files.isRegularFile(captionPath) || !Files.isReadable(captionPath)) {
                    throw CantLoadCaptionException(captionPath)
                } else {
                    return@withContext runInterruptible(dispatcherProvider.io) {
                        _log.debug("Reading caption file...")
                        return@runInterruptible captionPath.readText(StandardCharsets.UTF_8)
                    }
                }
            } catch (e: Exception) {
                _log.error("Can't save caption file", e)
                throw CantLoadCaptionException(captionPath)
            }
        }
    }
}