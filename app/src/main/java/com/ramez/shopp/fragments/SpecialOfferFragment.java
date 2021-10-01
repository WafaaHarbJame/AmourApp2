package com.ramez.shopp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.adapter.BrouchersrAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrochuresModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentSpecialOfferBinding;

import java.util.ArrayList;

public class SpecialOfferFragment extends FragmentBase implements BrouchersrAdapter.onBroucherClick {
    FragmentSpecialOfferBinding binding;
    ArrayList<BrochuresModel> list;
    private LocalModel localModel;
    private int booklet_id, store_id;
    private BrouchersrAdapter brouchersrAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpecialOfferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivityy());
        binding.broucherRecycler.setLayoutManager(linearLayoutManager);
        binding.broucherRecycler.setHasFixedSize(true);
        binding.broucherRecycler.setItemAnimator(null);
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());

        store_id = localModel.getCityId() != null ? Integer.parseInt(localModel.getCityId()) : 7263;

        binding.swipeContainer.setOnRefreshListener(() -> {
            binding.swipeContainer.setRefreshing(false);
            getBrochuresList(store_id, booklet_id);
        });

        getExtraIntent();
        return view;
    }


    public void getBrochuresList(int store_id, int booklet_id) {

        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                ResultAPIModel<ArrayList<BrochuresModel>> result = (ResultAPIModel<ArrayList<BrochuresModel>>) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
                binding.dataLY.setVisibility(View.VISIBLE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.message != null) {
                        message = result.message;
                    }
                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.FAIL)) {

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
                        Log.i("tag", "Log getBrochuresList " + result.data.size());
                        if (result.data != null && result.data.size() > 0) {
                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            list = result.data;
                            initAdapter();
                        } else {
                            binding.dataLY.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataTxt.setText(R.string.no_booklets);
                        }
                    } else {
                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);
                    }
                }
            }


        }).getBrochuresList(store_id, booklet_id);
    }


    private void initAdapter() {

        brouchersrAdapter = new BrouchersrAdapter(getActivityy(), list, booklet_id, binding.broucherRecycler, this);
        binding.broucherRecycler.setAdapter(brouchersrAdapter);

    }


    private void getExtraIntent() {

        Bundle bundle = getArguments();

        if (bundle != null) {

            BookletsModel bookletsModel = (BookletsModel) bundle.getSerializable(Constants.bookletsModel);
            if (bookletsModel != null) {
                Log.i("Log bookletsModel", "Log bookletsModel from 3 " + bookletsModel.getId());

                booklet_id = bookletsModel.getId();
                store_id = bookletsModel.getStoreID() > 0 ?bookletsModel.getStoreID():localModel.getCityId() != null ? Integer.parseInt(localModel.getCityId()) : 7263;
                ;
                Log.i("Log bookletsModel", "Log booklet_id " + booklet_id);
                Log.i("Log bookletsModel", "Log store_id " + store_id);

                getBrochuresList(store_id, booklet_id);
            }


        }


    }
}