package com.ramez.shopp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Adapter.MainCategoryAdapter;
import com.ramez.shopp.Adapter.ProductCategoryAdapter;
import com.ramez.shopp.Adapter.SubCategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.AutoCompleteModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.ChildCat;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityCategoryProductsActivityBinding;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class CategoryProductsActivity extends ActivityBase implements ProductCategoryAdapter.OnItemClick, MainCategoryAdapter.OnMainCategoryItemClicked {

    private static final int ZBAR_CAMERA_PERMISSION = 1;
    ActivityCategoryProductsActivityBinding binding;
    ArrayList<ProductModel> productList;
    ArrayList<AutoCompleteModel> data = null;
    ArrayList<String> autoCompleteList;
    ArrayList<CategoryModel> mainCategoryDMS;
    ArrayList<ChildCat> subCategoryDMS = new ArrayList<>();
    GridLayoutManager gridLayoutManager;
    int numColumn = 2;
    int selectedSubCat = 0;
    int category_id = 0, country_id, city_id;
    private String user_id = "0", filter = "";
    private MemberModel user;
    private LocalModel localModel;
    private ProductCategoryAdapter adapter;
    private CheckLoginDialog checkLoginDialog;
    private boolean toggleButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryProductsActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        productList = new ArrayList<>();
        subCategoryDMS = new ArrayList<>();
        mainCategoryDMS = new ArrayList<>();


        gridLayoutManager = new GridLayoutManager(getActiviy(), numColumn);
        binding.recycler.setLayoutManager(gridLayoutManager);
        setTitle("");


        binding.listShopCategories.setLayoutManager(new LinearLayoutManager(getActiviy(), LinearLayoutManager.HORIZONTAL, false));

        binding.listSubCategory.setLayoutManager(new LinearLayoutManager(getActiviy(), LinearLayoutManager.HORIZONTAL, false));


        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(null);

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
            getCategories(city_id,0);
         //   getProductList(category_id, country_id, city_id, user_id, filter, 0, 10);

        });


        binding.searchBut.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActiviy(), SearchActivity.class);
            startActivity(intent);

        });



        binding.view2But.setOnClickListener(view1 -> {


            toggleButton = !toggleButton;

            if (toggleButton) {
                numColumn = 1;
                initAdapter();
                gridLayoutManager.setSpanCount(numColumn);
                binding.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(),R.drawable.filter_view1));

            } else {
                numColumn = 2;
                gridLayoutManager.setSpanCount(numColumn);
                initAdapter();
                binding.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(),R.drawable.filter_view2));

            }
            adapter.notifyDataSetChanged();


        });



        binding.priceBut.setOnClickListener(view1 -> {
            Collections.sort(productList, Collections.reverseOrder());

        });


        binding.barcodeBut.setOnClickListener(view1 -> {

            checkCameraPermission();

        });


        binding.categoriesCountTv.setText(String.valueOf(productList.size()));

        binding.offerCountTv.setText(String.valueOf(productList.size()));

    }

    public void initAdapter() {

        adapter = new ProductCategoryAdapter(getActiviy(), productList, category_id, selectedSubCat, country_id, city_id, user_id, 0, binding.recycler, "", this, numColumn);
        binding.recycler.setAdapter(adapter);

        binding.categoriesCountTv.setText(String.valueOf(productList.size()));
        binding.offerCountTv.setText(String.valueOf(productList.size()));
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);


    }


    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            mainCategoryDMS = (ArrayList<CategoryModel>) bundle.getSerializable(Constants.CAT_LIST);
            category_id = bundle.getInt(Constants.SELECTED_POSITION, 0);
            CategoryModel categoryModel = (CategoryModel) bundle.getSerializable(Constants.CAT_MODEL);
            int position = bundle.getInt(Constants.position, 0);

            ChildCat childCat = new ChildCat();
            childCat.setId(0);
            childCat.setHName(getString(R.string.all));
            childCat.setName(getString(R.string.all));

            subCategoryDMS = mainCategoryDMS.get(position).getChildCat();
            category_id = categoryModel.getId();

            subCategoryDMS.add(0, childCat);

            initMainCategoryAdapter();

            initSubCategoryAdapter();

            getProductList(category_id, country_id, city_id, user_id, filter, 0, 10);

        }
    }


    private void initSubCategoryAdapter() {

        SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(getActiviy(), subCategoryDMS, object -> {

            category_id = object.getId();
            getProductList(category_id, country_id, city_id, user_id, "", 0, 10);


        }, selectedSubCat);

        binding.listSubCategory.setAdapter(subCategoryAdapter);

    }

    private void initMainCategoryAdapter() {
        MainCategoryAdapter mainCategoryShopAdapter = new MainCategoryAdapter(getActiviy(), mainCategoryDMS, this, category_id);
        binding.listShopCategories.setAdapter(mainCategoryShopAdapter);
    }


    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {
        productList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            FavouriteResultModel result = (FavouriteResultModel) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result!=null&&result.getMessage() != null) {
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


            }

            else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);

            }

            else {
                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        productList = result.getData();
                        Log.i(TAG, "Log productList" + productList.size());
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

        }).getCatProductList(category_id, country_id, city_id, user_id, filter, page_number, page_size);
    }

    @Override
    public void OnMainCategoryItemClicked(CategoryModel mainCategoryDM, int position) {

        subCategoryDMS.clear();
        category_id = mainCategoryDM.getId();
        getCategories(city_id,position);


    }


    private void checkCameraPermission() {
        Dexter.withContext(getActiviy()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startScan();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getActiviy(), "" + getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getActiviy(), "" + getString(R.string.error_in_data), Toast.LENGTH_SHORT).show();

            }
        }).onSameThread().check();
    }


    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActiviy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActiviy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getActiviy(), FullScannerActivity.class);
            startActivity(intent);
        }

    }


    public void getCategories(int storeId,int position) {

        subCategoryDMS.clear();
        mainCategoryDMS.clear();

        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.searchLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            CategoryResultModel result = (CategoryResultModel) obj;
            String message = getString(R.string.fail_to_get_data);

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

                            binding.searchLY.setVisibility(View.VISIBLE);
                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

                            ChildCat childCat = new ChildCat();
                            childCat.setId(0);
                            childCat.setHName(getString(R.string.all));
                            childCat.setName(getString(R.string.all));

                            mainCategoryDMS=result.getData();

                            subCategoryDMS = mainCategoryDMS.get(position).getChildCat();

                            subCategoryDMS.add(0, childCat);

                            if(subCategoryDMS.size()>0){
                                selectedSubCat = mainCategoryDMS.get(position).getChildCat().get(0).getId();

                            }

                            initMainCategoryAdapter();
                            initSubCategoryAdapter();
                            getProductList(category_id, country_id, city_id, user_id, "", 0, 10);


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


        }).GetAllCategories(storeId);
    }
}
