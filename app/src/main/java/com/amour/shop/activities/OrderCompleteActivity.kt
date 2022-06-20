package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.OrderModel
import com.amour.shop.R
import com.amour.shop.Utils.DateHandler
import com.amour.shop.databinding.ActivityOrderCompleteBinding


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
        getIntentData()
        binding.toolBar.backBtn.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
        intent.putExtra(Constants.TO_FRAG_HOME, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP /*| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK*/)
        startActivity(intent)
    }

    //                    dayName = (DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEEE")).substring(0, 3);
    private fun getIntentData(){
            val bundle = intent.extras
            if (bundle != null) {
                orderModel = bundle.getSerializable(Constants.ORDER_MODEL) as OrderModel?
                orderId = orderModel?.orderCode?:""
                binding.orderIDTv.text = orderId
//                val dayName: String
//                val today = DateHandler.GetDateNowString()
//                dayName = if (today == orderModel?.deliveryDate) {
//                    getString(R.string.today)
//                } else {
//                    if (UtilityApp.getLanguage() == Constants.Arabic)
//                    {
//                        DateHandler.FormatDate4(orderModel?.deliveryDate?:"", "yyyy-MM-dd", "EEEE")
//                    }
//                    else{
//                        DateHandler.FormatDate4(orderModel?.deliveryDate?:"", "yyyy-MM-dd", "EEE")
//
//                    }
//                }
//                binding.deliveryDayTv.text = dayName.plus(" ")
//                binding.deliveryTimeTv.text = orderModel?.deliveryTime.plus(" ")
            }
        }
}