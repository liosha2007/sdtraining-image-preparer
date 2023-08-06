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
fun CaptionToolbarPanel(
    show: Boolean = true,
    enabled: Boolean = show,
    isProjectLoaded: Boolean = false,
    onCreateCaptionFiles: () -> Unit = {},
    onDeleteCaptionFiles: () -> Unit = {},
    onCaptionReplace: () -> Unit = {}
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
                iconResource = "create_caption_files.png",
                tooltip = "Create all caption files",
                enabled = enabled && isProjectLoaded,
                onClick = onCreateCaptionFiles
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "delete_caption_files.png",
                tooltip = "Delete all caption files...",
                enabled = enabled && isProjectLoaded,
                onClick = onDeleteCaptionFiles
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(Color.LightGray)
            )
            TooltipIcon(
                iconResource = "replace_in_captions.png",
                tooltip = "Replace in all caption files...",
                enabled = enabled && isProjectLoaded,
                onClick = onCaptionReplace
            )
        }
    }
}


@Preview
@Composable
fun CaptionToolbarPanelPreview() {
    MaterialTheme {
        DefaultTheme {
            CaptionToolbarPanel(
            )
        }
    }
}