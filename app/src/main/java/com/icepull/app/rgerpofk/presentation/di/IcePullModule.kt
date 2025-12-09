package com.icepull.app.rgerpofk.presentation.di

import com.icepull.app.rgerpofk.data.repo.IcePullRepository
import com.icepull.app.rgerpofk.data.shar.IcePullSharedPreference
import com.icepull.app.rgerpofk.data.utils.IcePullPushToken
import com.icepull.app.rgerpofk.data.utils.IcePullSystemService
import com.icepull.app.rgerpofk.domain.usecases.IcePullGetAllUseCase
import com.icepull.app.rgerpofk.presentation.pushhandler.IcePullPushHandler
import com.icepull.app.rgerpofk.presentation.ui.load.IcePullLoadViewModel
import com.icepull.app.rgerpofk.presentation.ui.view.IcePullViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val icePullModule = module {
    factory {
        IcePullPushHandler()
    }
    single {
        IcePullRepository()
    }
    single {
        IcePullSharedPreference(get())
    }
    factory {
        IcePullPushToken()
    }
    factory {
        IcePullSystemService(get())
    }
    factory {
        IcePullGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        IcePullViFun(get())
    }
    viewModel {
        IcePullLoadViewModel(get(), get(), get())
    }
}