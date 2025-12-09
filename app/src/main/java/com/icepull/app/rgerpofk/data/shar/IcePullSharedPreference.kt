package com.icepull.app.rgerpofk.data.shar

import android.content.Context
import androidx.core.content.edit

class IcePullSharedPreference(context: Context) {
    private val icePullPrefs = context.getSharedPreferences("icePullSharedPrefsAb", Context.MODE_PRIVATE)

    var icePullSavedUrl: String
        get() = icePullPrefs.getString(ICE_PULL_SAVED_URL, "") ?: ""
        set(value) = icePullPrefs.edit { putString(ICE_PULL_SAVED_URL, value) }

    var icePullExpired : Long
        get() = icePullPrefs.getLong(ICE_PULL_EXPIRED, 0L)
        set(value) = icePullPrefs.edit { putLong(ICE_PULL_EXPIRED, value) }

    var icePullAppState: Int
        get() = icePullPrefs.getInt(ICE_PULL_APPLICATION_STATE, 0)
        set(value) = icePullPrefs.edit { putInt(ICE_PULL_APPLICATION_STATE, value) }

    var icePullNotificationRequest: Long
        get() = icePullPrefs.getLong(ICE_PULL_NOTIFICAITON_REQUEST, 0L)
        set(value) = icePullPrefs.edit { putLong(ICE_PULL_NOTIFICAITON_REQUEST, value) }

    var icePullNotificationRequestedBefore: Boolean
        get() = icePullPrefs.getBoolean(ICE_PULL_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = icePullPrefs.edit { putBoolean(
            ICE_PULL_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val ICE_PULL_SAVED_URL = "icePullSavedUrl"
        private const val ICE_PULL_EXPIRED = "icePullExpired"
        private const val ICE_PULL_APPLICATION_STATE = "icePullApplicationState"
        private const val ICE_PULL_NOTIFICAITON_REQUEST = "icePullNotificationRequest"
        private const val ICE_PULL_NOTIFICATION_REQUEST_BEFORE = "icePullNotificationRequestedBefore"
    }
}