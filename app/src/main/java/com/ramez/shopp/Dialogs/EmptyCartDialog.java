package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ramez.shopp.R;

public class EmptyCartDialog extends Dialog {


    Activity activity;
    TextView yesBtn, noBtn,closeBtn;

    public EmptyCartDialog(Context context, int message, int okStr, int cancelStr, final Click okCall, final Click  cancelCall) {
        super(context);

        activity = (Activity) context;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_empty_cart);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCancelable(true);

        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);
        closeBtn = findViewById(R.id.closeBtn);


        yesBtn.setOnClickListener(view -> {
            if (okCall != null)
                okCall.click();
            dismiss();
        });


        noBtn.setOnClickListener(view -> {
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



        closeBtn.setOnClickListener(view -> {
            dismiss();

        });





    }

    public static abstract class Click {
        public abstract void click();
    }

}
