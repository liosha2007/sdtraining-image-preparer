package com.x256n.sdtrainimagepreparer.desktop.repository

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.model.SampleModel
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

interface SampleModelRepository {
    suspend fun save(data: List<SampleModel>)

    suspend fun load(): List<SampleModel>
    suspend fun delete(model: SampleModel)
}

class SampleModelRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val json: Json
) : SampleModelRepository {
    private val _log = LoggerFactory.getLogger("SampleModelRepositoryImpl")

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun save(data: List<SampleModel>) {
        withContext(dispatcherProvider.io) {
            val configPath = Paths.get(ConfigPath)
            kotlin.runCatching {
                FileOutputStream(configPath.toFile()).use { stream ->
                    json.encodeToStream(data, stream)
                }
            }.onFailure { e ->
                _log.error(e.localizedMessage ?: "Can't save SampleModel list!")
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun load(): List<SampleModel> {
        val configPath = Paths.get(ConfigPath)
        return withContext(dispatcherProvider.io) {
            if (Files.exists(configPath)) {
                kotlin.runCatching {
                    FileInputStream(configPath.toFile()).use { stream ->
                        return@withContext json.decodeFromStream<List<SampleModel>>(stream)
                    }
                }.onFailure { e ->
                    _log.error("Can't load SampleModel list", e)
                }
            }
            emptyList()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun delete(model: SampleModel) {
        val modelList = load().toMutableList()

        modelList.removeIf { it.name == model.name }

        withContext(dispatcherProvider.io) {
            val configPath = Paths.get(ConfigPath)
            kotlin.runCatching {
                FileOutputStream(configPath.toFile()).use { stream ->
                    json.encodeToStream(modelList, stream)
                }
            }.onFailure { e ->
                _log.error(e.localizedMessage ?: "Can't delete SampleModel!")
            }
        }
    }

    private companion object {
        const val ConfigPath = "samplemodels.json"
    }
}