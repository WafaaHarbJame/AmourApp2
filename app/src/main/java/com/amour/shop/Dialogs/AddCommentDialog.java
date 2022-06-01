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

public class AddCommentDialog extends Dialog {

    TextView cancelBtn, okBtn;

    Activity activity;
    EditText remarkET;

    public AddCommentDialog(Context context, String message, int okStr, int cancelStr, final  AddCommentDialog.Click okCall, final AddCommentDialog.Click cancelCall) {
        super(context);

        activity = (Activity) context;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        setContentView(R.layout.dialog_add_comment);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        setCancelable(true);

        setCancelable(false);

        cancelBtn = findViewById(R.id.noBtn);
        okBtn = findViewById(R.id.yesBtn);
        remarkET = findViewById(R.id.remarkET);
        remarkET.setText(message);



        okBtn.setOnClickListener(view -> {
            if (okCall != null)
                okCall.click();
            dismiss();
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
