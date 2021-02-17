package com.ramez.shopp.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.ramez.shopp.Models.WelcomeModel;
import com.ramez.shopp.R;

import java.util.ArrayList;

public class WelcomeSliderAdapter extends PagerAdapter {
    public ArrayList<WelcomeModel> welcomeSliderModels;
    private Context context;


    public WelcomeSliderAdapter(Context context, ArrayList<WelcomeModel> welcomeSliderModels) {
        this.context = context;
        this.welcomeSliderModels = welcomeSliderModels;
    }


    @Override
    public int getCount() {
        return welcomeSliderModels.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.fragment_welcome, container, false);


        try {

            TextView textInfoTitle =  view.findViewById(R.id.infoTitle);
            ImageView IvWelcome =  view.findViewById(R.id.IvWelcome);
            TextView textInfoTv2 =  view.findViewById(R.id.infoTv2);

            WelcomeModel welcomeSliderModel=welcomeSliderModels.get(position);

            textInfoTitle.setText(welcomeSliderModel.getInfoTxtTitle());

            try {
                IvWelcome.setImageDrawable(ContextCompat.getDrawable(context, welcomeSliderModel.getImage()));
            } catch (Resources.NotFoundException e) {
                IvWelcome.setImageResource(welcomeSliderModel.getImage());
            }

            textInfoTv2.setText(welcomeSliderModel.getInfoTxt2());
            container.addView(view);
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