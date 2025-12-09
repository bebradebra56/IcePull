package com.icepull.app.rgerpofk

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication

class IcePullGlobalLayoutUtil {

    private var icePullMChildOfContent: View? = null
    private var icePullUsableHeightPrevious = 0

    fun icePullAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        icePullMChildOfContent = content.getChildAt(0)

        icePullMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val icePullUsableHeightNow = icePullComputeUsableHeight()
        if (icePullUsableHeightNow != icePullUsableHeightPrevious) {
            val icePullUsableHeightSansKeyboard = icePullMChildOfContent?.rootView?.height ?: 0
            val icePullHeightDifference = icePullUsableHeightSansKeyboard - icePullUsableHeightNow

            if (icePullHeightDifference > (icePullUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(IcePullApplication.icePullInputMode)
            } else {
                activity.window.setSoftInputMode(IcePullApplication.icePullInputMode)
            }
//            mChildOfContent?.requestLayout()
            icePullUsableHeightPrevious = icePullUsableHeightNow
        }
    }

    private fun icePullComputeUsableHeight(): Int {
        val r = Rect()
        icePullMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}