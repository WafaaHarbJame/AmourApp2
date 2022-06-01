package com.amour.shop.SpichalViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.amour.shop.classes.Constants;
import com.amour.shop.R;


/**
 * Created by iSoft4is on 8/27/2016.
 */
public class STabLayout extends TabLayout {
    int tabTxtColor;
    float tabTxtSize = 15;

    public STabLayout(Context context) {
        super(context);
        tabTxtColor = ContextCompat.getColor(context, R.color.colorPrimary);
    }

    public STabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        tabTxtColor = ContextCompat.getColor(context, R.color.colorPrimary);
    }

    public STabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tabTxtColor = ContextCompat.getColor(context, R.color.colorPrimary);
    }

    public void setTabTextColor(int tabTextColor) {
        tabTxtColor = tabTextColor;
    }

    public void setTabTextSize(float tabTextSize) {
        tabTextSize = tabTextSize;
    }

    @Override
    public void setupWithViewPager(ViewPager viewPager) {

        super.setupWithViewPager(viewPager);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constants.NORMAL_FONT);

        this.removeAllTabs();
        ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

//        for (int i = 0, count = viewPager.getAdapter().getCount(); i < count; i++) {
//            Tab tab = this.newTab();
//            LinearLayout customTabLY = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
//            TextView sTextViewBold = (TextView) customTabLY.findViewById(android.R.id.text1);
//            sTextViewBold.setTextColor(tabTxtColor);
//            sTextViewBold.setTextSize(tabTxtSize);
//            tab.setCustomView(customTabLY);
//            this.addTab(tab.setText(viewPager.getAdapter().getPageTitle(i)));
//            AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
//            view.setTypeface(typeface);
//        }
    }

}
