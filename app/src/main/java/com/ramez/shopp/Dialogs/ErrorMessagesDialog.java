package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramez.shopp.R;


public class ErrorMessagesDialog extends Dialog {


    private TextView messageTxt, okTxt;
    private LinearLayout okBtn;
    private TextView closeBtn;

    public ErrorMessagesDialog(Activity activity, int message, int okStr, Click okCall) {
        super(activity);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        setContentView(R.layout.dialog_my_info);

        messageTxt = findViewById(R.id.messageTxt);
        okBtn = findViewById(R.id.okBtn);
        okTxt = findViewById(R.id.okTxt);
        closeBtn = findViewById(R.id.closeBtn);

        okTxt.setText(okStr);
        messageTxt.setText(message);

        initListener();

        okBtn.setOnClickListener(view -> {
            if (okCall != null)
                okCall.click();
            dismiss();
        });



        try {
            if (activity != null && !activity.isFinishing())
                show();
        } catch (Exception e) {
            dismiss();
        }

    }

    private void initListener() {

        closeBtn.setOnClickListener(view -> {

            dismiss();
        });
    }

    public static abstract class Click {
        public abstract void click();
    }


}
