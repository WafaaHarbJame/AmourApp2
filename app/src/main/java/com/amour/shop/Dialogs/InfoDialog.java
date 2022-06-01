package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amour.shop.ApiHandler.DataFetcherCallBack;
import com.amour.shop.R;


public class InfoDialog extends Dialog {
    TextView messageTxt;
    Activity activity;
    DataFetcherCallBack dataFetcherCallBack;
    TextView closeBtn;
    private LinearLayout okBtn;


    public InfoDialog(Activity activity, String message, boolean isHtml, final DataFetcherCallBack dataFetcherCallBack) {
        super(activity);

        this.activity = activity;

        this.dataFetcherCallBack = dataFetcherCallBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
//        setTitle(title);
        setContentView(R.layout.dialog_my_info);

        messageTxt = findViewById(R.id.messageTxt);
        okBtn = findViewById(R.id.okBtn);
        closeBtn = findViewById(R.id.closeBtn);

        if (isHtml) {
            messageTxt.setText(Html.fromHtml(message));
        } else {
            if (message != null && !message.equals("")) {
                messageTxt.setText(message);
            }

        }

        okBtn.setOnClickListener(view -> {

            if (dataFetcherCallBack != null)
                dataFetcherCallBack.Result(getDialog(), "InfoDialog", true);
            dismiss();
        });


        closeBtn.setOnClickListener(view -> {
            dismiss();
        });

        try {
            if (activity != null && !activity.isFinishing())
                show();
        } catch (Exception e) {
            dismiss();
        }


    }

    private InfoDialog getDialog() {
        return this;
    }

}
