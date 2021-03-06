package com.amour.shop.fragments;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amour.shop.classes.Constants;
import com.amour.shop.adapter.RecommendProductAdapter;
import com.amour.shop.ApiHandler.DataFeacher;
import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Models.FavouriteResultModel;
import com.amour.shop.Models.LocalModel;
import com.amour.shop.Models.ProductModel;
import com.amour.shop.databinding.FragmentProductsBinding;

import java.util.ArrayList;
import java.util.List;


public class SuggestedProductsFragment extends FragmentBase {
    FragmentProductsBinding binding;
    private List<ProductModel> recommendList;
    int country_id;
    int city_id;
    String user_id = "0";
    String filter = "";
    LocalModel localModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);

        localModel = UtilityApp.INSTANCE.getLocalData();

        country_id = localModel.getCountryId() ;
        city_id = Integer.parseInt(localModel.getCityId() );

        if (UtilityApp.INSTANCE.isLogin() && UtilityApp.INSTANCE.getUserData() != null && UtilityApp.INSTANCE.getUserData().getId() != null) {
            user_id = String.valueOf(UtilityApp.INSTANCE.getUserData().getId());

        }


        recommendList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivityy(), 3);
        binding.recycler.setLayoutManager(gridLayoutManager);
        getExtraIntent();

        searchTxt(country_id, city_id, user_id, filter, 0, 10);

        return binding.getRoot();


    }

    private void initAdapter() {

        List<ProductModel> list = recommendList.subList(0, Math.min(recommendList.size(), 5));
        list.add(null);

        if (list.size() == 0) {
            binding.recycler.setVisibility(View.GONE);
            binding.noProductsTv.setVisibility(View.VISIBLE);
        } else {
            binding.recycler.setVisibility(View.VISIBLE);
            binding.noProductsTv.setVisibility(View.GONE);
        }

        RecommendProductAdapter adapter = new RecommendProductAdapter(getActivityy(), filter, list);
        binding.recycler.setAdapter(adapter);
    }


    private void getExtraIntent() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            filter = bundle.getString(Constants.KEY_FILTER);
        }


    }

    public void searchTxt(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {
        recommendList.clear();
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            FavouriteResultModel result = (FavouriteResultModel) obj;

            if (IsSuccess) {
                if (result.getData() != null && result.getData().size() > 0) {

                    recommendList = result.getData();
                    Log.i(getClass().getSimpleName(), "Log recommendList " + recommendList.size());

                    initAdapter();

                }


            }

        }).searchTxt(country_id, city_id, user_id, filter, page_number, page_size);
    }

}