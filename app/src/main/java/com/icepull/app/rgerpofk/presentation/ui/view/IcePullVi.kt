package com.icepull.app.rgerpofk.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication

class IcePullVi(
    private val icePullContext: Context,
    private val icePullCallback: IcePullCallBack,
    private val icePullWindow: Window
) : WebView(icePullContext) {
    private var icePullFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    fun icePullSetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.icePullFileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(icePullContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        icePullContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(icePullContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                if (url?.contains("ninecasino") == true) {
                    IcePullApplication.icePullInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "onPageFinished : ${IcePullApplication.icePullInputMode}")
                    icePullWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    IcePullApplication.icePullInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "onPageFinished : ${IcePullApplication.icePullInputMode}")
                    icePullWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                icePullFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                icePullHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun icePullFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun icePullHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = IcePullVi(icePullContext, icePullCallback, icePullWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            icePullCallback.icePullHandleCreateWebWindowRequest(windowWebView)
        }
    }

}