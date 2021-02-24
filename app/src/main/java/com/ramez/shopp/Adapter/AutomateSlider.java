package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
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

        if (UtilityApp.getLanguage().equals(Constants.English)) {
            imageUrl = slider.getImage();

        } else {
            imageUrl = slider.getImage2();

        }


        Glide.with(viewHolder.itemView).load(imageUrl).error(R.drawable.slider_image).placeholder(R.drawable.slider_image).fitCenter().into(viewHolder.imageViewBackground);

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
