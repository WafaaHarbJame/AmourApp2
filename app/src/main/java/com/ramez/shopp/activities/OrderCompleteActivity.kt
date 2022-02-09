package com.ramez.shopp.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.Constants.MAIN_ACTIVITY_CLASS
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.Models.OrderModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.DateHandler
import com.ramez.shopp.databinding.ActivityOrderCompleteBinding


class OrderCompleteActivity : ActivityBase() {
    private lateinit var  binding: ActivityOrderCompleteBinding
    var orderModel: OrderModel? = null
    private var orderId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCompleteBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.order_send)
        intentData
        binding.toolBar.backBtn.setOnClickListener { v -> onBackPressed() }
    }

    override fun onBackPressed() {
        val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
        intent.putExtra(Constants.TO_FRAG_HOME, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP /*| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK*/)
        startActivity(intent)
    }

    //                    dayName = (DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEEE")).substring(0, 3);
    @get:SuppressLint("SetTextI18n")
    private val intentData: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                orderModel = bundle.getSerializable(Constants.ORDER_MODEL) as OrderModel?
                orderId = orderModel?.orderCode?:""
                binding.orderIDTv.text = orderId
                val dayName: String
                val today = DateHandler.GetDateNowString()
                dayName = if (today == orderModel?.deliveryDate) {
                    getString(R.string.today)
                } else {
                    if (UtilityApp.getLanguage() == Constants.Arabic) DateHandler.FormatDate4(
                        orderModel?.deliveryDate, "yyyy-MM-dd", "EEEE"
                    ) else DateHandler.FormatDate4(
                        orderModel?.deliveryDate, "yyyy-MM-dd", "EEE"
                    )
                    //                    dayName = (DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEEE")).substring(0, 3);
                }
                binding.deliveryDayTv.text = dayName + ""
                binding.deliveryTimeTv.text = orderModel?.deliveryTime + ""
            }
        }
}