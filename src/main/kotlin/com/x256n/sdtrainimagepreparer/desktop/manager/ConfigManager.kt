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

    val thumbnailsWidth: Int = 168
    val captionDelimiter: String = ","

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
    }
}