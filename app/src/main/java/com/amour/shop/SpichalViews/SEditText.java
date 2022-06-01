package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.amour.shop.classes.Constants;


public class SEditText extends AppCompatEditText {
    public SEditText(Context context) {
        super(context);init();
    }

    public SEditText(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }

    public SEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);init();
    }

    private void init(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        setTypeface(typeface);
    }

}
