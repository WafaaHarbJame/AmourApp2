package com.ramez.shopp.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ramez.shopp.R;
import com.ramez.shopp.Utils.LocaleUtils;


public class ActivityBase extends AppCompatActivity {

    protected boolean isMainActivity = false;

    int onStartCount = 0;

    protected ImageView mainLogo;
    protected TextView mainTitle;
    protected ImageView home;
    protected LinearLayout toolbar;
    int back_to_home;


    public ActivityBase() {
        LocaleUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();


// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_color));

//        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }


    }


    @Override
    public void setTitle(CharSequence title) {

        toolbar = findViewById(R.id.tool_bar);
        home = toolbar.findViewById(R.id.backBtn);
        mainLogo = toolbar.findViewById(R.id.logoImg);
        mainTitle = toolbar.findViewById(R.id.mainTitleTv);


        mainTitle.setText(title);

        if (!isMainActivity) {
            home.setVisibility(View.VISIBLE);


        } else {

            home.setVisibility(View.GONE);

        }

        home.setOnClickListener(view -> {

            onBackPressed();

        });

        super.setTitle(title);

    }


    protected Activity getActiviy() {
        return this;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {

            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void showSoftKeyboard(Activity activity) {
        try {

            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
//            inputMethodManager.showSoftInput(_searchText, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
//                    System.out.println("Log event Action " + event.getAction());
                    if (event.getAction() != MotionEvent.ACTION_SCROLL) {
                        hideSoftKeyboard(getActiviy());
                    }

                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void Toast(String msg) {

        Toast.makeText(getActiviy(), msg, Toast.LENGTH_SHORT).show();
    }

    public void Toast(int resId) {

        Toast.makeText(getActiviy(), getString(resId), Toast.LENGTH_SHORT).show();
    }


}

