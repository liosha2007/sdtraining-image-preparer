@file:OptIn(ExperimentalPathApi::class)

package com.x256n.sdtrainimagepreparer.desktop.di

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.*
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about.AboutViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject.CreateProjectViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.settings.SettingsViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.io.path.ExperimentalPathApi

object ModulesInjection {
    val viewmodelBeans = module {
        single {
            HomeViewModel(
                dispatcherProvider = get(),
                checkProject = get(),
                loadImageModels = get(),
                readCaption = get(),
                removeIncorrectThumbnails = get(),
                extractCaptionKeywords = get(),
                writeCaption = get(),
                joinCaption = get(),
                splitCaption = get(),
                cropResizeImage = get(),
                createNewAndMergeExistingCaptions = get(),
                configManager = get()
            )
        }
        factoryOf(::AboutViewModel)
        factoryOf(::SettingsViewModel)
        factoryOf(::CreateProjectViewModel)
    }
    val usecaseBeans = module {
        factoryOf(::InitializeProjectUseCase)
        factoryOf(::CheckProjectUseCase)
        factoryOf(::WriteCaptionUseCase)
        factoryOf(::ReadCaptionUseCase)
        factoryOf(::LoadImageModelsUseCase)
        factoryOf(::RemoveIncorrectThumbnailsUseCase)
        factoryOf(::ExtractCaptionKeywordsUseCase)
        factoryOf(::SplitCaptionUseCase)
        factoryOf(::JoinCaptionUseCase)
        factoryOf(::CropResizeImageUseCase)
        factoryOf(::CreateNewAndMergeExistingCaptionsUseCase)
    }
    val managerBeans = module {
        singleOf(::ConfigManager)
    }
    val repositoryBeans = module {
        factoryOf(::ProjectConfigRepositoryImpl) { bind<ProjectConfigRepository>() }
        factoryOf(::CaptionRepositoryImpl) { bind<CaptionRepository>() }
        factoryOf(::ThumbnailsRepositoryImpl) { bind<ThumbnailsRepository>() }
    }
    val otherBeans = module {
        factoryOf(::StandardDispatcherProvider) { bind<DispatcherProvider>() }
    }
}