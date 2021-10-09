package com.ramez.shopp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramez.shopp.databinding.FragmentCategoryBinding
import com.ramez.shopp.databinding.FragmentFastqSettingsBinding


class FastqSettingsFragment : FragmentBase() {

    lateinit var binding: FragmentFastqSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFastqSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}