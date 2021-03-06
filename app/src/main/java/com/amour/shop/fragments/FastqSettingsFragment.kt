package com.amour.shop.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.FragmentFastqSettingsBinding


class FastqSettingsFragment : FragmentBase() {

    lateinit var binding: FragmentFastqSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFastqSettingsBinding.inflate(inflater, container, false)

        initData()
        initListeners()

        return binding.root
    }


    fun initListeners() {

        binding.continuousScanSW.setOnClickListener {
            UtilityApp.setContinuousScan(binding.continuousScanSW.isChecked)
        }

        binding.soundSW.setOnClickListener {
            UtilityApp.setScanSound(binding.soundSW.isChecked)
        }

    }


    private fun initData() {

        binding.soundSW.isChecked = UtilityApp.getScanSound()
        binding.continuousScanSW.isChecked = UtilityApp.getContinuousScan()

    }


}