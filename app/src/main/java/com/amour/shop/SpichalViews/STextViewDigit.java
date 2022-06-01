package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.amour.shop.classes.Constants;


public class STextViewDigit extends AppCompatTextView {
    public STextViewDigit(Context context) {
        super(context);
        init();
    }

    public STextViewDigit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextViewDigit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        setTypeface(typeface);
    }
    
}
