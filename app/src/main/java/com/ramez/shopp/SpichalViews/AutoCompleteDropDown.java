package com.ramez.shopp.SpichalViews;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.ramez.shopp.classes.Constants;


public class AutoCompleteDropDown extends AppCompatAutoCompleteTextView {
    //    implements AdapterView.OnItemClickListener
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private boolean isPopup;
    private int mPosition = ListView.INVALID_POSITION;

    public AutoCompleteDropDown(Context context) {
        super(context);
        init();
//        setOnItemClickListener(this);
    }

    public AutoCompleteDropDown(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init();
//        setOnItemClickListener(this);
    }

    public AutoCompleteDropDown(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
//        setOnItemClickListener(this);
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        setTypeface(typeface);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        try {
            if (focused) {
                performFiltering("", 0);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
                setKeyListener(null);
                dismissDropDown();
            } else {
                isPopup = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
            setKeyListener(null);
            dismissDropDown();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (isPopup) {
                    dismissDropDown();
                } else {
                    try {
                        requestFocus();
                        showDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void showDropDown() {
        super.showDropDown();
        isPopup = true;
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        isPopup = false;
    }


//    @Override
//    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
//        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_down);
//        if (dropdownIcon != null) {
//            right = dropdownIcon;
//            right.mutate().setAlpha(5);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            super.setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
//        } else {
//            super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
//        }
//
//    }


    public int getPosition() {
        return mPosition;
    }
}