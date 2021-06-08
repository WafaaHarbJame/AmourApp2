package com.ramez.shopp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramez.shopp.Adapter.CouponsAdapter;
import com.ramez.shopp.Adapter.MyOrdersAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.Models.OrderNewModel;
import com.ramez.shopp.Models.OrdersResultModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCardBinding;
import com.ramez.shopp.databinding.FragmentCouponsBinding;

import java.util.ArrayList;
import java.util.List;


public class CouponsFragment extends  FragmentBase implements CouponsAdapter.OnItemClick {
    List<CouponsModel> list;
    LinearLayoutManager linearLayoutManager;
    private FragmentCouponsBinding binding;
    private CouponsAdapter adapter;
    private int user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCouponsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        list = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.myOrderRecycler.setLayoutManager(linearLayoutManager);
        if(UtilityApp.getUserData()!=null&&UtilityApp.getUserData().getId()!=null){
            user_id = UtilityApp.getUserData().getId();
            list.add(new CouponsModel());
            list.add(new CouponsModel());
            list.add(new CouponsModel());
            list.add(new CouponsModel());

            initAdapter(list);

        }
        else {
            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
            checkLoginDialog.show();
        }



        binding.swipe.setOnRefreshListener(() -> {
            binding.swipe.setRefreshing(false);

        });


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {



        });

        binding.noDataLY.btnBrowseProducts.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });




        return view;
    }




    private void initAdapter(List<CouponsModel> list) {

        adapter = new CouponsAdapter(getActivity(),list,list.size(),this);
        binding.myOrderRecycler.setAdapter(adapter);


    }


    @Override
    public void onItemClicked(int position, CategoryModel categoryModel) {

    }
}