package com.icepull.app.rgerpofk.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.icepull.app.rgerpofk.presentation.di.icePullModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface IcePullAppsFlyerState {
    data object IcePullDefault : IcePullAppsFlyerState
    data class IcePullSuccess(val icePullData: MutableMap<String, Any>?) :
        IcePullAppsFlyerState

    data object IcePullError : IcePullAppsFlyerState
}

interface IcePullAppsApi {
    @Headers("Content-Type: application/json")
    @GET(ICE_PULL_LIN)
    fun icePullGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val ICE_PULL_APP_DEV = "X3MVC2zN8Dy4vt4rLKGxXX"
private const val ICE_PULL_LIN = "com.icepull.app"

class IcePullApplication : Application() {

    private var icePullIsResumed = false
    private var icePullConversionTimeoutJob: Job? = null
    private var icePullDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        icePullSetDebufLogger(appsflyer)
        icePullMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        icePullExtractDeepMap(p0.deepLink)
                        Log.d(ICE_PULL_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(ICE_PULL_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(ICE_PULL_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            ICE_PULL_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    icePullConversionTimeoutJob?.cancel()
                    Log.d(ICE_PULL_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = icePullGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.icePullGetClient(
                                    devkey = ICE_PULL_APP_DEV,
                                    deviceId = icePullGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(ICE_PULL_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    icePullResume(IcePullAppsFlyerState.IcePullError)
                                } else {
                                    icePullResume(
                                        IcePullAppsFlyerState.IcePullSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(ICE_PULL_MAIN_TAG, "Error: ${d.message}")
                                icePullResume(IcePullAppsFlyerState.IcePullError)
                            }
                        }
                    } else {
                        icePullResume(IcePullAppsFlyerState.IcePullSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    icePullConversionTimeoutJob?.cancel()
                    Log.d(ICE_PULL_MAIN_TAG, "onConversionDataFail: $p0")
                    icePullResume(IcePullAppsFlyerState.IcePullError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(ICE_PULL_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(ICE_PULL_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, ICE_PULL_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(ICE_PULL_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(ICE_PULL_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        icePullStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@IcePullApplication)
            modules(
                listOf(
                    icePullModule
                )
            )
        }
    }

    private fun icePullExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(ICE_PULL_MAIN_TAG, "Extracted DeepLink data: $map")
        icePullDeepLinkData = map
    }

    private fun icePullStartConversionTimeout() {
        icePullConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!icePullIsResumed) {
                Log.d(ICE_PULL_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                icePullResume(IcePullAppsFlyerState.IcePullError)
            }
        }
    }

    private fun icePullResume(state: IcePullAppsFlyerState) {
        icePullConversionTimeoutJob?.cancel()
        if (state is IcePullAppsFlyerState.IcePullSuccess) {
            val convData = state.icePullData ?: mutableMapOf()
            val deepData = icePullDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!icePullIsResumed) {
                icePullIsResumed = true
                icePullConversionFlow.value =
                    IcePullAppsFlyerState.IcePullSuccess(merged)
            }
        } else {
            if (!icePullIsResumed) {
                icePullIsResumed = true
                icePullConversionFlow.value = state
            }
        }
    }

    private fun icePullGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(ICE_PULL_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun icePullSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun icePullMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun icePullGetApi(url: String, client: OkHttpClient?): IcePullAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {

        var icePullInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val icePullConversionFlow: MutableStateFlow<IcePullAppsFlyerState> = MutableStateFlow(
            IcePullAppsFlyerState.IcePullDefault
        )
        var ICE_PULL_FB_LI: String? = null
        const val ICE_PULL_MAIN_TAG = "IcePullMainTag"
    }
}