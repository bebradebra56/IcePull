package com.icepull.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.icepull.app.rgerpofk.IcePullGlobalLayoutUtil
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication
import com.icepull.app.rgerpofk.presentation.pushhandler.IcePullPushHandler
import com.icepull.app.rgerpofk.icePullSetupSystemBars
import org.koin.android.ext.android.inject

class IcePullActivity : AppCompatActivity() {
    private val icePullPushHandler by inject<IcePullPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        icePullSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_ice_pull)

        val icePullRootView = findViewById<View>(android.R.id.content)
        IcePullGlobalLayoutUtil().icePullAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(icePullRootView) { icePullView, icePullInsets ->
            val icePullSystemBars = icePullInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val icePullDisplayCutout = icePullInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val icePullIme = icePullInsets.getInsets(WindowInsetsCompat.Type.ime())


            val icePullTopPadding = maxOf(icePullSystemBars.top, icePullDisplayCutout.top)
            val icePullLeftPadding = maxOf(icePullSystemBars.left, icePullDisplayCutout.left)
            val icePullRightPadding = maxOf(icePullSystemBars.right, icePullDisplayCutout.right)
            window.setSoftInputMode(IcePullApplication.icePullInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "ADJUST PUN")
                val icePullBottomInset = maxOf(icePullSystemBars.bottom, icePullDisplayCutout.bottom)

                icePullView.setPadding(icePullLeftPadding, icePullTopPadding, icePullRightPadding, 0)

                icePullView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = icePullBottomInset
                }
            } else {
                Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "ADJUST RESIZE")

                val icePullBottomInset = maxOf(icePullSystemBars.bottom, icePullDisplayCutout.bottom, icePullIme.bottom)

                icePullView.setPadding(icePullLeftPadding, icePullTopPadding, icePullRightPadding, 0)

                icePullView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = icePullBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Activity onCreate()")
        icePullPushHandler.icePullHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            icePullSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        icePullSetupSystemBars()
    }
}