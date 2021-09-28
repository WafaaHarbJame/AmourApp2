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

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ramez.shopp.R;

public class WhatsUpDialog extends Dialog {


    Activity activity;
    ConstraintLayout parentLy;
    TextView messageTv, titleTv;

    public WhatsUpDialog(Context context, int title, int message, int okStr, int cancelStr, final ConfirmDialog.Click okCall, final ConfirmDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(R.layout.dialog_whatsup);
        getWindow().setDimAmount(0);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);

        parentLy = findViewById(R.id.parentLy);
        messageTv = findViewById(R.id.messageTxt);
        titleTv = findViewById(R.id.titleTxt);

        messageTv.setText(message);
        titleTv.setText(title);

        parentLy.setOnClickListener(v -> {
            dismiss();
        });

        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }


    }

}
