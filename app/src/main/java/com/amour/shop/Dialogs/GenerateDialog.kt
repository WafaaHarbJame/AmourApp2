package com.amour.shop.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Models.GeneralModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.Toast
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.classes.UtilityApp

import com.amour.shop.R
import com.amour.shop.databinding.DialogGenerateCouponsBinding

class GenerateDialog(
    context: Context?,
    userId: Int,
    totalPoints: Double,
    minimumPoints: Int,
    callBack: DataFetcherCallBack
) :
        Dialog(context!!) {
    var activity: Activity? = context as Activity?
    var localModel: LocalModel =  UtilityApp.getLocalData()
    var countryId: Int = localModel.countryId
    var userId: Int = userId
    var count: Int
    var total: Double = totalPoints
    var dataFetcherCallBack: DataFetcherCallBack = callBack
    private val binding: DialogGenerateCouponsBinding
    private fun sendGenerateCoupon(points: Int) {
        progressDialog(activity, R.string.Generate_Coupons, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                if (IsSuccess) {
                    val result = obj as GeneralModel?
                    if (result != null && result.isSuccessful) {
                        Toast(activity!!.applicationContext, R.string.success_generate_coupon)
                        dismiss()
                        dataFetcherCallBack.Result("", Constants.success, true)
                    } else {
                        var message: String? = activity!!.getString(R.string.fail_generate_coupon)
                        if (result != null && result.message != null && !result.message.isEmpty()) {
                            message = result.message
                        }
                        Toast(activity, message)
                    }
                } else {
                    val message = activity!!.getString(R.string.fail_generate_coupon)
                    Toast(activity, message)
                }            }

        }).generateCoupon(userId, points)
    }

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGenerateCouponsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setCancelable(true)
        count = if (total < minimumPoints) total.toInt() else minimumPoints
        binding.minPointTv.text = minimumPoints.toString()
        binding.totalPointTv.text = total.toString()
        binding.countTV.text = count.toString()
        try {
            if (activity != null && !activity!!.isFinishing) show()
        } catch (e: Exception) {
            dismiss()
        }
        binding.plusBtn.setOnClickListener(View.OnClickListener { //                int count = Integer.parseInt(binding.countTV.getText().toString());
            if (count + minimumPoints <= totalPoints.toInt()) {
                count += minimumPoints
                binding.countTV.text = count.toString()
            } else {
                Toast(activity!!, R.string.you_reach_max_point)
            }
        })
        binding.minusBtn.setOnClickListener { v ->
//            int count = Integer.parseInt(binding.countTV.getText().toString());
            if (count - minimumPoints >= minimumPoints) {
                count -= minimumPoints
                binding.countTV.setText(count.toString())
            } else {
                val message =
                    activity!!.getString(R.string.minimum_points_needed) + " " + minimumPoints
                Toast(activity, message)
            }
        }
        binding.generateBut.setOnClickListener { v ->
            if (count < minimumPoints) {
                val message =
                    activity!!.getString(R.string.minimum_points_needed) + " " + minimumPoints
                Toast(activity, message)
                return@setOnClickListener
            }
            sendGenerateCoupon(count)
        }
    }
}
