@file:OptIn(ExperimentalPathApi::class, ExperimentalPathApi::class, ExperimentalPathApi::class,
    ExperimentalPathApi::class
)

package com.x256n.sdtrainingimagepreparer.desktop.di

import com.x256n.sdtrainingimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainingimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainingimagepreparer.desktop.repository.*
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.about.AboutViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.createproject.CreateProjectViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.deletecaptions.DeleteCaptionsConfirmationViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.settings.SettingsViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yescancel.YesCancelViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.dialog.yesnocancel.YesNoCancelViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.HomeViewModel
import com.x256n.sdtrainingimagepreparer.desktop.ui.screen.home.eventhandler.*
import com.x256n.sdtrainingimagepreparer.desktop.usecase.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.io.path.ExperimentalPathApi

object ModulesInjection {
    val viewmodelBeans = module {
        single {
            HomeViewModel(
                applicationEventHandler = get(),
                projectEventHandler = get(),
                imageEventHandler = get(),
                captionEventHandler = get(),
                cropEventHandler = get(),
                replaceEventHandler = get(),
            )
        }
        factoryOf(::AboutViewModel)
        factoryOf(::SettingsViewModel)
        factoryOf(::CreateProjectViewModel)
        factoryOf(::YesCancelViewModel)
        factoryOf(::YesNoCancelViewModel)
        factoryOf(::DeleteCaptionsConfirmationViewModel)
    }
    val eventhandlerBean = module {
        singleOf(::ApplicationHomeEventHandler)
        singleOf(::ProjectHomeEventHandler)
        singleOf(::ImageHomeEventHandler)
        singleOf(::CaptionHomeEventHandler)
        singleOf(::CropHomeEventHandler)
        singleOf(::ReplaceHomeEventHandler)
    }
    val usecaseBeans = module {
        factoryOf(::InitializeProjectUseCase)
        factoryOf(::CheckProjectValidUseCase)
        factoryOf(::IsProjectUseCase)
        factoryOf(::WriteCaptionUseCase)
        factoryOf(::ReadCaptionUseCase)
        factoryOf(::LoadImageModelsUseCase)
        factoryOf(::RemoveIncorrectThumbnailsUseCase)
        factoryOf(::ExtractCaptionKeywordsUseCase)
        factoryOf(::SplitCaptionUseCase)
        factoryOf(::JoinCaptionUseCase)
        factoryOf(::CropResizeImageUseCase)
        factoryOf(::CreateNewAndMergeExistingCaptionsUseCase)
        factoryOf(::DropProjectUseCase)
        factoryOf(::DeleteImageUseCase)
        factoryOf(::CreateCaptionIfNotExistUseCase)
        factoryOf(::DeleteCaptionUseCase)
        factoryOf(::ConvertImageUseCase)
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