package com.x256n.sdtrainimagepreparer.desktop.screen.home

import WinButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.goTo
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun HomeScreen(viewModel: HomeViewModel, navigator: Navigator<Destinations>) {
//    val LOG = remember { LoggerFactory.getLogger("HomeScreen") }
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.HomeDisplayed)
    }

    if (!state.isLoading) {
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spaces.small)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spaces.extraSmall)
            ) {
                WinButton(text = "Config", onClick = {
                    navigator.goTo(Destinations.Config())
                })
                Spacer(
                    modifier = Modifier
                        .height(MaterialTheme.spaces.medium)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}