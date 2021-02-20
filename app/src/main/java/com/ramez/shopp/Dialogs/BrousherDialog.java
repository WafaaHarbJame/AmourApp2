package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.ramez.shopp.CallBack.DataFetcherCallBack;
import com.ramez.shopp.R;


public class BrousherDialog extends Dialog {
    Activity activity;
    DataFetcherCallBack dataFetcherCallBack;


    public BrousherDialog(Activity activity, final DataFetcherCallBack dataFetcherCallBack) {
        super(activity);

        this.activity = activity;
        this.dataFetcherCallBack = dataFetcherCallBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        setContentView(R.layout.progressdialog);

    }

    private BrousherDialog getDialog() {
        return this;
    }

}
