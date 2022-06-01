package com.amour.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amour.shop.classes.Constants;
import com.amour.shop.classes.GlobalData;
import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Models.Slider;
import com.amour.shop.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutomateSlider extends SliderViewAdapter<AutomateSlider.sliderAdapterVH> {

    private Context context;
    private List<Slider> mSliderItems = new ArrayList();
    private OnSliderClick onSliderClick;


    public AutomateSlider(Context context, List<Slider> mSliderItems, OnSliderClick onSliderClick) {
        this.context = context;
        this.mSliderItems = mSliderItems;
        this.onSliderClick = onSliderClick;
    }


    @Override
    public sliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_slider, null);
        return new sliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(sliderAdapterVH viewHolder, final int position) {

        Slider slider = mSliderItems.get(position);
        String imageUrl;


        if (UtilityApp.INSTANCE.getLanguage().equals(Constants.English)) {
            imageUrl = slider.getImage();

        } else {
            imageUrl = slider.getImage2();

        }


        try {
            GlobalData.INSTANCE.GlideImg(context,imageUrl,R.drawable.holder_image,viewHolder.imageViewBackground);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        Glide.with(viewHolder.itemView).load(imageUrl).error(R.drawable.holder_image).placeholder(R.drawable.holder_image).fitCenter().into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(view -> onSliderClick.onSliderClicked(position, slider));

    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public interface OnSliderClick {
        void onSliderClicked(int position, Slider slider);
    }

    class sliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public sliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slideImg);
            this.itemView = itemView;


        }


    }

}
