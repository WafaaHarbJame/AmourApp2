package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;

import com.amour.shop.ApiHandler.DataFetcherCallBack;
import com.amour.shop.classes.Constants;
import com.amour.shop.R;


public class PickImageDialog extends Dialog {

    private LinearLayout captureImgBtn;
    private LinearLayout pickImgBtn;


    Activity activity;
    DataFetcherCallBack dataFetcherCallBack;

    public PickImageDialog(Activity activity, final DataFetcherCallBack dataFetcherCallBack) {
        super(activity);

        this.activity = activity;

        this.dataFetcherCallBack = dataFetcherCallBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        setContentView(R.layout.dialog_pick_image);

        captureImgBtn =  findViewById(R.id.captureImgBtn);
        pickImgBtn =  findViewById(R.id.pickImgBtn);

        captureImgBtn.setOnClickListener(view -> {
            if (dataFetcherCallBack != null)
                dataFetcherCallBack.Result(getDialog(), Constants.CAPTURE, true);
            dismiss();
        });

        pickImgBtn.setOnClickListener(view -> {

            if (dataFetcherCallBack != null)
                dataFetcherCallBack.Result(getDialog(), Constants.PICK, true);
            dismiss();
        });

        try {
            if (activity != null && !activity.isFinishing())
                show();
        } catch (Exception e) {
            dismiss();
        }

    }

    private PickImageDialog getDialog() {
        return this;
    }

}
