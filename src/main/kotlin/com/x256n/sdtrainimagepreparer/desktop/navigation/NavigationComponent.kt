package com.x256n.sdtrainimagepreparer.desktop.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.FrameWindowScope
import com.chrynan.navigation.ExperimentalNavigationApi
import com.chrynan.navigation.compose.ComposeNavigatorByKey
import com.chrynan.navigation.compose.NavContainer
import com.chrynan.navigation.compose.rememberNavigatorByKey
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about.AboutDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject.CreateProjectDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.settings.SettingsDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.yescancel.YesCancelDialog
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeScreen
import org.slf4j.LoggerFactory
import kotlin.io.path.ExperimentalPathApi

typealias Navigator<T> = ComposeNavigatorByKey<T, Destinations>

@ExperimentalPathApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalNavigationApi
@Composable
fun FrameWindowScope.NavigationComponent() {
    val navigator = rememberNavigatorByKey<Destinations, Destinations>(initialContext = Destinations.Home()) { dest ->

        HomeScreen(navigator, dest)

        when (dest) {
            is Destinations.CreateProject -> {
                CreateProjectDialog(navigator)
            }
            is Destinations.Settings -> {
                SettingsDialog(navigator)
            }
            is Destinations.YesCancel -> {
                YesCancelDialog(navigator, dest)
            }
            is Destinations.About -> {
                AboutDialog(navigator)
            }
            else -> {
                LoggerFactory.getLogger("NavigationComponent").warn("Unknown destination: $dest")
            }
        }
    }
    NavContainer(navigator)
}