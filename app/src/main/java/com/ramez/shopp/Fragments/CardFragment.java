package com.ramez.shopp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Adapter.CardsTransAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.GenerateDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.TransactionModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCardBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CardFragment extends FragmentBase {

    private FragmentCardBinding binding;
    //    private CardsTransAdapter adapter;
    private int user_id;


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


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getData();

        });

        binding.generateBut.setOnClickListener(v -> {
            GenerateDialog generateDialog = new GenerateDialog(getActivityy(), R.string.Generate_Coupons, R.string.is_Active, R.string.ok, R.string.cancel, null, null);
            generateDialog.show();

        });

        binding.noDataLY.btnBrowseProducts.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        getData();

    }

    private void getData() {

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
            user_id = UtilityApp.getUserData().getId();

//            List<TransactionModel> list = new ArrayList<>();
//            list.add(new TransactionModel());
//            list.add(new TransactionModel());
//            list.add(new TransactionModel());
//            list.add(new TransactionModel());

//            initAdapter(list);

            callApi();

        } else {
            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
            checkLoginDialog.show();
        }

    }

    private void callApi() {
        new DataFeacher(false, new DataFetcherCallBack() {
            @Override
            public void Result(Object obj, String func, boolean IsSuccess) {
                ResultAPIModel<List<TransactionModel>> result = (ResultAPIModel<List<TransactionModel>>) obj;
                if (result.isSuccessful()) {
                    List<TransactionModel> list = result.data;
                    initAdapter(list);
                } else {

                }
            }
        }).getTrans(user_id);
    }

    private void initAdapter(List<TransactionModel> list) {

        CardsTransAdapter adapter = new CardsTransAdapter(getActivity(), list, list.size(), (position, categoryModel) -> {

        });
        binding.myOrderRecycler.setAdapter(adapter);


    }

}