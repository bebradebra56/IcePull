package com.icepull.app.rgerpofk.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.icepull.app.MainActivity
import com.icepull.app.R
import com.icepull.app.databinding.FragmentLoadIcePullBinding
import com.icepull.app.rgerpofk.data.shar.IcePullSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class IcePullLoadFragment : Fragment(R.layout.fragment_load_ice_pull) {
    private lateinit var icePullLoadBinding: FragmentLoadIcePullBinding

    private val icePullLoadViewModel by viewModel<IcePullLoadViewModel>()

    private val icePullSharedPreference by inject<IcePullSharedPreference>()

    private var icePullUrl = ""

    private val icePullRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            icePullNavigateToSuccess(icePullUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                icePullSharedPreference.icePullNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                icePullNavigateToSuccess(icePullUrl)
            } else {
                icePullNavigateToSuccess(icePullUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        icePullLoadBinding = FragmentLoadIcePullBinding.bind(view)

        icePullLoadBinding.icePullGrandButton.setOnClickListener {
            val icePullPermission = Manifest.permission.POST_NOTIFICATIONS
            icePullRequestNotificationPermission.launch(icePullPermission)
            icePullSharedPreference.icePullNotificationRequestedBefore = true
        }

        icePullLoadBinding.icePullSkipButton.setOnClickListener {
            icePullSharedPreference.icePullNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            icePullNavigateToSuccess(icePullUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                icePullLoadViewModel.icePullHomeScreenState.collect {
                    when (it) {
                        is IcePullLoadViewModel.IcePullHomeScreenState.IcePullLoading -> {

                        }

                        is IcePullLoadViewModel.IcePullHomeScreenState.IcePullError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is IcePullLoadViewModel.IcePullHomeScreenState.IcePullSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val icePullPermission = Manifest.permission.POST_NOTIFICATIONS
                                val icePullPermissionRequestedBefore = icePullSharedPreference.icePullNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), icePullPermission) == PackageManager.PERMISSION_GRANTED) {
                                    icePullNavigateToSuccess(it.data)
                                } else if (!icePullPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > icePullSharedPreference.icePullNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    icePullLoadBinding.icePullNotiGroup.visibility = View.VISIBLE
                                    icePullLoadBinding.icePullLoadingGroup.visibility = View.GONE
                                    icePullUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(icePullPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > icePullSharedPreference.icePullNotificationRequest) {
                                        icePullLoadBinding.icePullNotiGroup.visibility = View.VISIBLE
                                        icePullLoadBinding.icePullLoadingGroup.visibility = View.GONE
                                        icePullUrl = it.data
                                    } else {
                                        icePullNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    icePullNavigateToSuccess(it.data)
                                }
                            } else {
                                icePullNavigateToSuccess(it.data)
                            }
                        }

                        IcePullLoadViewModel.IcePullHomeScreenState.IcePullNotInternet -> {
                            icePullLoadBinding.icePullStateGroup.visibility = View.VISIBLE
                            icePullLoadBinding.icePullLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun icePullNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_icePullLoadFragment_to_icePullV,
            bundleOf(ICE_PULL_D to data)
        )
    }

    companion object {
        const val ICE_PULL_D = "icePullData"
    }
}