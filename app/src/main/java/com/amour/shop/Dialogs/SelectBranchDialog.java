package com.amour.shop.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.amour.shop.adapter.CityAdapter;
import com.amour.shop.ApiHandler.DataFetcherCallBack;
import com.amour.shop.Models.CityModel;
import com.amour.shop.R;
import com.amour.shop.databinding.DialogSelectBranchBinding;

import java.util.ArrayList;

public class SelectBranchDialog extends Dialog {


    Activity activity;
    ArrayList<CityModel> cityModelsList;
    CityModel selectedCity;
    private DataFetcherCallBack dataFetcherCallBack;

    private DialogSelectBranchBinding binding;

    public SelectBranchDialog(Context context, CityModel city, ArrayList<CityModel> citiesList, DataFetcherCallBack callBack) {
        super(context);

        activity = (Activity) context;
        this.dataFetcherCallBack = callBack;
        this.cityModelsList = citiesList;
        this.selectedCity = city;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DialogSelectBranchBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setCancelable(true);


        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(activity));


        binding.confirmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedCity == null) {
                    Toast.makeText(activity, activity.getString(R.string.please_select_branch), Toast.LENGTH_SHORT).show();
                    return;
                }

                dismiss();
                dataFetcherCallBack.Result(selectedCity, "", true);
            }
        });

        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }

        initCityAdapter();

    }


    public void initCityAdapter() {

        CityAdapter cityAdapter = new CityAdapter(activity, cityModelsList, selectedCity != null ? selectedCity.getId() : 0,
                new CityAdapter.OnCityClick() {
            @Override
            public void onCityClicked(int position, CityModel cityModel) {
                selectedCity = cityModel;
            }
        });
        binding.rv.setAdapter(cityAdapter);


    }

}
