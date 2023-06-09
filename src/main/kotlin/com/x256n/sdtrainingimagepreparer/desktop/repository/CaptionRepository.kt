package com.x256n.sdtrainingimagepreparer.desktop.repository

import com.x256n.sdtrainingimagepreparer.desktop.common.*
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.model.ImageModel
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.io.path.writeText

interface CaptionRepository {

    suspend fun exist(model: ImageModel): Boolean

    suspend fun read(model: ImageModel): String

    suspend fun write(model: ImageModel, captionContent: String = "")

    suspend fun delete(model: ImageModel)

    fun split(captionContent: String): List<String>
    fun join(keywordList: List<String>): String
}

class CaptionRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager
) : CaptionRepository {
    private val _log = LoggerFactory.getLogger(this::class.java)

    override suspend fun exist(model: ImageModel): Boolean {
        return withContext(dispatcherProvider.io) {
            val captionPath = model.captionPath
            return@withContext Files.exists(captionPath) && Files.isRegularFile(captionPath) && Files.isReadable(captionPath)
        }
    }

    override suspend fun write(model: ImageModel, captionContent: String) {
        withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            try {
                if (Files.exists(captionPath) && !Files.isWritable(captionPath)) {
                    _log.debug("Can't write to caption file: {}", captionPath)
                    throw CantCreateCaptionException(captionPath)
                } else {
                    runInterruptible(dispatcherProvider.io) {
                        captionPath.writeText(captionContent, StandardCharsets.UTF_8)
                    }
                }
            } catch (e: IOException) {
                _log.error("Can't write to caption file", e)
                throw CantSaveCaptionException(captionPath)
            }
        }
    }

    override suspend fun delete(model: ImageModel) {
        withContext(dispatcherProvider.default) {
            try {
                if (Files.exists(model.captionPath)) {
                    _log.debug("Deleting caption file: {}", model.captionPath)
                    runInterruptible { Files.delete(model.captionPath) }
                }
            } catch (e: IOException) {
                _log.error("Can't delete caption file", e)
                throw CantSaveCaptionException(model.captionPath)
            }
        }
    }

    override suspend fun read(model: ImageModel): String {
        return withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            _log.trace("captionPath: $captionPath")
            try {
                if (Files.notExists(captionPath)) {
                    return@withContext ""
                } else if (!Files.isRegularFile(captionPath) || !Files.isReadable(captionPath)) {
                    throw CantLoadCaptionException(captionPath)
                } else {
                    return@withContext runInterruptible(dispatcherProvider.io) {
                        _log.debug("Reading caption file '${model.captionPath.fileName}'")
                        return@runInterruptible captionPath.readText(StandardCharsets.UTF_8)
                    }
                }
            } catch (e: Exception) {
                _log.error("Can't read from caption file", e)
                throw CantLoadCaptionException(captionPath)
            }
        }
    }

    override fun split(captionContent: String): List<String> {
        return captionContent.split(configManager.keywordsDelimiter)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
            .toList()
    }

    override fun join(keywordList: List<String>): String {
        return keywordList.joinToString("${configManager.keywordsDelimiter} ")
    }
}