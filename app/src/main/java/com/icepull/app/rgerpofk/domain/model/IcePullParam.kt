package com.icepull.app.rgerpofk.domain.model

import com.google.gson.annotations.SerializedName


private const val ICE_PULL_A = "com.icepull.app"
private const val ICE_PULL_B = "icepull-b1e01"
data class IcePullParam (
    @SerializedName("af_id")
    val icePullAfId: String,
    @SerializedName("bundle_id")
    val icePullBundleId: String = ICE_PULL_A,
    @SerializedName("os")
    val icePullOs: String = "Android",
    @SerializedName("store_id")
    val icePullStoreId: String = ICE_PULL_A,
    @SerializedName("locale")
    val icePullLocale: String,
    @SerializedName("push_token")
    val icePullPushToken: String,
    @SerializedName("firebase_project_id")
    val icePullFirebaseProjectId: String = ICE_PULL_B,

    )