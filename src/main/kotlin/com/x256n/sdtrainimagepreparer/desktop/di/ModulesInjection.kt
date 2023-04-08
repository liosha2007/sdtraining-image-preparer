package com.x256n.sdtrainimagepreparer.desktop.di

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.ProjectConfigRepositoryImpl
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.about.AboutViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.createproject.CreateProjectViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.dialog.settings.SettingsViewModel
import com.x256n.sdtrainimagepreparer.desktop.ui.screen.home.HomeViewModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.CheckProjectUseCase
import com.x256n.sdtrainimagepreparer.desktop.usecase.InitializeProjectUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object ModulesInjection {
    val viewmodelBeans = module {
        single {
            HomeViewModel(
                dispatcherProvider = get(),
                checkProject = get()
            )
        }
        factoryOf(::AboutViewModel)
        factoryOf(::SettingsViewModel)
        factoryOf(::CreateProjectViewModel)
    }
    val usecaseBeans = module {
        factoryOf(::InitializeProjectUseCase)
        factoryOf(::CheckProjectUseCase)

    }
    val managerBeans = module {
        singleOf(::ConfigManager)
    }
    val repositoryBeans = module {
        factoryOf(::ProjectConfigRepositoryImpl) { bind<ProjectConfigRepository>() }
    }
    val otherBeans = module {
        factoryOf(::StandardDispatcherProvider) { bind<DispatcherProvider>() }
    }
}