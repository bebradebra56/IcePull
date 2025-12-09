package com.icepull.app.rgerpofk.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication

class IcePullPushHandler {
    fun icePullHandlePush(extras: Bundle?) {
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = icePullBundleToMap(extras)
            Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    IcePullApplication.ICE_PULL_FB_LI = map["url"]
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Push data no!")
        }
    }

    private fun icePullBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}