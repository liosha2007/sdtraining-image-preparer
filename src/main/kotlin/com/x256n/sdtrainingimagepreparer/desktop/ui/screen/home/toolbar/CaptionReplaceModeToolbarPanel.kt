package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.toolbar

import WinTextField
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.theme.DefaultTheme
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.component.TooltipIcon

@Composable
fun CaptionReplaceModeToolbarPanel(
    show: Boolean = false,
    onSearchValueChange: (value: String) -> Unit = {},
    onCaptionReplaceApply: (searchValue: String, replacementValue: String) -> Unit = { _, _ -> },
    onCaptionReplaceCancel: () -> Unit = {}
) {
    if (show) {
        var searchValue by remember { mutableStateOf("") }
        var replacementValue by remember { mutableStateOf("") }

        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = "Replace settings:"
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
            WinTextField(
                modifier = Modifier
                    .weight(0.5f),
                fieldModifier = Modifier.fillMaxWidth(),
                text = searchValue,
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search",
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.LightGray
                    )
                },
                onValueChange = {
                    searchValue = it
                    onSearchValueChange(it)
                }
            )

            Spacer(
                modifier = Modifier
                    .width(3.dp)
            )
            WinTextField(
                modifier = Modifier
                    .weight(0.5f),
                fieldModifier = Modifier.fillMaxWidth(),
                text = replacementValue,
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Replacement",
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        color = Color.LightGray
                    )
                },
                onValueChange = {
                    replacementValue = it
                }
            )

            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            TooltipIcon(
                modifier = Modifier
                    .width(32.dp)
                    .padding(2.dp),
                iconResource = "cancel.png",
                tooltip = "Cancel",
                onClick = onCaptionReplaceCancel
            )
            TooltipIcon(
                modifier = Modifier
                    .width(32.dp)
                    .padding(2.dp),
                iconResource = "apply.png",
                tooltip = "Replace in all caption files",
                onClick = {
                    onCaptionReplaceApply(searchValue, replacementValue)
                }
            )
        }
    }
}


@Preview
@Composable
fun CaptionReplaceModeToolbarPanelPreview() {
    MaterialTheme {
        DefaultTheme {
            CaptionReplaceModeToolbarPanel(
            )
        }
    }
}