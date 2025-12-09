package com.icepull.app.rgerpofk.data.repo

import android.util.Log
import com.icepull.app.rgerpofk.domain.model.IcePullEntity
import com.icepull.app.rgerpofk.domain.model.IcePullParam
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication.Companion.ICE_PULL_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IcePullApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun icePullGetClient(
        @Body jsonString: JsonObject,
    ): Call<IcePullEntity>
}


private const val ICE_PULL_MAIN = "https://iccepull.com/"
class IcePullRepository {

    suspend fun icePullGetClient(
        icePullParam: IcePullParam,
        icePullConversion: MutableMap<String, Any>?
    ): IcePullEntity? {
        val gson = Gson()
        val api = icePullGetApi(ICE_PULL_MAIN, null)

        val icePullJsonObject = gson.toJsonTree(icePullParam).asJsonObject
        icePullConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            icePullJsonObject.add(key, element)
        }
        return try {
            val icePullRequest: Call<IcePullEntity> = api.icePullGetClient(
                jsonString = icePullJsonObject,
            )
            val icePullResult = icePullRequest.awaitResponse()
            Log.d(ICE_PULL_MAIN_TAG, "Retrofit: Result code: ${icePullResult.code()}")
            if (icePullResult.code() == 200) {
                Log.d(ICE_PULL_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ICE_PULL_MAIN_TAG, "Retrofit: Code = ${icePullResult.code()}")
                Log.d(ICE_PULL_MAIN_TAG, "Retrofit: ${icePullResult.body()}")
                icePullResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ICE_PULL_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ICE_PULL_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun icePullGetApi(url: String, client: OkHttpClient?) : IcePullApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
