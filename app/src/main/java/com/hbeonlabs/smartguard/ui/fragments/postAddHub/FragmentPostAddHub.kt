package com.hbeonlabs.smartguard.ui.fragments.postAddHub

import android.view.View
import androidx.navigation.fragment.findNavController
import com.hbeonlabs.smartguard.R
import com.hbeonlabs.smartguard.base.BaseFragment
import com.hbeonlabs.smartguard.databinding.FragmentAddHubPostVerificationBinding
import com.hbeonlabs.smartguard.ui.activities.MainActivity

import org.koin.android.ext.android.inject


class FragmentPostAddHub:BaseFragment<PostAddHubViewModel,FragmentAddHubPostVerificationBinding>() {

    private  val postAddHubViewModel: PostAddHubViewModel by inject()
    override fun getViewModel(): PostAddHubViewModel {
            return postAddHubViewModel
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_add_hub_post_verification
    }

    override fun initView() {
        super.initView()

        (requireActivity() as MainActivity).binding.toolbarIconEnd.setImageDrawable(null)
        (requireActivity() as MainActivity).binding.toolbarIconStart.setOnClickListener {

        }

        binding.btnContinueToAddSensors.setOnClickListener {
            findNavController().navigate(FragmentPostAddHubDirections.actionFragmentPostAddHubToFragmentSelectAHub())
        }
    }



}