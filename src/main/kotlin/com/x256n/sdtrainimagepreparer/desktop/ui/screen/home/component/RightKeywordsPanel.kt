@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeEvent
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
import kotlin.io.path.ExperimentalPathApi

@Composable
fun RightKeywordsPanel(modifier: Modifier = Modifier, viewModel: HomeViewModel, lazyState: LazyListState) {
    val state by viewModel.state
    Column(
        modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            state = lazyState
        ) {
            items(state.keywordList) { item ->
                var modifier: Modifier = Modifier
                var textColor = Color.Black
                if (item.isAdded) {
                    modifier = Modifier
                        .background(Color.DarkGray)
                    textColor = Color.White
                }
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(HomeEvent.KeywordSelected(item))
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spaces.medium, vertical = MaterialTheme.spaces.small)
                            .fillMaxWidth(),
                        text = item.keyword,
                        color = textColor
                    )
                }
            }
        }
    }
}