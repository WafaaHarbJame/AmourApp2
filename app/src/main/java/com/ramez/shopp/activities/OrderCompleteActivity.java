package com.ramez.shopp.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.OrderModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.DateHandler;
import com.ramez.shopp.databinding.ActivityOrderCompleteBinding;

public class OrderCompleteActivity extends ActivityBase {
    ActivityOrderCompleteBinding binding;
    OrderModel orderModel;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderCompleteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.order_send);

        getIntentData();

        binding.toolBar.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getActiviy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
        intent.putExtra(Constants.TO_FRAG_HOME, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP /*| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK*/);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderModel = (OrderModel) bundle.getSerializable(Constants.ORDER_MODEL);
            orderId = orderModel.getOrderCode();
            binding.orderIDTv.setText(orderId);
            String dayName;

            String today = DateHandler.GetDateNowString();

            if (today.equals(orderModel.getDeliveryDate())) {
                dayName = getString(R.string.today);
            } else {
                if (UtilityApp.getLanguage().equals(Constants.Arabic))
                    dayName = DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEEE");
                else
                    dayName = (DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEE"));
//                    dayName = (DateHandler.FormatDate4(orderModel.getDeliveryDate(), "yyyy-MM-dd", "EEEE")).substring(0, 3);
            }


            binding.deliveryDayTv.setText(dayName + "");
            binding.deliveryTimeTv.setText(orderModel.getDeliveryTime() + "");
        }
    }


}