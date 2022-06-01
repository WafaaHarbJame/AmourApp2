package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.amour.shop.classes.Constants;

public class STextViewIcon extends AppCompatTextView {
    public STextViewIcon(Context context) {
        super(context);
        init();
    }

    public STextViewIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextViewIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
         Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.ICON_AWSM_FONT);

        setTypeface(typeface);
    }
}
