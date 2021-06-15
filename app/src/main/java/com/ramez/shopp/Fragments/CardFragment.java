package com.ramez.shopp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Adapter.CardsTransAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.SoicalLink;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.GenerateDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.Data;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.Models.TransactionModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCardBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CardFragment extends FragmentBase {

    private FragmentCardBinding binding;
    //    private CardsTransAdapter adapter;
    private int userId, countryId;
    private String coupBarcode;
    TotalPointModel totalPointModel;
    SettingCouponsModel settingCouponsModel;

    LocalModel localModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.myOrderRecycler.setLayoutManager(linearLayoutManager);

        localModel = UtilityApp.getLocalData();
        countryId = localModel.getCountryId();


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getData();

        });

        binding.generateBut.setOnClickListener(v -> {
            GenerateDialog generateDialog = new GenerateDialog(getActivityy(),userId, totalPointModel.points, settingCouponsModel.minimumPoints, new DataFetcherCallBack() {
                @Override
                public void Result(Object obj, String func, boolean IsSuccess) {

                }
            });
            generateDialog.show();

        });

        binding.noDataLY.noDataLY.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        getData();

    }

    private void getData() {

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
            userId = UtilityApp.getUserData().getId();
            coupBarcode = UtilityApp.getUserData().getLoyalBarcode();
            getTotalPoint();
            getCouponSettings();

            callApi();

        } else {
            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
            checkLoginDialog.show();
        }

    }

    private void callApi() {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.generateBut.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<List<TransactionModel>> result = (ResultAPIModel<List<TransactionModel>>) obj;
            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            String message = getActivity().getString(R.string.fail_to_get_data);
            if (func.equals(Constants.ERROR)) {

                if (result != null && result.message != null) {
                    message = result.message;
                }
                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.generateBut.setVisibility(View.GONE);


            } else if (func.equals(Constants.FAIL)) {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.generateBut.setVisibility(View.GONE);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);
                binding.generateBut.setVisibility(View.GONE);


            }
            if (result.isSuccessful()) {
                binding.dataLY.setVisibility(View.VISIBLE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                binding.generateBut.setVisibility(View.VISIBLE);


                List<TransactionModel> list = result.data;
                initAdapter(list);

            } else {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.generateBut.setVisibility(View.GONE);


            }
        }).getTrans(userId);
    }

    private void initAdapter(List<TransactionModel> list) {

        CardsTransAdapter adapter = new CardsTransAdapter(getActivity(), list, list.size(), (position, categoryModel) -> {

        });
        binding.myOrderRecycler.setAdapter(adapter);


    }


    private void getTotalPoint() {

        totalPointModel = DBFunction.getTotalPoints();

        if (totalPointModel == null) {

            callGetTotalPoints();

        } else {
            callGetTotalPoints();
            setTotalPointsData();
        }
    }

    private void callGetTotalPoints() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<TotalPointModel> result = (ResultAPIModel<TotalPointModel>) obj;

            if (result.isSuccessful()) {

                totalPointModel = result.data;
                DBFunction.setTotalPoints(totalPointModel);

                setTotalPointsData();
            }

        }).getTotalPoint(userId);


    }

    private void setTotalPointsData() {

        binding.totalPointTv.setText(String.valueOf(totalPointModel.points));
        binding.currencyPriceTv.setText(String.valueOf(totalPointModel.value));
        binding.barcodeView.setBarcodeText(coupBarcode);
        binding.textCouponCode.setText(coupBarcode);


    }

    private void getCouponSettings() {

        settingCouponsModel = DBFunction.getCouponSettings();
        callGetCouponSettings();

    }


    private void callGetCouponSettings() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingCouponsModel> result = (ResultAPIModel<SettingCouponsModel>) obj;

            if (result.isSuccessful()) {

                settingCouponsModel = result.data;
                DBFunction.setCouponSettings(settingCouponsModel);

            }

        }).getSettings(countryId);

    }


}