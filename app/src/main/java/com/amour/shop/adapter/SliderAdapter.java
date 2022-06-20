package com.amour.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.amour.shop.classes.Constants;
import com.amour.shop.classes.GlobalData;
import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Dialogs.ShowImageDialog;
import com.amour.shop.Models.Slider;
import com.amour.shop.R;

import java.util.ArrayList;
import java.util.Objects;

public class SliderAdapter extends PagerAdapter {
    public ArrayList<Slider> sliderList;
    private Context context;


    public SliderAdapter(Context context, ArrayList<Slider> sliderModels) {
        this.context = context;
        this.sliderList = sliderModels;
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

            if (Objects.equals(UtilityApp.INSTANCE.getLanguage(), Constants.English)) {
                imageUrl=slider.getImage();

            }
            else {
                imageUrl=slider.getImage2();

            }

            Log.i("tag","Log slider url "+imageUrl);

            try {

                GlobalData.INSTANCE.GlideImg(context,imageUrl, R.drawable.holder_image, productImg);


            } catch (Exception e) {
                e.printStackTrace();
            }


            container.addView(view);

            productImg.setOnClickListener(view1 -> {
                ShowImageDialog showImageDialog = new ShowImageDialog((Activity) context, imageUrl);
                showImageDialog.show();

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


}