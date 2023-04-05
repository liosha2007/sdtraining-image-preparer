package com.x256n.sdtrainimagepreparer.desktop.di

import com.x256n.sdtrainimagepreparer.desktop.common.DispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.common.StandardDispatcherProvider
import com.x256n.sdtrainimagepreparer.desktop.manager.ConfigManager
import com.x256n.sdtrainimagepreparer.desktop.repository.SampleModelRepository
import com.x256n.sdtrainimagepreparer.desktop.repository.SampleModelRepositoryImpl
import com.x256n.sdtrainimagepreparer.desktop.screen.config.ConfigViewModel
import com.x256n.sdtrainimagepreparer.desktop.screen.home.HomeViewModel
import com.x256n.sdtrainimagepreparer.desktop.usecase.DoSampleModelUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object ModulesInjection {
    val viewmodelBeans = module {
        single {
            HomeViewModel(
                dispatcherProvider = get(),
                doSampleModel = get()
            )
        }
        singleOf(::ConfigViewModel)
    }
    val usecaseBeans = module {
        singleOf(::DoSampleModelUseCase)

    }
    val managerBeans = module {
        singleOf(::ConfigManager)
    }
    val repositoryBeans = module {
        singleOf(::SampleModelRepositoryImpl) { bind<SampleModelRepository>() }
    }
    val otherBeans = module {
        singleOf(::StandardDispatcherProvider) { bind<DispatcherProvider>() }
    }
}