package com.ramez.shopp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.adapter.MyOrdersAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.OrderNewModel;
import com.ramez.shopp.Models.OrderResultModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCurrentOrderBinding;

import java.util.ArrayList;
import java.util.List;


public class CurrentOrderFragment extends FragmentBase {

    List<OrderNewModel> currentOrdersList;
    private FragmentCurrentOrderBinding binding;
    private MyOrdersAdapter myOrdersAdapter;
    private int user_id;
    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCurrentOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        currentOrdersList = new ArrayList<>();
        activity = getActivity();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.myOrderRecycler.setLayoutManager(linearLayoutManager);



        if (UtilityApp.isLogin()) {
            if (UtilityApp.getUserData() != null) {
                user_id = UtilityApp.getUserData().getId();
            }
            getUpcomingOrders(user_id,Constants.user_type,"u");

        }


        binding.swipe.setOnRefreshListener(() -> {
            getUpcomingOrders(user_id,Constants.user_type,"u");
            binding.swipe.setRefreshing(false);
        });

        binding.noDataLY.btnBrowseProducts.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        });

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getUpcomingOrders(user_id,Constants.user_type,"u");
        });


        return view;
    }



    private void initOrdersAdapters(List<OrderNewModel> list) {
        Log.i("TAG", "Log upcoming adapter  " + list.size());

        myOrdersAdapter = new MyOrdersAdapter(getActivity(), binding.myOrderRecycler, list, user_id);
        binding.myOrderRecycler.setAdapter(myOrdersAdapter);


    }


    public void getUpcomingOrders(int user_id,String type,String filter) {

        currentOrdersList.clear();

        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {

                OrderResultModel result = (OrderResultModel) obj;

                String message = "";
                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.message != null) {
                        message = result.message;
                    } else {
                        message = getString(R.string.fail_to_get_data);
                    }
                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.FAIL)) {

                    message = getString(R.string.fail_to_get_data);

                    if (result != null && result.message != null) {
                        message = result.message;
                    }
                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);


                } else if (func.equals(Constants.NO_CONNECTION)) {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                    binding.dataLY.setVisibility(View.GONE);

                } else {
                    if (IsSuccess) {
                        if (result.data != null && result.data.size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

                            currentOrdersList = result.data;

                            initOrdersAdapters(currentOrdersList);

                            Log.i("TAG", "Log ordersDMS" + currentOrdersList.size());


                        } else {

                            binding.dataLY.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                        }


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }
            }
        }).getOrders(user_id,type,filter);
    }

}