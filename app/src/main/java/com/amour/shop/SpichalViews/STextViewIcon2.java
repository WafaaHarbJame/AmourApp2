package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.amour.shop.classes.Constants;


/**
 * Created by ahmed barakat on 8/20/14.
 */
public class STextViewIcon2 extends AppCompatTextView {
    public STextViewIcon2(Context context) {
        super(context);
        init();
    }

    public STextViewIcon2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextViewIcon2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
         Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.ICON_OLD_AWSM_FONT);

        setTypeface(typeface);
    }
}
