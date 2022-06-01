package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.amour.shop.ApiHandler.DataFetcherCallBack;
import com.amour.shop.R;


public class ShowImageDialog extends Dialog {
    Activity activity;
    String imageUrl;


    public ShowImageDialog(Activity activity, String imageUrl) {
        super(activity);

        this.activity = activity;
        this.imageUrl = imageUrl;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        setContentView(R.layout.show_image_dialog);
        ImageView closeBtn = findViewById(R.id.closeBtn);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setCancelable(true);


        closeBtn.setOnClickListener(v -> {
            dismiss();

        });
        ImageView touchImageView = findViewById(R.id.iv_adImageView);
        Glide.with(activity).load(imageUrl).thumbnail(0.02f).into(touchImageView);

        try {
            if (!activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }


    }

    private ShowImageDialog getDialog() {
        return this;
    }

}
