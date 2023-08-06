package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.theme.DefaultTheme
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component.TooltipIcon

@Composable
fun ImageCropModeToolbarPanel(
    show: Boolean = true,
    onCropMode512: () -> Unit = {},
    onCropMode768: () -> Unit = {},
    onCropModeMax: () -> Unit = {},
    onCropModeApply: () -> Unit = {},
    onCropModeCancel: () -> Unit = {}
) {
    if (show) {

        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = "Crop settings:"
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TooltipIcon(
                modifier = Modifier
                    .padding(2.dp),
                iconResource = "nothing.png",
                tooltip = "512x512",
                onClick = onCropMode512
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(2.dp),
                iconResource = "nothing.png",
                tooltip = "768x768",
                onClick = onCropMode768
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(2.dp),
                iconResource = "nothing.png",
                tooltip = "MAX square",
                onClick = onCropModeMax
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .padding(horizontal = 2.dp, 2.dp)
                    .background(Color.LightGray)
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(2.dp),
                iconResource = "cancel.png",
                tooltip = "Cancel",
                onClick = onCropModeCancel
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(2.dp),
                iconResource = "apply.png",
                tooltip = "Apply crop",
                onClick = onCropModeApply
            )
        }
    }
}


@Preview
@Composable
fun ImageCropModeToolbarPanelPreview() {
    MaterialTheme {
        DefaultTheme {
            ImageCropModeToolbarPanel()
        }
    }
}