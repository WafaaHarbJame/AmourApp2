package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.amour.shop.R;


public class NoConnectionDialog {
//        Activity activity;

    static ConnectionDialog connectionDialog;

    public static ConnectionDialog with(Activity activity) {

        if (connectionDialog == null) {
            connectionDialog = new ConnectionDialog(activity);
            return connectionDialog;
        } else {
            return connectionDialog;
        }

    }

    public static class ConnectionDialog extends Dialog {

        private LinearLayout okBtn;
        Activity activity;

        protected ConnectionDialog(Activity activity) {
            super(activity);

//        this.activity = activity;

            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            requestWindowFeature(Window.FEATURE_NO_TITLE); //before

            setContentView(R.layout.dialog_no_connection);

            okBtn =  findViewById(R.id.okBtn);

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            try {
                if (activity != null && !activity.isFinishing())
                    show();
            } catch (Exception e) {
                dismiss();
            }
        }

        private ConnectionDialog getDialog() {
            return this;
        }

    }
}
