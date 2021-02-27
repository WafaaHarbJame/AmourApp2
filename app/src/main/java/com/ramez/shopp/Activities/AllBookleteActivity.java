package com.ramez.shopp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ramez.shopp.Adapter.BookletAdapter;
import com.ramez.shopp.Adapter.BrandsAdapter;
import com.ramez.shopp.Adapter.ProductCategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrandModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityAllBookleteBinding;
import com.ramez.shopp.databinding.ActivityAllListBinding;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AllBookleteActivity extends ActivityBase implements BookletAdapter.OnBookletClick,BrandsAdapter.OnBrandClick {
    ActivityAllBookleteBinding binding;
    ArrayList<BookletsModel> list;
    GridLayoutManager gridLayoutManager;
    private BookletAdapter adapter;
    private int city_id;
    private MemberModel user;
    private LocalModel localModel;
    private String type = "";
    private BrandsAdapter brandsAdapter;
    ArrayList<BrandModel> brandsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllBookleteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        list = new ArrayList<>();
        brandsList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActiviy(), 2);
        binding.recycler.setLayoutManager(gridLayoutManager);
        binding.recycler.setHasFixedSize(true);

        localModel = UtilityApp.getLocalData();
        user = UtilityApp.getUserData();

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(null);
        city_id = Integer.parseInt(localModel.getCityId());
        setTitle("");

        getIntentExtra();


        binding.swipeDataContainer.setOnRefreshListener(() -> {
            binding.swipeDataContainer.setRefreshing(false);

            if(type.equals(Constants.BOOKLETS)){
                getBooklets(city_id);
            }
            else {
                GetAllBrands(city_id);

            }

        });
        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            if(type.equals(Constants.BOOKLETS)){
                getBooklets(city_id);
            }
            else {
                GetAllBrands(city_id);

            }


        });


    }

    public void initAdapter() {
        adapter = new BookletAdapter(getActiviy(), list, list.size(), this);
        binding.recycler.setAdapter(adapter);

    }


    public void getBooklets(int city_id) {
        list.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<BookletsModel>> result = (ResultAPIModel<ArrayList<BookletsModel>>) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

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
                    if (result.data != null && result.data.size() > 0) {
                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        list = result.data;
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

        }).getBookletsList(city_id);
    }

    public void GetAllBrands(int city_id) {
        list.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<BrandModel>> result = (ResultAPIModel<ArrayList<BrandModel>>) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

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
                    if (result.data != null && result.data.size() > 0) {
                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        ArrayList<BrandModel> allBrandList = result.data;
//                            while (allBrandList.size() > 0) {
//
//                            }
                        for (int i = 0; i < allBrandList.size(); i++) {
                            BrandModel brandModel = result.data.get(i);
                            if (brandModel.getImage() != null && brandModel.getImage2() != null) {
                                brandsList.add(brandModel);
                                allBrandList.remove(i);
                                i--;
                            }

                        }

                        initBrandsAdapter();

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

        }).GetAllBrands(city_id);
    }


    @Override
    public void onBookletClicked(int position, BookletsModel bookletsModel) {
        Intent intent = new Intent(getActiviy(), BrousherActivity.class);
        intent.putExtra(Constants.bookletsModel, bookletsModel);
        startActivity(intent);
    }


    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            type = bundle.getString(Constants.Activity_type);
            if (type.equals(Constants.BOOKLETS)) {
                getBooklets(city_id);
            } else {
                gridLayoutManager.setSpanCount(3);
                GetAllBrands(city_id);

            }

        }
    }


    public void initBrandsAdapter() {
        brandsAdapter = new BrandsAdapter(getActiviy(), brandsList, this);
        binding.recycler.setAdapter(brandsAdapter);

    }

    @Override
    public void onBrandClicked(int position, BrandModel brandModel) {
        Intent intent = new Intent(getActiviy(), AllListActivity.class);
        intent.putExtra(Constants.LIST_MODEL_NAME,getString(R.string.Brands));
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter);
        intent.putExtra(Constants.brand_id, brandModel.getId());
        startActivity(intent);

    }
}