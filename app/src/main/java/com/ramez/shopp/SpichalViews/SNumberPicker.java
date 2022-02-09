package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;

import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.R;

public class SNumberPicker extends NumberPicker {

    Typeface type;

    public SNumberPicker(Context context) {
        super(context);
//        init();
    }

    public SNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public SNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        init();
    }

//    private void init() {
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "a_jannat_lt.otf");
//
////        setTypeface(typeface);
//    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        type = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);

        type = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        updateView(child);
    }

    private void updateView(View view) {

        if (view instanceof EditText) {
            ((EditText) view).setTypeface(type);
//            ((EditText) view).setTextSize(25);
            ((EditText) view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }

    }

}

