package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.GlobalData;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.DialogGenerateCouponsBinding;

public class GenerateDialog extends Dialog {


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

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setCancelable(true);

        count = total < minimumPoints ? (int) total : minimumPoints;

        binding.minPointTv.setText(String.valueOf(minimumPoints));
        binding.totalPointTv.setText(String.valueOf(total));
        binding.countTV.setText(String.valueOf(count));

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
                    GlobalData.INSTANCE.Toast(activity, R.string.you_reach_max_point);
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
                GlobalData.INSTANCE.Toast(activity, message);
            }


        });

        binding.generateBut.setOnClickListener(v -> {

            if (count < minimumPoints) {
                String message = activity.getString(R.string.minimum_points_needed) + " " + minimumPoints;
                GlobalData.INSTANCE.Toast(activity, message);
                return;
            }
            sendGenerateCoupon(count);


        });


    }

    private void sendGenerateCoupon(int points) {

        GlobalData.INSTANCE.progressDialog(activity, R.string.Generate_Coupons, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.INSTANCE.hideProgressDialog();
            if (IsSuccess) {
                GeneralModel result = (GeneralModel) obj;
                if (result != null && result.isSuccessful()) {
                    GlobalData.INSTANCE.Toast(activity, R.string.success_generate_coupon);
                    dismiss();
                    dataFetcherCallBack.Result("", Constants.success, true);
                } else {
                    String message = activity.getString(R.string.fail_generate_coupon);
                    if (result != null && result.getMessage() != null && !result.getMessage().isEmpty()) {
                        message = result.getMessage();
                    }
                    GlobalData.INSTANCE.Toast(activity, message);
                }
            } else {

                String message = activity.getString(R.string.fail_generate_coupon);

                GlobalData.INSTANCE.Toast(activity, message);

            }


        }).generateCoupon(userId, points);

    }


}
