package com.ramez.shopp.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.GlobalData
import com.ramez.shopp.databinding.DialogFilterBinding

class SortDialog(context: Context?, dataFetcherCallBack: DataFetcherCallBack?) :
        Dialog(context!!) {
    var activity: Activity? = context as Activity?
    var dataFetcherCallBack:DataFetcherCallBack?=dataFetcherCallBack
    private var sortType = 0
    var binding: DialogFilterBinding

    init {
        sortType = GlobalData.sortType
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogFilterBinding.inflate(LayoutInflater.from(activity))
        setContentView(binding.root)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.BOTTOM)
        setCancelable(true)
        checkSort()
        initListeners()


        try {
            if (activity != null && !activity!!.isFinishing) show()
        } catch (e: Exception) {
            dismiss()
        }

    }

    private fun initListeners() {

        binding.applyBut.setOnClickListener {
            dataFetcherCallBack?.Result(sortType, Constants.success, true)
            dismiss()
        }

        binding.closeBtn.setOnClickListener { dismiss() }

        binding.rbSuggestedHL.setOnClickListener { sortType = 0 }

        binding.rbPriceHL.setOnClickListener { sortType = 1 }

        binding.rbPriceLH.setOnClickListener { sortType = 2 }

        binding.rbNameHL.setOnClickListener { sortType = 3 }

        binding.rbNameLH.setOnClickListener { sortType = 4 }

        binding.rbNewest.setOnClickListener { sortType = 5 }

    }

    private fun checkSort() {

        when (sortType) {
            0 -> {
                binding.rbSuggestedHL.isChecked = true
            }
            1 -> {
                binding.rbPriceHL.isChecked = true
            }
            2 -> {
                binding.rbPriceLH.isChecked = true
            }
            3 -> {
                binding.rbNameHL.isChecked = true
            }
            4 -> {
                binding.rbNameLH.isChecked = true
            }
            5 -> {
                binding.rbNewest.isChecked = true
            }
        }
    }
}

