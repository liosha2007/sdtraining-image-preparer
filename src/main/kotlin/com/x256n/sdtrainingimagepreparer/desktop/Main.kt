package com.x256n.sdtrainingimagepreparer.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.chrynan.navigation.ExperimentalNavigationApi
import com.x256n.sdtrainingimagepreparer.desktop.di.ModulesInjection
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.navigation.NavigationComponent
import com.x256n.sdtrainingimagepreparer.desktop.theme.DefaultTheme
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.dsl.module
import java.awt.Dimension
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ExperimentalMaterialApi
@ExperimentalNavigationApi
@ExperimentalSerializationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() {
    ConfigManager.reloadConfig()

    configureKoin()

    application {
        Window(
            state = WindowState(
                width = 840.dp,
                height = 560.dp,
                position = WindowPosition.Aligned(Alignment.Center)
            ),
            onCloseRequest = ::exitApplication,
            title = "Stable Diffusion training - Image preparer",
            icon = painterResource("icon.ico"),
            resizable = true
        ) {
            this.window.minimumSize = Dimension(640, 480)
            MaterialTheme {
                DefaultTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NavigationComponent()
                    }
                }
            }
        }
    }
}

@ExperimentalSerializationApi
fun configureKoin() {
    startKoin {
        logger(PrintLogger())
        modules(
            module {
                single(createdAtStart = true) {
                    Json {
                        explicitNulls = true
                        prettyPrint = true
                        encodeDefaults = true
                    }
                }
            }
        )
        modules(ModulesInjection.viewmodelBeans)
        modules(ModulesInjection.usecaseBeans)
        modules(ModulesInjection.managerBeans)
        modules(ModulesInjection.repositoryBeans)
        modules(ModulesInjection.otherBeans)
    }
}
