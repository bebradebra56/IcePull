package com.icepull.app.rgerpofk.domain.usecases

import android.util.Log
import com.icepull.app.rgerpofk.data.repo.IcePullRepository
import com.icepull.app.rgerpofk.data.utils.IcePullPushToken
import com.icepull.app.rgerpofk.data.utils.IcePullSystemService
import com.icepull.app.rgerpofk.domain.model.IcePullEntity
import com.icepull.app.rgerpofk.domain.model.IcePullParam
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication

class IcePullGetAllUseCase(
    private val icePullRepository: IcePullRepository,
    private val icePullSystemService: IcePullSystemService,
    private val icePullPushToken: IcePullPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : IcePullEntity?{
        val params = IcePullParam(
            icePullLocale = icePullSystemService.icePullGetLocale(),
            icePullPushToken = icePullPushToken.icePullGetToken(),
            icePullAfId = icePullSystemService.icePullGetAppsflyerId()
        )
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Params for request: $params")
        return icePullRepository.icePullGetClient(params, conversion)
    }



}