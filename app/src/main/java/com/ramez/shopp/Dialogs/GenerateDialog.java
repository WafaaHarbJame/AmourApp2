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
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.DialogGenerateCouponsBinding;

public class GenerateDialog extends Dialog {


//    ConstraintLayout parentLy;
//    TextView countTV, minPointTv, totalPointTv;
//    ImageView minusBtn, plusBtn;

    Activity activity;
    LocalModel localModel;
    int countryId, userId, count;
    double total;
    DataFetcherCallBack dataFetcherCallBack;

    private DialogGenerateCouponsBinding binding;

    public GenerateDialog(Context context, int userId, double totalPoints, int minimumPoints, final DataFetcherCallBack callBack) {
        super(context);

        activity = (Activity) context;
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);
        countryId = localModel.getCountryId();
        total = totalPoints;
        this.userId = userId;
        dataFetcherCallBack = callBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogGenerateCouponsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getWindow().setDimAmount(0);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);


//        parentLy = findViewById(R.id.parentLy);
//        plusBtn = findViewById(R.id.plusBtn);
//        minusBtn = findViewById(R.id.minusBtn);
//        minPointTv = findViewById(R.id.minimum_pointsTv);
//        countTV = findViewById(R.id.countTV);
//        totalPointTv = findViewById(R.id.totalPointTv);

        // GetSettings(countryId);
        count = total < minimumPoints ? (int) total : minimumPoints;

        binding.minPointTv.setText(String.valueOf(minimumPoints));
        binding.totalPointTv.setText(String.valueOf(total));
        binding.countTV.setText(String.valueOf(count));

//        parentLy.setOnClickListener(v -> {
//            dismiss();
//        });

        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }

        binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int count = Integer.parseInt(binding.countTV.getText().toString());

                if (count + minimumPoints <= (int) totalPoints) {

                    count += minimumPoints;

                    binding.countTV.setText(String.valueOf(count));
                } else {
                    GlobalData.Toast(activity, R.string.you_reach_max_point);
                }


            }
        });

        binding.minusBtn.setOnClickListener(v -> {
//            int count = Integer.parseInt(binding.countTV.getText().toString());
            if (count - minimumPoints >= minimumPoints) {
                count -= minimumPoints;
                binding.countTV.setText(String.valueOf(count));
            } else {
                String message = activity.getString(R.string.minimum_points_needed) + " " + minimumPoints;
                GlobalData.Toast(activity, message);
            }


        });

        binding.generateBut.setOnClickListener(v -> {

            if (count < minimumPoints) {
                String message = activity.getString(R.string.minimum_points_needed) + " " + minimumPoints;
                GlobalData.Toast(activity, message);
                return;
            }
            sendGenerateCoupon(count);


        });


    }

    private void sendGenerateCoupon(int points) {

        GlobalData.progressDialog(activity, R.string.Generate_Coupons, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();

            GeneralModel result = (GeneralModel) obj;
            if (result!=null && result.isSuccessful()) {
//                callGetTotalPoints();
//                GlobalData.refresh_points = true;
                GlobalData.Toast(activity, R.string.success_generate_coupon);
                dismiss();
                dataFetcherCallBack.Result("", Constants.success, true);
            } else {
                String message = activity.getString(R.string.fail_generate_coupon);
                if (result != null && result.getMessage() != null && !result.getMessage().isEmpty()){
                    message = result.getMessage();
                }
                GlobalData.Toast(activity, message);
            }

        }).generateCoupon(userId, points);

    }



}
