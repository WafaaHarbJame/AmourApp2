package com.ramez.shopp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.request.ProductRequest;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.activities.ProductDetailsActivity;
import com.ramez.shopp.adapter.MainCategoryAdapter;
import com.ramez.shopp.adapter.OfferProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Models.CategoryModel;
import com.ramez.shopp.classes.MessageEvent;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentOfferBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OfferFragment extends FragmentBase implements OfferProductAdapter.OnItemClick, MainCategoryAdapter.OnMainCategoryItemClicked {
    private FragmentOfferBinding binding;
    List<ProductModel> productOffersList;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    int category_id = 0, country_id, city_id;
    ArrayList<CategoryModel> mainCategoryDMS;
    private LocalModel localModel;

    private MainCategoryAdapter categoryAdapter;
    private OfferProductAdapter productOfferAdapter;
    private int kind_id = 0;
    private String sortType = "";
    int numColumn = 2;

    String user_id = "0";
    private int brand_id;
     private ProductRequest productRequest ;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOfferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        productOffersList = new ArrayList<>();

        mainCategoryDMS = new ArrayList<>();


        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());

        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());


        linearLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        binding.catRecycler.setHasFixedSize(true);
        binding.catRecycler.setLayoutManager(linearLayoutManager);

        if (UtilityApp.isLogin()) {
            MemberModel memberModel = UtilityApp.getUserData();
            if (memberModel != null && memberModel.getId() != null) {
                user_id = String.valueOf(memberModel.getId());

            }

        }


        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

        gridLayoutManager = new GridLayoutManager(getActivityy(), numColumn);
        binding.offerRecycler.setLayoutManager(gridLayoutManager);

        binding.offerRecycler.setHasFixedSize(true);
        binding.offerRecycler.setItemAnimator(null);


        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
            mainCategoryDMS = UtilityApp.getCategories();

            mainCategoryDMS.add(new CategoryModel(0));
            CategoryModel all = new CategoryModel();
            all.setId(0);
            all.sethName(getString(R.string.all));
            all.setName(getString(R.string.all));
            mainCategoryDMS.add(0, all);


            initCateAdapter();
        } else {
            getCategories(Integer.parseInt(localModel.getCityId()));

        }

        getProductsList();

        binding.dataLY.setOnRefreshListener(this::getProductsList);

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getProductsList();
        });


        return view;
    }

    public void initAdapter() {

//        binding.offerRecycler.invalidate();
//        productOfferAdapter = null;
        productOfferAdapter = new OfferProductAdapter(getActivityy(), productOffersList, category_id, 0, country_id, city_id, user_id,
                productOffersList.size(), binding.offerRecycler, Constants.offered_filter, this, (obj, func, IsSuccess) -> {

        }, numColumn);
        binding.offerRecycler.setAdapter(productOfferAdapter);
//        productOfferAdapter.setAdapterData(productOffersList, category_id, 0, country_id, city_id, user_id, Constants.offered_filter, numColumn);
//        productOfferAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        Log.i(getClass().getSimpleName(), "Log offer  IsSpecial  " + productModel.getFirstProductBarcodes().isSpecial());
        Log.i(getClass().getSimpleName(), "Log offer SpecialPrice  " + productModel.getFirstProductBarcodes().getSpecialPrice());
        Log.i(getClass().getSimpleName(), "Log offer  price  " + productModel.getFirstProductBarcodes().getPrice());
        Log.i(getClass().getSimpleName(), "Log offer Id  " + productModel.getFirstProductBarcodes().getId());


        startActivity(intent);


    }

    public void getOfferList(ProductRequest  productRequest) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
        productOffersList.clear();

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                FavouriteResultModel result = (FavouriteResultModel) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
                if (binding.dataLY.isRefreshing())
                    binding.dataLY.setRefreshing(false);

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
                            productOffersList = result.getData();

                            initAdapter();
//                            if (productOfferAdapter != null) {
////                                if (category_id != 0){
////                                productOffersList = productOffersList.subList(0, 3);
////                                }
//                                productOfferAdapter.setAdapterData(productOffersList, category_id, 0, country_id, city_id, user_id, Constants.offered_filter, numColumn);
//                                productOfferAdapter.notifyDataSetChanged();
//                                return;
//                            }


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


        }).getProductList(productRequest);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {
        if (event.type.equals(MessageEvent.TYPE_SORT)) {
            //getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);

        } else if (event.type.equals(MessageEvent.TYPE_view)) {
            numColumn = (int) event.data;
            try {
                gridLayoutManager.setSpanCount(numColumn);
                binding.offerRecycler.setLayoutManager(gridLayoutManager);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (productOfferAdapter != null)
                productOfferAdapter.notifyDataSetChanged();

//            initAdapter();

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


    public void getCategories(int storeId) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {
                CategoryResultModel result = (CategoryResultModel) obj;
                String message = getActivityy().getString(R.string.fail_to_get_data);

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
                        if (result!=null && result.getData() != null && result.getData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            mainCategoryDMS = result.getData();

                            Log.i(TAG, "Log productBestList" + mainCategoryDMS.size());
                            UtilityApp.setCategoriesData(mainCategoryDMS);

                            initCateAdapter();

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

    private void initCateAdapter() {

        categoryAdapter = new MainCategoryAdapter(getActivityy(), mainCategoryDMS, this, category_id);
        binding.catRecycler.setAdapter(categoryAdapter);

    }


    @Override
    public void OnMainCategoryItemClicked(CategoryModel categoryModel, int position) {
        category_id = categoryModel.getId();
        Log.i(TAG, "Log MainItem category_id " + category_id);
        getProductsList();
    }

    private void getProductsList(){
        productRequest=new ProductRequest(category_id, country_id, city_id, Constants.offered_filter, brand_id, 0, 10, kind_id, null, null);
        getOfferList(productRequest);

    }


}




