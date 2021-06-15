package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.R;

public class GenerateDialog extends Dialog {


    Activity activity;
    ConstraintLayout parentLy;
    TextView countTV,minPointTv,totalPointTv;
    ImageView minusBtn,plusBtn;
    LocalModel localModel;
    int countryId;
    double total;


    public GenerateDialog(Context context,double totalPoints,int minimumPoints,int title, int message, int okStr, int cancelStr, final ConfirmDialog.Click okCall, final ConfirmDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;
        localModel=UtilityApp.getLocalData();
        countryId=localModel.getCountryId();
        total=totalPoints;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_generate_coupons);
        getWindow().setDimAmount(0);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);

        parentLy = findViewById(R.id.parentLy);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        minPointTv = findViewById(R.id.minimum_pointsTv);
        countTV = findViewById(R.id.countTV);
        totalPointTv = findViewById(R.id.totalPointTv);

       // GetSettings(countryId);

        minPointTv.setText(String.valueOf(minimumPoints));
        totalPointTv.setText(String.valueOf(total));




        parentLy.setOnClickListener(v -> {
            dismiss();
        });

        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count= Integer.parseInt(countTV.getText().toString());
                ++count;
                countTV.setText(String.valueOf(count));


            }
        });

        minusBtn.setOnClickListener(v -> {
            int count= Integer.parseInt(countTV.getText().toString());
            --count;
            countTV.setText(String.valueOf(count));

        });


    }


    private void GetSettings(int countryId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingCouponsModel> result = (ResultAPIModel<SettingCouponsModel>) obj;

            if (result.isSuccessful()) {
                if(result.data != null){
                    SettingCouponsModel settingCouponsModel=result.data;
                    Log.i(getClass().getSimpleName(),"Log minimumPoints "+settingCouponsModel.minimumPoints);
                    Log.i(getClass().getSimpleName(),"Log  value "+settingCouponsModel.value);

                    minPointTv.setText(String.valueOf(settingCouponsModel.minimumPoints));
                    totalPointTv.setText(String.valueOf(total));




                }


            }

        }).getSettings(countryId);
    }









}
