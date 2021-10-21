package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;

import java.util.ArrayList;

public class MainSliderAdapter extends PagerAdapter {
    public ArrayList<Slider> sliderList;
    private Context context;
    private OnSliderClick onSliderClick;


    public MainSliderAdapter(Context context, ArrayList<Slider> sliderModels, OnSliderClick onSliderClick) {
        this.context = context;
        this.sliderList = sliderModels;
        this.onSliderClick=onSliderClick;
    }


    @Override
    public int getCount() {
        return sliderList.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.fragment_slider, container, false);

        String imageUrl;

        try {

            ImageView productImg = view.findViewById(R.id.slideImg);
            Slider slider=sliderList.get(position);

            if (UtilityApp.getLanguage().equals(Constants.English)) {
                imageUrl=slider.getImage();

            }
            else {
                imageUrl=slider.getImage2();

            }

            try {

                GlobalData.INSTANCE.GlideImgWeb(context,imageUrl,R.drawable.holder_image,productImg);

            } catch (Exception e) {
                e.printStackTrace();
            }



            container.addView(view);

            productImg.setOnClickListener(view1 -> {
                onSliderClick.onSliderClicked(position, slider);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public interface OnSliderClick {
        void onSliderClicked(int position, Slider slider);
    }

}