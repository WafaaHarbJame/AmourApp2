package com.ramez.shopp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Adapter.MainCategoryAdapter;
import com.ramez.shopp.Adapter.ProductCategoryAdapter;
import com.ramez.shopp.Adapter.SubCategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.AutoCompleteModel;
import com.ramez.shopp.Models.ChildCat;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCategoryProductsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class CategoryProductsFragment extends FragmentBase implements ProductCategoryAdapter.OnItemClick, MainCategoryAdapter.OnMainCategoryItemClicked {
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    ArrayList<ProductModel> productList;
    ArrayList<AutoCompleteModel> data = null;
    ArrayList<String> autoCompleteList;
    ArrayList<CategoryModel> mainCategoryDMS;
    GridLayoutManager gridLayoutManager;
    int numColumn = 2;
    int selectedSubCat = 0;
    int category_id = 0, country_id, city_id;
    private FragmentCategoryProductsBinding binding;
    private String user_id = "0", filter = "";
    private MemberModel user;
    private LocalModel localModel;
    private ProductCategoryAdapter adapter;
    private int SEARCH_CODE = 2000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false);

        productList = new ArrayList<>();
        mainCategoryDMS = new ArrayList<>();


        gridLayoutManager = new GridLayoutManager(getActivityy(), numColumn);
        binding.productsRv.setLayoutManager(gridLayoutManager);


        binding.listShopCategories.setLayoutManager(new LinearLayoutManager(getActivityy(), LinearLayoutManager.HORIZONTAL, false));

        binding.listSubCategory.setLayoutManager(new LinearLayoutManager(getActivityy(), LinearLayoutManager.HORIZONTAL, false));


        binding.productsRv.setHasFixedSize(true);
        binding.productsRv.setItemAnimator(null);

        data = new ArrayList<>();
        autoCompleteList = new ArrayList<>();

        localModel = UtilityApp.getLocalData();
        country_id = localModel.getCountryId();


        if (UtilityApp.isLogin()) {

            user = UtilityApp.getUserData();
            user_id = String.valueOf(user.getId());

        }
        city_id = Integer.parseInt(localModel.getCityId());

        getIntentExtra();

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);

        });


        binding.searchBut.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_search));
            FragmentManager fragmentManager = getParentFragmentManager();
            SearchFragment searchFragment = new SearchFragment();
            fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commit();


        });


        binding.priceBut.setOnClickListener(view1 -> {
            Collections.sort(productList, Collections.reverseOrder());

        });


        binding.barcodeBut.setOnClickListener(view1 -> {

            checkCameraPermission();

        });


        binding.categoriesCountTv.setText(String.valueOf(productList.size()));

        binding.offerCountTv.setText(String.valueOf(productList.size()));

        return binding.getRoot();

    }

    public void initAdapter() {
        adapter = new ProductCategoryAdapter(getActivityy(), binding.productsRv, productList, selectedSubCat, country_id, city_id, user_id, 0, "", this, numColumn,0);
        binding.productsRv.setAdapter(adapter);

        binding.categoriesCountTv.setText(String.valueOf(productList.size()));
        binding.offerCountTv.setText(String.valueOf(productList.size()));
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);


    }

    private void cancelAPiCall() {
        if (adapter != null && adapter.apiCall != null && adapter.apiCall.isExecuted()) {
            adapter.isCanceled = true;
            adapter.apiCall.cancel();
        }
    }

    private void getIntentExtra() {
        Bundle bundle = getArguments();
        Log.i(CategoryProductsFragment.class.getName(), "Log getIntentExtra ");

        if (bundle != null) {

            mainCategoryDMS = (ArrayList<CategoryModel>) bundle.getSerializable(Constants.CAT_LIST);
            category_id = bundle.getInt(Constants.SELECTED_POSITION, 0);
            CategoryModel categoryModel = (CategoryModel) bundle.getSerializable(Constants.CAT_MODEL);
            int position = bundle.getInt(Constants.position, 0);
            Log.i(CategoryProductsFragment.class.getName(), "Log mainCategoryDMS " + mainCategoryDMS.size());
            Log.i(CategoryProductsFragment.class.getName(), "Log category_id " + category_id);
            Log.i(CategoryProductsFragment.class.getName(), "Log categoryModel " + categoryModel.getHName());

            selectedSubCat = categoryModel.getId();
            initMainCategoryAdapter();

            initSubCatList(mainCategoryDMS.get(position).getChildCat());

            getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);

        }
    }

    private void initSubCatList(ArrayList<ChildCat> subCatList) {
        ChildCat childCat = new ChildCat();
        childCat.setId(selectedSubCat);
        childCat.setHName(getString(R.string.all));
        childCat.setName(getString(R.string.all));

     //   selectedSubCat = category_id;
        ArrayList<ChildCat> subCategoryDMS = new ArrayList<>(subCatList);

        subCategoryDMS.add(0, childCat);

        initSubCategoryAdapter(subCategoryDMS);
    }

    private void initSubCategoryAdapter(ArrayList<ChildCat> subCategoryDMS) {

        SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(getActivityy(), subCategoryDMS, object -> {

            selectedSubCat = object.getId();
            cancelAPiCall();
            getProductList(selectedSubCat, country_id, city_id, user_id, "", 0, 10);

        }, selectedSubCat);

        binding.listSubCategory.setAdapter(subCategoryAdapter);

    }

    private void initMainCategoryAdapter() {
        MainCategoryAdapter mainCategoryShopAdapter = new MainCategoryAdapter(getActivityy(), mainCategoryDMS, this, selectedSubCat);
        binding.listShopCategories.setAdapter(mainCategoryShopAdapter);
    }


    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {
//        productList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.productsRv.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                FavouriteResultModel result = (FavouriteResultModel) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
                    binding.productsRv.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                    binding.productsRv.setVisibility(View.GONE);

                } else {
                    if (IsSuccess) {
                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.productsRv.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productList = new ArrayList<>(result.getData());
                            initAdapter();

                        } else {

                            binding.productsRv.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                        }


                    } else {

                        binding.productsRv.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }

            }

        }).getFavorite(category_id, country_id, city_id, user_id, filter, 0, page_number, page_size);

    }

    @Override
    public void OnMainCategoryItemClicked(CategoryModel mainCategoryDM, int position) {

        selectedSubCat = mainCategoryDM.getId();
        initSubCatList(mainCategoryDM.getChildCat());

        cancelAPiCall();

        getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);


    }


    private void checkCameraPermission() {
        Dexter.withContext(getActivityy()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
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
            startActivityForResult(intent, SEARCH_CODE);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {
        if (event.type.equals(MessageEvent.TYPE_view)) {
            numColumn = (int) event.data;
            initAdapter();
            gridLayoutManager.setSpanCount(numColumn);
            adapter.notifyDataSetChanged();


        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {

                if (data != null) {
                    boolean SEARCH_BY_CODE_byCode = data.getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false);
                    String CODE = data.getStringExtra(Constants.CODE);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CODE, CODE);
                    bundle.putBoolean(Constants.SEARCH_BY_CODE_byCode, SEARCH_BY_CODE_byCode);
                    searchFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commitAllowingStateLoss();

                }


            }

        }
    }
}