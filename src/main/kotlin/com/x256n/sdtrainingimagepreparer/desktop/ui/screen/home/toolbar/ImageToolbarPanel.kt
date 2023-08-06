package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.theme.DefaultTheme
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component.TooltipIcon

@Composable
fun ImageToolbarPanel(
    show: Boolean = true,
    enabled: Boolean = show,
    isProjectLoaded: Boolean = false,
    isImageSelected: Boolean = false,
    onAddImages: () -> Unit = {},
    onDeleteImage: () -> Unit = {},
    onImageCrop: () -> Unit = {},
    onConvertAllImages: () -> Unit = {},
    onSyncImages: () -> Unit = {},
) {
    Spacer(
        modifier = Modifier
            .fillMaxHeight()
            .width(12.dp)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .background(Color.LightGray)
    )

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(start = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (show) {
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "add_images.png",
                tooltip = "Add images...",
                enabled = enabled && isProjectLoaded,
                onClick = onAddImages
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "delete_image.png",
                tooltip = "Delete image...",
                enabled = enabled && isImageSelected,
                onClick = onDeleteImage
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "crop.png",
                tooltip = "Crop image...",
                enabled = enabled && isImageSelected,
                onClick = onImageCrop
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "convert_all.png",
                tooltip = "Convert all images...",
                enabled = enabled && isProjectLoaded,
                onClick = onConvertAllImages
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(Color.LightGray)
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "sync.png",
                tooltip = "Sunc images",
                enabled = enabled && isProjectLoaded,
                onClick = onSyncImages
            )
        }
    }
}


@Preview
@Composable
fun ImageToolbarPanelPreview() {
    MaterialTheme {
        DefaultTheme {
            ImageToolbarPanel()
        }
    }
}