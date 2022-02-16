package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.R;
import com.ramez.shopp.activities.RegisterLoginActivity;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.databinding.DialogFilterBinding;

public class FilterDialog extends Dialog {


    Activity activity;
    private DataFetcherCallBack dataFetcherCallBack;
    private  DialogFilterBinding binding;
    private int sortByPrice = 0;

    public FilterDialog(Context context,final DataFetcherCallBack dataFetcherCallBack) {
        super(context);

        activity = (Activity) context;
        this.dataFetcherCallBack = dataFetcherCallBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogFilterBinding.inflate(LayoutInflater.from(activity));
        setContentView(binding.getRoot());

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCancelable(true);



        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }

        binding.applyBut.setOnClickListener(view -> {
            if (dataFetcherCallBack != null) {
                dataFetcherCallBack.Result(sortByPrice, Constants.success, true);
            }
            dismiss();


        });


        binding.closeBtn.setOnClickListener(view -> {
         dismiss();

        });

        binding.rbPriceLH.setOnClickListener(v -> {
            sortByPrice=1;
        });


        binding.rbPriceHL.setOnClickListener(v -> {
            sortByPrice=2;
        });

    }

}
