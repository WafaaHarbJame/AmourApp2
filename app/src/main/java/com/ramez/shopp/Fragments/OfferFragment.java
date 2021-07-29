package com.ramez.shopp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Adapter.MainCategoryAdapter;
import com.ramez.shopp.Adapter.OfferProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.ChildCat;
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

    int numColumn = 2;

    String user_id = "0";
    private int brand_id;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOfferBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        productOffersList = new ArrayList<>();

        mainCategoryDMS = new ArrayList<>();


        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());

        country_id = UtilityApp.getLocalData() != null && UtilityApp.getLocalData().getCountryId() != null ?
                UtilityApp.getLocalData().getCountryId() : UtilityApp.getDefaultLocalData(getActivityy()).getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData() != null && UtilityApp.getLocalData().getCityId() != null ?
                UtilityApp.getLocalData().getCityId() : UtilityApp.getDefaultLocalData(getActivityy()).getCityId());



        linearLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        binding.catRecycler.setHasFixedSize(true);
        binding.catRecycler.setLayoutManager(linearLayoutManager);

        if (UtilityApp.isLogin()) {
            MemberModel memberModel = UtilityApp.getUserData();
            if(memberModel!=null&&memberModel.getId()!=null){
                user_id = String.valueOf(memberModel.getId());

            }

        }



        country_id = UtilityApp.getLocalData().getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());

        gridLayoutManager = new GridLayoutManager(getActivityy(), numColumn);
        binding.offerRecycler.setLayoutManager(gridLayoutManager);

        binding.offerRecycler.setHasFixedSize(true);
        binding.offerRecycler.setItemAnimator(null);


        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
            mainCategoryDMS = UtilityApp.getCategories();

            mainCategoryDMS.add(new CategoryModel(0));
            CategoryModel all=new CategoryModel();
            all.setId(0);
            all.setHName(getString(R.string.all));
            all.setName(getString(R.string.all));
            mainCategoryDMS.add(0, all);


            initCateAdapter();
        } else {
            getCategories(Integer.parseInt(localModel.getCityId()));

        }

        getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);

        binding.dataLY.setOnRefreshListener(() -> {
//            binding.dataLY.setRefreshing(false);
            getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);


        });

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);
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
        startActivity(intent);


    }

    public void getOfferList(int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size) {
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


        }).getFavorite(category_id, country_id, city_id, user_id, filter, brand_id, page_number, page_size);
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
                        if (result.getData() != null && result.getData().size() > 0) {

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

//        CategoryModel categoryModel = new CategoryModel();
//        categoryModel.setId(category_id);
//        categoryModel.setHName(getString(R.string.all));
//        categoryModel.setName(getString(R.string.all));
//        categoryModel.setImage("https://www.vskills.in/certification/blog/wp-content/uploads/2015/04/2_The-cons-of-the-word-%E2%80%9CALL%E2%80%9D.gif");
//        categoryModel.setImage2("https://www.vskills.in/certification/blog/wp-content/uploads/2015/04/2_The-cons-of-the-word-%E2%80%9CALL%E2%80%9D.gif");
//        ArrayList<CategoryModel> allCategoryDMS = new ArrayList<>(mainCategoryDMS);
//        allCategoryDMS.add(0, categoryModel);

        categoryAdapter = new MainCategoryAdapter(getActivityy(), mainCategoryDMS, this, category_id);
        binding.catRecycler.setAdapter(categoryAdapter);

    }


    @Override
    public void OnMainCategoryItemClicked(CategoryModel categoryModel, int position) {
        category_id = categoryModel.getId();
        Log.i(TAG, "Log MainItem category_id " + category_id);
//        if (productOfferAdapter != null)
//            productOfferAdapter.categoryId = category_id;
        getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);

    }
}




