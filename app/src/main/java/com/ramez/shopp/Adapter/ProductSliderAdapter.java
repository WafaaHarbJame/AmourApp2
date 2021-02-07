package com.ramez.shopp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ramez.shopp.Dialogs.ShowImageDialog;
import com.ramez.shopp.Models.SliderModel;
import com.ramez.shopp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductSliderAdapter extends PagerAdapter {
    public ArrayList<String> sliderList;
    private Context context;


    public ProductSliderAdapter(Context context, ArrayList<String> sliderModels) {
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

        View view = layoutInflater.inflate(R.layout.fragment_slider_image, container, false);


        try {

            ImageView productImg =  view.findViewById(R.id.productImg);
//            Glide.with(context).asBitmap().load(sliderList.get(position)).placeholder(R.drawable.holder_image).into(productImg);

            Picasso.get().load(sliderList.get(position)).placeholder(R.drawable.holder_image)
                    .error(R.drawable.holder_image).into(productImg);

            container.addView(view);

            Picasso.get().load(sliderList.get(position)).placeholder(R.drawable.holder_image).error(R.drawable.holder_image).into(productImg);

            productImg.setOnClickListener(view1 -> {
                ShowImageDialog showImageDialog = new ShowImageDialog((Activity) context,sliderList.get(position));
                showImageDialog.show();

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;    }



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