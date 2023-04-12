package com.x256n.sdtrainimagepreparer.desktop.repository

import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
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

    suspend fun create(model: ImageModel, captionContent: String? = null)
    suspend fun exist(model: ImageModel): Boolean

    suspend fun load(model: ImageModel): String
    suspend fun save(model: ImageModel, captionContent: String)

    fun split(captionContent: String): List<String>
    fun join(keywordList: List<String>): String
}

class CaptionRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager
) : CaptionRepository {
    private val _log = LoggerFactory.getLogger(this::class.java)

    override suspend fun create(model: ImageModel, captionContent: String?) {
        withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            try {
                if (Files.exists(captionPath) && Files.isRegularFile(captionPath)) {
                    _log.debug("Can't create caption file because it is already exist: {}", captionPath)
                    throw CantCreateCaptionException(captionPath)
                } else {
                    runInterruptible(dispatcherProvider.io) {
                        captionPath.writeText(captionContent ?: "", StandardCharsets.UTF_8)
                    }
                }
            } catch (e: IOException) {
                _log.error("Can't create caption file", e)
                throw CantCreateCaptionException(captionPath)
            }
        }
    }

    override suspend fun exist(model: ImageModel): Boolean {
        return withContext(dispatcherProvider.io) {
            val captionPath = model.captionPath
            return@withContext Files.exists(captionPath) && Files.isRegularFile(captionPath) && Files.isReadable(captionPath)
        }
    }

    override suspend fun save(model: ImageModel, captionContent: String) {
        withContext(dispatcherProvider.default) {
            val captionPath = model.captionPath
            try {
                if (Files.notExists(captionPath)
                    || (Files.exists(captionPath) && (!Files.isRegularFile(captionPath) || !Files.isWritable(captionPath)))
                ) {
                    _log.error("Can't save caption file because it is not exist or is not writable")
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