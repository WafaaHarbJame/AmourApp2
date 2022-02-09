package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.R;
import com.ramez.shopp.activities.RegisterLoginActivity;

public class CheckLoginDialog extends Dialog {


    Activity activity;
    TextView loginBut, registerBut,closeBtn,messageTv,titleTv;

    public CheckLoginDialog(Context context, int title ,int  message, int okStr, int cancelStr, final ConfirmDialog.Click okCall, final ConfirmDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_check_login);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCancelable(true);

        loginBut = findViewById(R.id.loginBut);
        registerBut = findViewById(R.id.registerBut);
        closeBtn = findViewById(R.id.closeBtn);
        messageTv=findViewById(R.id.messageTxt);
        titleTv=findViewById(R.id.titleTxt);

        messageTv.setText(message);
        titleTv.setText(title);

        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }

        loginBut.setOnClickListener(view -> {

            dismiss();
            Intent intent = new Intent(context, RegisterLoginActivity.class);
            intent.putExtra(Constants.LOGIN, true);
            context.startActivity(intent);

        });
        registerBut.setOnClickListener(view -> {

            dismiss();
            Intent intent = new Intent(context, RegisterLoginActivity.class);
            intent.putExtra(Constants.REGISTER, true);
            context.startActivity(intent);

        });

        closeBtn.setOnClickListener(view -> {
         dismiss();

        });



    }

}
