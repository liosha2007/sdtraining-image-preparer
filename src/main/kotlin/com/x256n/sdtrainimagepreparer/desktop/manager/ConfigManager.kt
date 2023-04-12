package com.x256n.sdtrainimagepreparer.desktop.manager

import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ConfigManager {
    private val _log = LoggerFactory.getLogger("ConfigManager")

    val isDebugMode: Boolean
        get() {
            key_isDebugMode.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "false"
                }
                return (config[this] as String).toBoolean()
            }
        }

    val thumbnailsWidth: Int
        get() {
            key_thumbnailsWidth.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "168"
                }
                return (config[this] as String).toInt()
            }
        }

    val thumbnailsFormat: String
        get() {
            key_thumbnailsFormat.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "png"
                }
                return (config[this] as String)
            }
        }

    val keywordsDelimiter: String
        get() {
            key_keywordsDelimiter.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = ","
                }
                return (config[this] as String)
            }
        }

    val openLastProjectOnStart: Boolean
        get() {
            key_openLastProjectOnStart.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "true"
                }
                return (config[this] as String).toBoolean()
            }
        }

    var lastProjectPath: String
        get() {
            key_lastProjectPath.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = ""
                }
                return (config[this] as String)
            }
        }
        set(value) {
            config[key_lastProjectPath] = value
            FileOutputStream(propertiesPath.toFile()).use { outputStream ->
                config.store(outputStream, null)
                _log.info("Default config saved: $propertiesPath\n\tlastProjectPath was changed")
            }
        }

    val supportedImageFormats: List<String>
        get() {
            key_supportedImageFormats.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "png,jpg"
                }
                return (config[this] as String).split(',').map { it.trim() }
            }
        }

    val supportedCaptionExtensions: List<String>
        get() {
            key_supportedCaptionExtensions.apply {
                if (!config.containsKey(this) || (config[this] as String).isBlank()) {
                    config[this] = "txt,caption"
                }
                return (config[this] as String).split(',').map { it.trim() }
            }
        }

    companion object {
        val config = Properties()
        private val propertiesPath: Path = Paths.get(".", "config.properties").toAbsolutePath().normalize()

        fun reloadConfig() {
            try {
                FileInputStream(propertiesPath.toFile()).use { inputStream ->
                    config.load(inputStream)
                }
            } catch (e: Exception) {
                val log = LoggerFactory.getLogger("ConfigManager")
                log.warn("Can't read config: $propertiesPath")
                // Default values for saving config file
                config[key_isDebugMode] = "false"
                config[key_thumbnailsWidth] = "168"
                config[key_thumbnailsFormat] = "png"
                config[key_keywordsDelimiter] = ","
                config[key_openLastProjectOnStart] = "true"
                config[key_lastProjectPath] = ""
                config[key_supportedImageFormats] = "png,jpg"
                config[key_supportedCaptionExtensions] = "txt,caption"

                if (Files.notExists(propertiesPath)) {
                    try {
                        FileOutputStream(propertiesPath.toFile()).use { outputStream ->
                            config.store(outputStream, "Default config")
                            log.info("Default config saved: $propertiesPath")
                        }
                    } catch (e: Exception) {
                        log.warn("Can't write default config: $propertiesPath")
                    }
                }
            }
        }

        const val key_isDebugMode = "isDebugMode"
        const val key_thumbnailsWidth = "thumbnailsWidth"
        const val key_thumbnailsFormat = "thumbnailsFormat"
        const val key_keywordsDelimiter = "keywordsDelimiter"
        const val key_openLastProjectOnStart = "openLastProjectOnStart"
        const val key_lastProjectPath = "lastProjectPath"
        const val key_supportedImageFormats = "supportedImageFormats"
        const val key_supportedCaptionExtensions = "supportedCaptionExtensions"
    }
}