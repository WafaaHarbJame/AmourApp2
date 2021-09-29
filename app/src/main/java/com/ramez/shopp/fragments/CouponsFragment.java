package com.ramez.shopp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.adapter.CouponsAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.GenerateDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCouponsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CouponsFragment extends FragmentBase implements CouponsAdapter.OnItemClick {

    List<CouponsModel> list;
    private FragmentCouponsBinding binding;
    private CouponsAdapter adapter;
    private int userId, countryId;
    TotalPointModel totalPointModel;
    SettingCouponsModel settingCouponsModel;
    LocalModel localModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCouponsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());
        countryId = localModel.getCountryId();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.myOrderRecycler.setLayoutManager(linearLayoutManager);

        getData();


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getData();
        });


        binding.generateBut.setOnClickListener(v -> {
            if (totalPointModel != null && totalPointModel.points > 0) {
                showGenerateDialog();
            } else {
                getData();

            }

            showGenerateDialog();


        });

        binding.noDataLY.btnBrowseProducts.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

    }

    private void showGenerateDialog() {
        double points = totalPointModel != null ? totalPointModel.points : 0;

        if(settingCouponsModel==null){
            getCouponSettings();
        }

        int minimumPoints=settingCouponsModel != null  && settingCouponsModel.minimumPoints > 0 ? settingCouponsModel.minimumPoints : 0;

        GenerateDialog generateDialog = new GenerateDialog(getActivityy(),
                userId, points,
                minimumPoints, (obj, func, IsSuccess) -> {
            if (IsSuccess) {
                GlobalData.refresh_points = true;
                callGetTotalPoints();

            }
        });
        generateDialog.show();
    }

    private void initAdapter(List<CouponsModel> list) {

        adapter = new CouponsAdapter(getActivity(), list, list.size(), this);
        binding.myOrderRecycler.setAdapter(adapter);


    }


    private void getData() {

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
            userId = UtilityApp.getUserData().getId();
            getTotalPoint();
            getCouponSettings();
            callApi(true);

        } else {
            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
            checkLoginDialog.show();
        }

    }

    private void callApi(boolean loading) {

        if (loading) {
            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
            binding.noDataLY.noDataLY.setVisibility(View.GONE);
            binding.dataLY.setVisibility(View.GONE);
        }
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
                ResultAPIModel<List<CouponsModel>> result = (ResultAPIModel<List<CouponsModel>>) obj;
                if (IsSuccess) {
                    if (result != null && result.isSuccessful()) {

                        if (result.data != null && result.data.size() > 0) {
                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            List<CouponsModel> list = result.data;
                            initAdapter(list);

                        } else {
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.tvErrorMessage.setText(getString(R.string.no_data));
                            binding.noDataLY.titleTv.setText(getString(R.string.Coupons));
                            binding.noDataLY.btnBrowseProducts.setVisibility(View.GONE);
                            binding.dataLY.setVisibility(View.GONE);
                        }

                    }
                } else {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.dataLY.setVisibility(View.GONE);
                }
            }

        }).getCoupons(userId);
    }

    private void getTotalPoint() {

        totalPointModel = DBFunction.getTotalPoints();

        if (totalPointModel == null) {
            callGetTotalPoints();
        }


    }

    private void callGetTotalPoints() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<TotalPointModel> result = (ResultAPIModel<TotalPointModel>) obj;

            if (IsSuccess) {
                if (result != null && result.isSuccessful() && result.data != null) {

                    totalPointModel = result.data;
                    Log.i(getClass().getSimpleName(), "Log  totalPointModel call " + totalPointModel.points);
                    Log.i(getClass().getSimpleName(), "Log  totalPointModel call" + totalPointModel.value);
                    DBFunction.setTotalPoints(totalPointModel);
                }
            }


        }).getTotalPoint(userId);


    }

    private void getCouponSettings() {

        settingCouponsModel = DBFunction.getCouponSettings();
        if (settingCouponsModel == null)
            callGetCouponSettings();

    }


    private void callGetCouponSettings() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingCouponsModel> result = (ResultAPIModel<SettingCouponsModel>) obj;

            if (IsSuccess) {

                if (result != null && result.data != null && result.status == 200) {
                    settingCouponsModel = result.data;
                    DBFunction.setCouponSettings(settingCouponsModel);
                }


            }


        }).getSettings(countryId);

    }


    @Override
    public void onItemClicked(int position, CouponsModel categoryModel) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (GlobalData.refresh_points) {
            callGetTotalPoints();
            GlobalData.refresh_points = false;

        }
    }


}