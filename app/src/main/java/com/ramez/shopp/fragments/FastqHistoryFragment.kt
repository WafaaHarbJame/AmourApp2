package com.ramez.shopp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.Models.FastqHistoryModel
import com.ramez.shopp.adapter.FastqHistoryAdapter
import com.ramez.shopp.databinding.FragmentFastqHistoryBinding


class FastqHistoryFragment : FragmentBase() {

    lateinit var binding: FragmentFastqHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFastqHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.layoutManager = LinearLayoutManager(requireActivity())

        binding.dataLY.setOnRefreshListener {

        }

        binding.failGetDataLY.refreshBtn.setOnClickListener {

        }

        initOrdersAdapters()
        
    }

    private fun initOrdersAdapters() {

        val list: MutableList<FastqHistoryModel?> = mutableListOf(
            FastqHistoryModel(),
            FastqHistoryModel(),
            FastqHistoryModel(),
            FastqHistoryModel(),
        )

        val adapter = FastqHistoryAdapter(requireActivity(), binding.rv, list)
        binding.rv.adapter = adapter
    }


}