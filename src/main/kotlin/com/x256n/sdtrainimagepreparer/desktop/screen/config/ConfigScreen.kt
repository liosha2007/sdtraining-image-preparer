package com.x256n.sdtrainimagepreparer.desktop.screen.config

import WinButton
import WinTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.sdtrainimagepreparer.desktop.navigation.Destinations
import com.x256n.sdtrainimagepreparer.desktop.navigation.Navigator
import com.x256n.sdtrainimagepreparer.desktop.theme.spaces
import org.slf4j.LoggerFactory

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun ConfigScreen(viewModel: ConfigViewModel, navigator: Navigator<Destinations>) {
    val state by viewModel.state

    val LOG = remember { LoggerFactory.getLogger("ConfigScreen") }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ConfigEvent.ConfigDisplayed)
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
                WinTextField("Config", onValueChange = {

                })
                WinButton(text = "Home", onClick = {
                    navigator.goBack()
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