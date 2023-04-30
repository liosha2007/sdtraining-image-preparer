@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.component

import WinTextField
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlin.io.path.ExperimentalPathApi

@Composable
fun BottomCaptionPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val state by viewModel.state
    Row(
        modifier = modifier
    ) {
        WinTextField(
            modifier = Modifier
                .fillMaxSize(),
            fieldModifier = Modifier
                .fillMaxSize(),
            text = state.captionContent,
            onValueChange = {
                viewModel.sendEvent(HomeEvent.CaptionContentChanged(it))
            }
        )
    }
}