package com.icepull.app.rgerpofk.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class IcePullDataStore : ViewModel(){
    val icePullViList: MutableList<IcePullVi> = mutableListOf()
    var icePullIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var icePullContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var icePullView: IcePullVi

}