package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;

public class GenerateDialog extends Dialog {


    Activity activity;
    ConstraintLayout parentLy;
    TextView minusBtn, countTV,plusBtn;

    public GenerateDialog(Context context, int title, int message, int okStr, int cancelStr, final ConfirmDialog.Click okCall, final ConfirmDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;

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
        countTV = findViewById(R.id.countTV);

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




}
