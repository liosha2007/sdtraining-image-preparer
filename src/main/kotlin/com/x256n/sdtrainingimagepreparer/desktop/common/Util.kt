package com.x256n.sdtrainingimagepreparer.desktop.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.awt.Image
import java.awt.image.BufferedImage

fun BufferedImage.resizeImage(targetWidth: Int, targetHeight: Int): BufferedImage {
    val resultingImage = this.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT)
    return BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB).apply {
        graphics.drawImage(resultingImage, 0, 0, null)
    }
}

fun BufferedImage.cropImage(offset: Offset, size: Size): BufferedImage {
    // TODO: Need some checks...
    return this.getSubimage(offset.x.toInt(), offset.y.toInt(), size.width.toInt(), size.height.toInt())
}