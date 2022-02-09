package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.ramez.shopp.classes.Constants;


public class STextView extends AppCompatTextView {
    public STextView(Context context) {
        super(context);
        init();
    }

    public STextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        setTypeface(typeface);
    }

}
