package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amour.shop.R;

public class AddRateDialog extends Dialog {

    TextView cancelBtn, okBtn;

    Activity activity;
    EditText rateEt;

    public AddRateDialog(Context context, String message, int okStr, int cancelStr, final  AddRateDialog.Click okCall, final AddRateDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        setContentView(R.layout.dialog_add_rate);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCancelable(true);

        setCancelable(false);

        cancelBtn = findViewById(R.id.noBtn);
        okBtn = findViewById(R.id.yesBtn);
        rateEt = findViewById(R.id.rateEt);



        okBtn.setOnClickListener(view -> {
            if (okCall != null)
                okCall.click();

        });
        cancelBtn.setOnClickListener(view -> {
            if (cancelCall != null)
                cancelCall.click();
            dismiss();
        });

        try {
            if (activity != null && !activity.isFinishing())
                show();
        } catch (Exception e) {
            dismiss();
        }


    }

    public static abstract class Click {
        public abstract void click();
    }
}
