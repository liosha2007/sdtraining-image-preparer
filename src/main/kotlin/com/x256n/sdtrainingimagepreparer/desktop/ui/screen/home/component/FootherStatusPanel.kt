@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.Status
import kotlin.io.path.ExperimentalPathApi

@Composable
fun FootherStatusPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val state by viewModel.state
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = MaterialTheme.spaces.medium),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .weight(0.3f)
                .padding(horizontal = 3.dp, vertical = 1.dp)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth(),
                fontSize = MaterialTheme.typography.body2.fontSize,
                text = state.imageDetails,
                color = Color.Black,
                maxLines = 1
            )
        }
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .weight(0.7f)
                .padding(horizontal = MaterialTheme.spaces.medium, vertical = 1.dp)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth(),
                fontSize = MaterialTheme.typography.body2.fontSize,
                text = if (state.status is Status.Error) "Error: " + state.status.text else state.status.text,
                color = if (state.status is Status.Error) Color.Red else Color.Black,
                maxLines = 1
            )
        }
    }
}