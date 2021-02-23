package com.ramez.shopp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.CategoryProductsActivity;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.SearchActivity;
import com.ramez.shopp.Adapter.CategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCategoryBinding;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class CategoryFragment extends FragmentBase implements CategoryAdapter.OnItemClick {
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    ArrayList<CategoryModel> categoryModelList;
    GridLayoutManager gridLayoutManager;
    LocalModel localModel;
    private FragmentCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    private Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        gridLayoutManager = new GridLayoutManager(getActivityy(), 3);
        binding.catRecycler.setHasFixedSize(true);
        binding.catRecycler.setLayoutManager(gridLayoutManager);
        localModel = UtilityApp.getLocalData();
        activity=getActivity();

        if(UtilityApp.getCategories()!=null&&UtilityApp.getCategories().size()>0){
            categoryModelList=UtilityApp.getCategories();
            initAdapter();

        }
        else {
            getCategories(Integer.parseInt(localModel.getCityId()));

        }

        binding.swipeDataContainer.setOnRefreshListener(() -> {
            binding.swipeDataContainer.setRefreshing(false);
            getCategories(Integer.parseInt(localModel.getCityId()));

        });


        binding.searchBut.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivityy(), SearchActivity.class);
            startActivity(intent);

        });


        binding.barcodeBut.setOnClickListener(view1 -> {

            checkCameraPermission();

        });

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getCategories(Integer.parseInt(localModel.getCityId()));

        });


        return view;
    }

    private void initAdapter() {

        categoryAdapter = new CategoryAdapter(activity, categoryModelList,categoryModelList.size(), this);
        binding.catRecycler.setAdapter(categoryAdapter);

    }


    @Override
    public void onItemClicked(int position, CategoryModel categoryModel) {
        Intent intent = new Intent(getActivityy(), CategoryProductsActivity.class);
        intent.putExtra(Constants.CAT_LIST, categoryModelList);
        intent.putExtra(Constants.SELECTED_POSITION, categoryModelList.get(position).getId());
        intent.putExtra(Constants.position, position);
        intent.putExtra(Constants.CAT_MODEL, categoryModel);
        startActivity(intent);


    }

    public void getCategories(int storeId) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {
                CategoryResultModel result = (CategoryResultModel) obj;
                String message = activity.getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null) {
                        message = result.getMessage();
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
                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            categoryModelList = result.getData();

                            Log.i(TAG, "Log productBestList" + categoryModelList.size());
                            UtilityApp.setCategoriesData(categoryModelList);

                            initAdapter();

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

        }).GetAllCategories(storeId);
    }


    private void checkCameraPermission() {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startScan();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getActivityy(), "" + getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(error -> Toast.makeText(getActivityy(), "" + getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActivityy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivityy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getActivityy(), FullScannerActivity.class);
            startActivity(intent);
        }

    }
}