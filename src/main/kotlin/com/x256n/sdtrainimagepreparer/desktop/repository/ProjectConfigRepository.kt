@file:OptIn(ExperimentalSerializationApi::class)

package com.x256n.sdtrainimagepreparer.desktop.repository

import com.x256n.sdtrainimagepreparer.desktop.common.*
import com.x256n.sdtrainimagepreparer.desktop.model.ProjectConfig
import kotlinx.coroutines.runInterruptible
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
    suspend fun create(projectDirectory: Path, data: ProjectConfig)

    suspend fun save(projectDirectory: Path, data: ProjectConfig)

    suspend fun load(projectDirectory: Path): ProjectConfig

    suspend fun delete(projectDirectory: Path)

    suspend fun check(projectDirectory: Path)
}

class ProjectConfigRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val json: Json
) : ProjectConfigRepository {
    private val _log = LoggerFactory.getLogger(this::class.java)

    override suspend fun create(projectDirectory: Path, data: ProjectConfig) {
        withContext(dispatcherProvider.default) {
            projectDirectory.assertExist(::ProjectDirectoryDoesNotExist)

            val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
            projectConfigDirectory.assertNotExist(::ProjectAlreadyExist)

            val configPath = projectConfigDirectory.resolve(Constants.CONFIG_FILE_NAME)

            try {
                runInterruptible(dispatcherProvider.io) {
                    Files.createDirectory(projectConfigDirectory)

                    FileOutputStream(configPath.toFile()).use { stream ->
                        json.encodeToStream(data, stream)
                    }
                }
            } catch (e: IOException) {
                _log.error("Can't create project", e)
                throw CantCreateProjectException(configPath)
            }
        }
    }

    override suspend fun save(projectDirectory: Path, data: ProjectConfig) {
        withContext(dispatcherProvider.default) {
            projectDirectory.assertExist(::ProjectDirectoryDoesNotExist)

            val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
            projectConfigDirectory.assertExist({ NotAProjectException(it.parent) })

            val configPath = projectConfigDirectory.resolve(Constants.CONFIG_FILE_NAME)
            configPath.assertExist(::ProjectConfigNotFoundException, isFile = true)

            try {
                runInterruptible(dispatcherProvider.io) {
                    FileOutputStream(configPath.toFile()).use { stream ->
                        json.encodeToStream(data, stream)
                    }
                }
            } catch (e: Exception) {
                _log.error("Can't save project", e)
                throw CantSaveProjectException(configPath)
            }
        }
    }

    override suspend fun load(projectDirectory: Path): ProjectConfig {
        return withContext(dispatcherProvider.default) {
            projectDirectory.assertExist(::ProjectDirectoryDoesNotExist)

            val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
            projectConfigDirectory.assertExist({ NotAProjectException(it.parent) })

            val configPath = projectConfigDirectory.resolve(Constants.CONFIG_FILE_NAME)
            configPath.assertExist(::ProjectConfigNotFoundException, isFile = true)

            try {
                runInterruptible(dispatcherProvider.io) {
                    FileInputStream(configPath.toFile()).use { stream ->
                        return@runInterruptible json.decodeFromStream<ProjectConfig>(stream)
                    }
                }
            } catch (e: Exception) {
                _log.error("Can't load project config", e)
                throw CantLoadProjectException(configPath)
            }
        }
    }

    override suspend fun delete(projectDirectory: Path) {
        return withContext(dispatcherProvider.default) {
            projectDirectory.assertExist(::ProjectDirectoryDoesNotExist)

            val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
            projectConfigDirectory.assertExist({ NotAProjectException(it.parent) })

            try {
                runInterruptible(dispatcherProvider.io) {
                    Files.walkFileTree(projectConfigDirectory, object : SimpleFileVisitor<Path>() {
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
                }
            } catch (e: Exception) {
                _log.error("Can't delete config", e)
                throw CantDeleteProjectException(projectConfigDirectory)
            }
        }
    }

    override suspend fun check(projectDirectory: Path) {
        projectDirectory.assertExist(::ProjectDirectoryDoesNotExist)

        val projectConfigDirectory = projectDirectory.resolve(Constants.PROJECT_DIRECTORY_NAME)
        projectConfigDirectory.assertExist({ NotAProjectException(it.parent) })

        val configPath = projectConfigDirectory.resolve(Constants.CONFIG_FILE_NAME)
        configPath.assertExist(::ProjectConfigNotFoundException, isFile = true)
    }

    private suspend fun Path.assertExist(exceptionFactory: (path: Path) -> SDTrainImagePreparerException, isFile: Boolean = false) {
        withContext(dispatcherProvider.io) {
            if (Files.notExists(this@assertExist)
                || ((isFile && !Files.isRegularFile(this@assertExist))
                        || (!isFile && !Files.isDirectory(this@assertExist)))
            ) {
                throw exceptionFactory(this@assertExist)
            }
        }
    }

    private suspend fun Path.assertNotExist(exceptionFactory: (path: Path) -> SDTrainImagePreparerException) {
        withContext(dispatcherProvider.io) {
            if (Files.exists(this@assertNotExist)) {
                throw exceptionFactory(this@assertNotExist)
            }
        }
    }
}