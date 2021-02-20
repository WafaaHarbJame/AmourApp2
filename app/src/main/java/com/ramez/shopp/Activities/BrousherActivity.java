package com.ramez.shopp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Adapter.BrouchersrAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrochuresModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityBrousherBinding;

import java.util.ArrayList;

public class BrousherActivity extends ActivityBase implements BrouchersrAdapter.onBroucherClick {
    ActivityBrousherBinding binding;
    ArrayList<BrochuresModel> list;
    private LocalModel localModel;
    private int booklet_id, store_id;
    private BrouchersrAdapter brouchersrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrousherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActiviy());
        binding.broucherRecycler.setLayoutManager(linearLayoutManager);
        binding.broucherRecycler.setHasFixedSize(true);
        binding.broucherRecycler.setItemAnimator(null);
        localModel = UtilityApp.getLocalData();
        store_id = Integer.parseInt(localModel.getCityId());

        binding.swipeContainer.setOnRefreshListener(() -> {
            binding.swipeContainer.setRefreshing(false);
            getBrochuresList(store_id, booklet_id);
        });

        getExtraIntent();

    }

    public void getBrochuresList(int store_id, int booklet_id) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<BrochuresModel>> result = (ResultAPIModel<ArrayList<BrochuresModel>>) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
            binding.dataLY.setVisibility(View.VISIBLE);

            if (func.equals(Constants.ERROR)) {

                if (result.message != null) {
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

        }).getBrochuresList(store_id, booklet_id);
    }


    private void initAdapter() {

        brouchersrAdapter = new BrouchersrAdapter(getActiviy(), list, booklet_id, binding.broucherRecycler, this);
        binding.broucherRecycler.setAdapter(brouchersrAdapter);

    }


    private void getExtraIntent() {
        if (getIntent().getExtras() != null) {
            BookletsModel bookletsModel = (BookletsModel) getIntent().getSerializableExtra(Constants.bookletsModel);
            booklet_id = bookletsModel.getId();

            if(!bookletsModel.getTitleName().isEmpty()){
                setTitle(bookletsModel.getTitleName());

            }
            getBrochuresList(store_id, booklet_id);

        }


    }


}