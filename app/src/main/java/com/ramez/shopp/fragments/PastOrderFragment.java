package com.ramez.shopp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.adapter.MyOrdersAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.OrderNewModel;
import com.ramez.shopp.Models.OrderResultModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentPastOrderBinding;

import java.util.ArrayList;
import java.util.List;


public class PastOrderFragment extends FragmentBase {
    List<OrderNewModel> completeOrdersList;
    LinearLayoutManager linearLayoutManager;
    private FragmentPastOrderBinding binding;
    private MyOrdersAdapter myOrdersAdapter;
    private int user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPastOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        completeOrdersList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.myOrderRecycler.setLayoutManager(linearLayoutManager);
        if(UtilityApp.getUserData()!=null&&UtilityApp.getUserData().getId()!=null){
            user_id = UtilityApp.getUserData().getId();
            getOrders(user_id,Constants.user_type, Constants.past_order);

        }
        else {
            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
            checkLoginDialog.show();
        }



        binding.swipe.setOnRefreshListener(() -> {
            binding.swipe.setRefreshing(false);
            getOrders(user_id,Constants.user_type,Constants.past_order);

        });


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getOrders(user_id,Constants.user_type,Constants.past_order);


        });

        binding.noDataLY.btnBrowseProducts.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });




        return view;
    }





    private void initOrdersAdapters(List<OrderNewModel> list) {

        myOrdersAdapter = new MyOrdersAdapter(getActivity(), binding.myOrderRecycler, list, user_id);
        binding.myOrderRecycler.setAdapter(myOrdersAdapter);


    }




    public void getOrders(int user_id,String type,String filter) {
        completeOrdersList.clear();
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
                    }
                    else {
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
                            completeOrdersList = result.data;
                            initOrdersAdapters(completeOrdersList);


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