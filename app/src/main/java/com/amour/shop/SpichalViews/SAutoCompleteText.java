package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.amour.shop.classes.Constants;


public class SAutoCompleteText extends AppCompatAutoCompleteTextView {

    Context context;
    public SAutoCompleteText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SAutoCompleteText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SAutoCompleteText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init(){
//        String name = context.getResources().getString(R.s)
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        setTypeface(typeface);
    }
}
