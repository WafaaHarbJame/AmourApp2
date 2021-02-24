package com.ramez.shopp.Adapter;

import android.app.Activity;
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
import com.ramez.shopp.Dialogs.ShowImageDialog;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;

import java.util.ArrayList;

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

            if (UtilityApp.getLanguage().equals(Constants.English)) {
                imageUrl=slider.getImage();

            }
            else {
                imageUrl=slider.getImage2();

            }

            GlobalData.PicassoImg(imageUrl, R.drawable.holder_image, productImg);

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