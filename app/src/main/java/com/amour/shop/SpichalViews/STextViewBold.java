package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.amour.shop.classes.Constants;


/**
 * Created by ahmed barakat on 8/20/14.
 */
public class STextViewBold extends AppCompatTextView {
    public STextViewBold(Context context) {
        super(context);
        init();
    }

    public STextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.BOLD_FONT);

        setTypeface(typeface);
    }
}
