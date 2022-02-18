package com.ramez.shopp.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.PaymentModel
import com.ramez.shopp.Models.PaymentResultModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.PaymentAdapter
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.GlobalData
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.databinding.DialogPaymentBinding


class PaymentDialog(context: Context?, dataFetcherCallBack: DataFetcherCallBack?) :
        Dialog(context!!) {
    var storeId = 0
    var selectedPaymentMethod: PaymentModel? = null

    var activity: Activity? = context as Activity?
    private val dataFetcherCallBack: DataFetcherCallBack? = dataFetcherCallBack
    private val binding: DialogPaymentBinding
    var paymentList: ArrayList<PaymentModel>? = null
    var localModel: LocalModel? = null

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogPaymentBinding.inflate(LayoutInflater.from(activity))
        setContentView(binding.root)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.BOTTOM)
        setCancelable(true)
        paymentList = ArrayList()


        val payLinearLayoutManager = LinearLayoutManager(activity)
        binding.paymentRv.layoutManager = payLinearLayoutManager
        binding.paymentRv.setHasFixedSize(true)
        binding.paymentRv.animation = null
        binding.paymentRv.layoutManager = payLinearLayoutManager

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        getPayments()
        try {
            if (activity != null && !activity!!.isFinishing) show()
        } catch (e: Exception) {
            dismiss()
        }
        binding.applyBut.setOnClickListener {
            if (selectedPaymentMethod != null) {
                dataFetcherCallBack?.Result(selectedPaymentMethod, Constants.success, true)
                dismiss()
            } else {
                GlobalData.Toast(activity, activity?.getString(R.string.please_select_payment_method))


            }

        }
        binding.closeBtn.setOnClickListener { dismiss() }
    }


    private fun getPayments() {
        if (UtilityApp.getPaymentsData() != null && UtilityApp.getPaymentsData().size > 0) {
            paymentList = UtilityApp.getPaymentsData()
            initPaymentAdapter()
        } else {
            getPaymentMethod(storeId)
        }


    }

    private fun getPaymentMethod(storeId: Int) {
        paymentList?.clear()
        binding.loadingLYPay.visibility = View.VISIBLE
        binding.paymentRv.visibility = View.GONE
        binding.applyBut.visibility = View.GONE
        DataFeacher(
            false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    binding.loadingLYPay.visibility = View.GONE
                    binding.paymentRv.visibility = View.VISIBLE
                    binding.applyBut.visibility = View.VISIBLE

                    var message = activity?.getString(R.string.fail_to_get_data)
                    val result = obj as PaymentResultModel?
                    if (func == Constants.ERROR) {
                        if (result != null && result.message != null) {
                            message = result.message
                        }
                        GlobalData.Toast(activity, message)
                    } else if (func == Constants.FAIL) {
                        GlobalData.Toast(activity, message)
                    } else if (func == Constants.NO_CONNECTION) {
                        message = activity?.getString(R.string.no_internet_connection)
                        GlobalData.Toast(activity, message)
                    } else {
                        if (IsSuccess) {
                            if (result?.data != null && result.data.size > 0) {
                                paymentList = result.data
                                initPaymentAdapter()

                            }
                        }
                    }

                }
            }
        ).getPaymentMethod(storeId)
    }

    private fun initPaymentAdapter() {

        for (i in paymentList!!.indices) {

            val paymentModel = paymentList!![i]

            when (paymentModel.id) {
                1 -> {
                    paymentModel.image = R.drawable.cash
                }

                3 -> {
                    paymentModel.image = R.drawable.benefit
                }
                4 -> {
                    paymentModel.image = R.drawable.card
                }
                5 -> {
                    paymentModel.image = R.drawable.benefit_web
                }

                6 -> {
                    paymentModel.image = R.drawable.benefit
                }

                7 -> {
                    paymentModel.image = R.drawable.benefit_web
                }
            }

        }
        Log.i(javaClass.name, "Log paymentList ${paymentList?.size}")

        UtilityApp.setPaymentsData(paymentList)

        val paymentAdapter = PaymentAdapter(
            activity, paymentList
        ) { obj: Any?, func: String?, IsSuccess: Boolean ->
            selectedPaymentMethod = obj as PaymentModel?
            Log.i(
                javaClass.simpleName,
                "Log selectedPaymentMethod " + selectedPaymentMethod?.shortname
            )

        }
        binding.paymentRv.adapter = paymentAdapter
    }

}
