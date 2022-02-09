package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.ramez.shopp.classes.Constants;


public class SRadioButton extends AppCompatRadioButton {
    public SRadioButton(Context context) {
        super(context);
        init();
    }

    public SRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

       setTypeface(typeface);
    }
}
