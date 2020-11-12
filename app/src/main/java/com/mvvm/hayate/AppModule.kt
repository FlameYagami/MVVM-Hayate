package com.mvvm.hayate

import com.mvvm.component.uc.dialog.DialogToastVm
import com.mvvm.hayate.model.repository.LoginRepository
import com.mvvm.hayate.ui.login.LoginVm
import com.mvvm.hayate.ui.main.FirstVm
import com.mvvm.hayate.ui.main.MainVm
import com.mvvm.hayate.ui.main.ProfileVm
import com.mvvm.hayate.ui.main.SecondVm
import com.mvvm.hayate.ui.profile.AvatarVm
import com.mvvm.hayate.ui.profile.FeedbackVm
import com.mvvm.hayate.ui.profile.PasswordModifyVm
import com.mvvm.hayate.ui.profile.ProfileDetailVm
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginVm(get()) }
    viewModel { AvatarVm() }
    viewModel { FeedbackVm() }
    viewModel { PasswordModifyVm() }
    viewModel { ProfileDetailVm() }
    viewModel { MainVm() }
    viewModel { FirstVm() }
    viewModel { SecondVm() }
    viewModel { ProfileVm() }

    viewModel { DialogToastVm() }
}

val repositoryModule = module {
    single { LoginRepository() }
}

val appModule = listOf(viewModelModule, repositoryModule)