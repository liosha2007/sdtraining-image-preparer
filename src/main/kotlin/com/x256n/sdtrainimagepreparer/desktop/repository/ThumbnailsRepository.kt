package com.x256n.sdtrainimagepreparer.desktop.repository

import androidx.compose.ui.geometry.Size
import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.resizeImage
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.model.ImageModel
import kotlinx.coroutines.runInterruptible
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.file.Files
import javax.imageio.ImageIO

interface ThumbnailsRepository {
    suspend fun create(model: ImageModel, originalImage: BufferedImage)
    suspend fun exist(model: ImageModel): Boolean
    suspend fun readMetadata(model: ImageModel): Size
    suspend fun writeMetadata(model: ImageModel, size: Size)
    suspend fun delete(model: ImageModel)
}

class ThumbnailsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider = StandardDispatcherProvider(),
    private val configManager: ConfigManager
) : ThumbnailsRepository {
    private val _log = LoggerFactory.getLogger(this::class.java)

    override suspend fun create(model: ImageModel, originalImage: BufferedImage) {
        val thumbnailsWidth = configManager.thumbnailsWidth
        val ratio = originalImage.width.toFloat() / originalImage.height.toFloat()

        val resizedImage = originalImage.resizeImage(thumbnailsWidth, (thumbnailsWidth / ratio).toInt())

        runInterruptible(dispatcherProvider.io) {
            ImageIO.write(resizedImage, configManager.thumbnailsFormat, model.thumbnailPath.toFile())
        }
    }

    override suspend fun exist(model: ImageModel): Boolean {
        return runInterruptible(dispatcherProvider.io) { Files.exists(model.thumbnailPath) }
    }

    override suspend fun readMetadata(model: ImageModel): Size {
        val width = runInterruptible(dispatcherProvider.io) {
            val byteBuffer = Files.getAttribute(model.thumbnailPath, "user:image-width")
            ByteBuffer.wrap((byteBuffer as ByteArray)).int
        }
        val height = runInterruptible(dispatcherProvider.io) {
            val byteBuffer = Files.getAttribute(model.thumbnailPath, "user:image-height")
            ByteBuffer.wrap((byteBuffer as ByteArray)).int
        }
        return Size(width.toFloat(), height.toFloat())
    }

    override suspend fun writeMetadata(model: ImageModel, size: Size) {
        runInterruptible(dispatcherProvider.io) {
            val byteBuffer = ByteBuffer.allocate(4)
            byteBuffer.putInt(model.imageSize.width.toInt())
            Files.setAttribute(model.thumbnailPath, "user:image-width", byteBuffer.array())
        }
        runInterruptible(dispatcherProvider.io) {
            val byteBuffer = ByteBuffer.allocate(4)
            byteBuffer.putInt(model.imageSize.height.toInt())
            Files.setAttribute(model.thumbnailPath, "user:image-height", byteBuffer.array())
        }
    }

    override suspend fun delete(model: ImageModel) {
        runInterruptible(dispatcherProvider.io) {
            if (Files.exists(model.thumbnailPath)) {
                Files.delete(model.thumbnailPath)
            }
        }
    }
}