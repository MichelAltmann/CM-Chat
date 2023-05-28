package com.cmchat.application

import com.cmchat.LoginViewModel
import com.cmchat.retrofit.Repository
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.retrofit.RetrofitInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val modelModule : Module = module {
    viewModel { LoginViewModel(get(), get()) }
}

val dataModule = module {
    single { RetrofitInitializer.create(androidContext())}
}

val retrofitModule : Module = module {
    single<RepositoryInterface> {Repository(get())}
}

val applicationModule : Module = module {
    single {androidContext() as Application}
}

val appModules : List<Module> = listOf(retrofitModule, dataModule, modelModule, applicationModule)