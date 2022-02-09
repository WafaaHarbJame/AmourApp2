package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.ramez.shopp.classes.Constants;


/**
 * Created by ahmed barakat on 8/20/14.
 */
public class SButtonIcon extends AppCompatButton {
    public SButtonIcon(Context context) {
        super(context);
        init();
    }

    public SButtonIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SButtonIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
       Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.ICON_AWSM_FONT);

        setTypeface(typeface);
    }
}
