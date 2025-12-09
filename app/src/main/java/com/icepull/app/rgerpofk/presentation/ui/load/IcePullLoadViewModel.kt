package com.icepull.app.rgerpofk.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icepull.app.rgerpofk.data.shar.IcePullSharedPreference
import com.icepull.app.rgerpofk.data.utils.IcePullSystemService
import com.icepull.app.rgerpofk.domain.usecases.IcePullGetAllUseCase
import com.icepull.app.rgerpofk.presentation.app.IcePullAppsFlyerState
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IcePullLoadViewModel(
    private val icePullGetAllUseCase: IcePullGetAllUseCase,
    private val icePullSharedPreference: IcePullSharedPreference,
    private val icePullSystemService: IcePullSystemService
) : ViewModel() {

    private val _icePullHomeScreenState: MutableStateFlow<IcePullHomeScreenState> =
        MutableStateFlow(IcePullHomeScreenState.IcePullLoading)
    val icePullHomeScreenState = _icePullHomeScreenState.asStateFlow()

    private var icePullGetApps = false


    init {
        viewModelScope.launch {
            when (icePullSharedPreference.icePullAppState) {
                0 -> {
                    if (icePullSystemService.icePullIsOnline()) {
                        IcePullApplication.icePullConversionFlow.collect {
                            when(it) {
                                IcePullAppsFlyerState.IcePullDefault -> {}
                                IcePullAppsFlyerState.IcePullError -> {
                                    icePullSharedPreference.icePullAppState = 2
                                    _icePullHomeScreenState.value =
                                        IcePullHomeScreenState.IcePullError
                                    icePullGetApps = true
                                }
                                is IcePullAppsFlyerState.IcePullSuccess -> {
                                    if (!icePullGetApps) {
                                        icePullGetData(it.icePullData)
                                        icePullGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _icePullHomeScreenState.value =
                            IcePullHomeScreenState.IcePullNotInternet
                    }
                }
                1 -> {
                    if (icePullSystemService.icePullIsOnline()) {
                        if (IcePullApplication.ICE_PULL_FB_LI != null) {
                            _icePullHomeScreenState.value =
                                IcePullHomeScreenState.IcePullSuccess(
                                    IcePullApplication.ICE_PULL_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > icePullSharedPreference.icePullExpired) {
                            Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Current time more then expired, repeat request")
                            IcePullApplication.icePullConversionFlow.collect {
                                when(it) {
                                    IcePullAppsFlyerState.IcePullDefault -> {}
                                    IcePullAppsFlyerState.IcePullError -> {
                                        _icePullHomeScreenState.value =
                                            IcePullHomeScreenState.IcePullSuccess(
                                                icePullSharedPreference.icePullSavedUrl
                                            )
                                        icePullGetApps = true
                                    }
                                    is IcePullAppsFlyerState.IcePullSuccess -> {
                                        if (!icePullGetApps) {
                                            icePullGetData(it.icePullData)
                                            icePullGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Current time less then expired, use saved url")
                            _icePullHomeScreenState.value =
                                IcePullHomeScreenState.IcePullSuccess(
                                    icePullSharedPreference.icePullSavedUrl
                                )
                        }
                    } else {
                        _icePullHomeScreenState.value =
                            IcePullHomeScreenState.IcePullNotInternet
                    }
                }
                2 -> {
                    _icePullHomeScreenState.value =
                        IcePullHomeScreenState.IcePullError
                }
            }
        }
    }


    private suspend fun icePullGetData(conversation: MutableMap<String, Any>?) {
        val icePullData = icePullGetAllUseCase.invoke(conversation)
        if (icePullSharedPreference.icePullAppState == 0) {
            if (icePullData == null) {
                icePullSharedPreference.icePullAppState = 2
                _icePullHomeScreenState.value =
                    IcePullHomeScreenState.IcePullError
            } else {
                icePullSharedPreference.icePullAppState = 1
                icePullSharedPreference.apply {
                    icePullExpired = icePullData.icePullExpires
                    icePullSavedUrl = icePullData.icePullUrl
                }
                _icePullHomeScreenState.value =
                    IcePullHomeScreenState.IcePullSuccess(icePullData.icePullUrl)
            }
        } else  {
            if (icePullData == null) {
                _icePullHomeScreenState.value =
                    IcePullHomeScreenState.IcePullSuccess(icePullSharedPreference.icePullSavedUrl)
            } else {
                icePullSharedPreference.apply {
                    icePullExpired = icePullData.icePullExpires
                    icePullSavedUrl = icePullData.icePullUrl
                }
                _icePullHomeScreenState.value =
                    IcePullHomeScreenState.IcePullSuccess(icePullData.icePullUrl)
            }
        }
    }


    sealed class IcePullHomeScreenState {
        data object IcePullLoading : IcePullHomeScreenState()
        data object IcePullError : IcePullHomeScreenState()
        data class IcePullSuccess(val data: String) : IcePullHomeScreenState()
        data object IcePullNotInternet: IcePullHomeScreenState()
    }
}