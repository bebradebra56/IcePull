package com.icepull.app.rgerpofk.domain.model

import com.google.gson.annotations.SerializedName


data class IcePullEntity (
    @SerializedName("ok")
    val icePullOk: String,
    @SerializedName("url")
    val icePullUrl: String,
    @SerializedName("expires")
    val icePullExpires: Long,
)