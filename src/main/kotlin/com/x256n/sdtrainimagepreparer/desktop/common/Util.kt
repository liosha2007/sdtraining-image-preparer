package com.x256n.sdtrainimagepreparer.desktop.common

import java.awt.Image
import java.awt.image.BufferedImage

fun BufferedImage.resizeImage(targetWidth: Int, targetHeight: Int): BufferedImage {
    val resultingImage = this.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT)
    return BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB).apply {
        graphics.drawImage(resultingImage, 0, 0, null)
    }
}