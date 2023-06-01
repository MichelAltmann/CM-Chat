package com.cmchat.application

import com.cmchat.ui.login.LoginViewModel
import com.cmchat.retrofit.Repository
import com.cmchat.retrofit.RepositoryInterface
import com.cmchat.retrofit.RetrofitInitializer
import com.cmchat.ui.login.signup.SignupViewModel
import com.cmchat.ui.main.home.HomeViewModel
import com.cmchat.ui.main.profile.EditProfileViewModel
import com.cmchat.ui.main.profile.ProfileViewModel
import com.cmchat.ui.popups.AddFriendPopupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val modelModule : Module = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SignupViewModel(get()) }
    viewModel {ProfileViewModel(get(), get())}
    viewModel { EditProfileViewModel(get(), get())}
    viewModel { AddFriendPopupViewModel(get(), get())}
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