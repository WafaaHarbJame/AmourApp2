package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.databinding.DialogFilterBinding;

public class SortDialog extends Dialog {


    Activity activity;
    private DataFetcherCallBack dataFetcherCallBack;
    private  DialogFilterBinding binding;
    private int sortType = 0;

    public SortDialog(Context context, final DataFetcherCallBack dataFetcherCallBack) {
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
                dataFetcherCallBack.Result(sortType, Constants.success, true);
            }
            dismiss();


        });


        binding.closeBtn.setOnClickListener(view -> {
         dismiss();

        });



        binding.rbPriceHL.setOnClickListener(v -> {
            sortType =1;
        });

        binding.rbPriceLH.setOnClickListener(v -> {
            sortType =2;
        });


        binding.rbNameHL.setOnClickListener(v -> {
            sortType =3;
        });

        binding.rbNameLH.setOnClickListener(v -> {
            sortType =4;
        });

    }

}
