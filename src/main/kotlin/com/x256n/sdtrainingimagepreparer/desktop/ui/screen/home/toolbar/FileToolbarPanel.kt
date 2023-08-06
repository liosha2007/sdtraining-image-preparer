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
fun FileToolbarPanel(
    show: Boolean = true,
    enabled: Boolean = show,
    isProjectLoaded: Boolean = false,
    onCreateProject: () -> Unit = {},
    onOpenFolderOrProject: () -> Unit = {},
    onCloseFolderOrProject: () -> Unit = {},
    onClearProject: () -> Unit = {}
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
                iconResource = "create_project.png",
                tooltip = "Create new project...",
                enabled = enabled,
                onClick = onCreateProject
            )
            TooltipIcon(
                iconResource = "open_folder.png",
                tooltip = "Open folder or project...",
                enabled = enabled,
                onClick = onOpenFolderOrProject
            )
            TooltipIcon(
                modifier = Modifier
                    .padding(vertical = 2.dp),
                iconResource = "close.png",
                tooltip = "Close folder or project...",
                enabled = enabled && isProjectLoaded,
                onClick = onCloseFolderOrProject
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(Color.LightGray)
            )
            TooltipIcon(
                iconResource = "clear.png",
                tooltip = "Clear project...",
                enabled = enabled && isProjectLoaded,
                onClick = onClearProject
            )
        }
    }
}


@Preview
@Composable
fun FileToolbarPanelPreview() {
    MaterialTheme {
        DefaultTheme {
            FileToolbarPanel()
        }
    }
}