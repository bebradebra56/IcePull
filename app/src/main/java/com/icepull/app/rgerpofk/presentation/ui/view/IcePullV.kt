package com.icepull.app.rgerpofk.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication
import com.icepull.app.rgerpofk.presentation.ui.load.IcePullLoadFragment
import org.koin.android.ext.android.inject

class IcePullV : Fragment(){

    private lateinit var icePullPhoto: Uri
    private var icePullFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val icePullTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        icePullFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        icePullFilePathFromChrome = null
    }

    private val icePullTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            icePullFilePathFromChrome?.onReceiveValue(arrayOf(icePullPhoto))
            icePullFilePathFromChrome = null
        } else {
            icePullFilePathFromChrome?.onReceiveValue(null)
            icePullFilePathFromChrome = null
        }
    }

    private val icePullDataStore by activityViewModels<IcePullDataStore>()


    private val icePullViFun by inject<IcePullViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (icePullDataStore.icePullView.canGoBack()) {
                        icePullDataStore.icePullView.goBack()
                        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "WebView can go back")
                    } else if (icePullDataStore.icePullViList.size > 1) {
                        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "WebView can`t go back")
                        icePullDataStore.icePullViList.removeAt(icePullDataStore.icePullViList.lastIndex)
                        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "WebView list size ${icePullDataStore.icePullViList.size}")
                        icePullDataStore.icePullView.destroy()
                        val previousWebView = icePullDataStore.icePullViList.last()
                        icePullAttachWebViewToContainer(previousWebView)
                        icePullDataStore.icePullView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (icePullDataStore.icePullIsFirstCreate) {
            icePullDataStore.icePullIsFirstCreate = false
            icePullDataStore.icePullContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return icePullDataStore.icePullContainerView
        } else {
            return icePullDataStore.icePullContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "onViewCreated")
        if (icePullDataStore.icePullViList.isEmpty()) {
            icePullDataStore.icePullView = IcePullVi(requireContext(), object :
                IcePullCallBack {
                override fun icePullHandleCreateWebWindowRequest(icePullVi: IcePullVi) {
                    icePullDataStore.icePullViList.add(icePullVi)
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "WebView list size = ${icePullDataStore.icePullViList.size}")
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "CreateWebWindowRequest")
                    icePullDataStore.icePullView = icePullVi
                    icePullVi.icePullSetFileChooserHandler { callback ->
                        icePullHandleFileChooser(callback)
                    }
                    icePullAttachWebViewToContainer(icePullVi)
                }

            }, icePullWindow = requireActivity().window).apply {
                icePullSetFileChooserHandler { callback ->
                    icePullHandleFileChooser(callback)
                }
            }
            icePullDataStore.icePullView.icePullFLoad(arguments?.getString(
                IcePullLoadFragment.ICE_PULL_D) ?: "")
//            ejvview.fLoad("www.google.com")
            icePullDataStore.icePullViList.add(icePullDataStore.icePullView)
            icePullAttachWebViewToContainer(icePullDataStore.icePullView)
        } else {
            icePullDataStore.icePullViList.forEach { webView ->
                webView.icePullSetFileChooserHandler { callback ->
                    icePullHandleFileChooser(callback)
                }
            }
            icePullDataStore.icePullView = icePullDataStore.icePullViList.last()

            icePullAttachWebViewToContainer(icePullDataStore.icePullView)
        }
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "WebView list size = ${icePullDataStore.icePullViList.size}")
    }

    private fun icePullHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        icePullFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Launching file picker")
                    icePullTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Launching camera")
                    icePullPhoto = icePullViFun.icePullSavePhoto()
                    icePullTakePhoto.launch(icePullPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                icePullFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun icePullAttachWebViewToContainer(w: IcePullVi) {
        icePullDataStore.icePullContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            icePullDataStore.icePullContainerView.removeAllViews()
            icePullDataStore.icePullContainerView.addView(w)
        }
    }


}