package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

import com.amour.shop.classes.Constants;


public class SSwitch extends SwitchCompat {
    public SSwitch(Context context) {
        super(context);
        init();
    }

    public SSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

       setTypeface(typeface);
    }
}
