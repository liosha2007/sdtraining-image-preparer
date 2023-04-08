package com.x256n.sdtrainimagepreparer.desktop.repository

import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.model.ProjectConfig
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

interface ProjectConfigRepository {
    suspend fun save(projectDirectory: Path, data: ProjectConfig)

    suspend fun load(projectDirectory: Path): ProjectConfig

    suspend fun delete(projectDirectory: Path)

    suspend fun check(projectDirectory: Path)
}

class ProjectConfigRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val json: Json
) : ProjectConfigRepository {
    private val _log = LoggerFactory.getLogger("SampleModelRepositoryImpl")

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun save(projectDirectory: Path, data: ProjectConfig) {
        withContext(dispatcherProvider.io) {
            if (Files.notExists(projectDirectory) || !Files.isDirectory(projectDirectory)) {
                throw ProjectDirectoryDoesNotExist(projectDirectory)
            }
            val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
            if (Files.exists(projectConfigDirectory)) {
                throw ProjectAlreadyExist(projectDirectory)
            }
            val configPath = projectConfigDirectory.resolve(Constants.CONFIG_FILE_NAME)
            kotlin.runCatching {
                Files.createDirectory(projectConfigDirectory)

                FileOutputStream(configPath.toFile()).use { stream ->
                    json.encodeToStream(data, stream)
                }
            }.onFailure { e ->
                _log.error(e.localizedMessage ?: "Can't save SampleModel list!")
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun load(projectDirectory: Path): ProjectConfig {
        val configPath = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME).resolve(Constants.CONFIG_FILE_NAME)
        return withContext(dispatcherProvider.io) {
            if (Files.exists(configPath)) {
                kotlin.runCatching {
                    FileInputStream(configPath.toFile()).use { stream ->
                        return@withContext json.decodeFromStream<ProjectConfig>(stream)
                    }
                }.onFailure { e ->
                    _log.error("Can't load SampleModel list", e)
                }
            }
            throw ProjectConfigNotFoundException(configPath)
        }
    }

    override suspend fun delete(projectDirectory: Path) {
        val projectPath = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
        return withContext(dispatcherProvider.io) {
            if (Files.exists(projectPath)) {
                kotlin.runCatching {
                    Files.walkFileTree(projectPath, object : SimpleFileVisitor<Path>() {
                        override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                            dir?.let {
                                Files.delete(dir)
                            }
                            return FileVisitResult.CONTINUE
                        }

                        override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                            file?.let {
                                Files.delete(file)
                            }
                            return FileVisitResult.CONTINUE
                        }
                    })
                }.onFailure { e ->
                    _log.error("Can't delete config", e)
                    throw CantDeleteProjectException(projectPath)
                }
            }
        }
    }

    override suspend fun check(projectDirectory: Path) {
        val projectPath = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
        return withContext(dispatcherProvider.io) {
            if (Files.notExists(projectPath)) {
                throw NotAProjectException(projectDirectory)
            }
            val configPath = projectPath.resolve(Constants.CONFIG_FILE_NAME)
            if (Files.notExists(configPath)) {
                throw ProjectBrokenException(configPath)
            }
        }
    }
}