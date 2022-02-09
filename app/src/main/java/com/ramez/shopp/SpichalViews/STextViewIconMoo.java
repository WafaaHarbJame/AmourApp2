package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.ramez.shopp.classes.Constants;


/**
 * Created by ahmed barakat on 8/20/14.
 */
public class STextViewIconMoo extends AppCompatTextView {
    public STextViewIconMoo(Context context) {
        super(context);
        init();
    }

    public STextViewIconMoo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextViewIconMoo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
         Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.ICON_MOON_FONT);

        setTypeface(typeface);
    }
}
