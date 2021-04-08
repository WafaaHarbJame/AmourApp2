package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ramez.shopp.Adapter.ProductCategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityAllListBinding;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AllListActivity extends ActivityBase implements ProductCategoryAdapter.OnItemClick {

    ActivityAllListBinding binding;
    ArrayList<ProductModel> list;
    GridLayoutManager gridLayoutManager;
    String name = "";
    private ProductCategoryAdapter adapter;
    private int category_id = 0, country_id, city_id;
    private String user_id = "0", filter = "";
    private MemberModel user;
    private LocalModel localModel;
    private int brand_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        list = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActiviy(), 2);
        binding.recycler.setLayoutManager(gridLayoutManager);
        binding.recycler.setHasFixedSize(true);

        localModel = UtilityApp.getLocalData();
        user = UtilityApp.getUserData();

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(null);

        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

        if (UtilityApp.isLogin()) {
            user_id = String.valueOf(user.getId());

        }

        getIntentExtra();


        binding.swipeDataContainer.setOnRefreshListener(() -> {
            binding.swipeDataContainer.setRefreshing(false);
            getProductList(0, country_id, city_id, user_id, filter, brand_id, 0, 10);


        });
        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getProductList(0, country_id, city_id, user_id, filter, brand_id, 0, 10);


        });


    }

    public void initAdapter() {
        adapter = new ProductCategoryAdapter(getActiviy(), binding.recycler, list, 0, country_id, city_id, user_id, list.size(), filter, this, Constants.twoRow,brand_id);
        binding.recycler.setAdapter(adapter);
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
            name = bundle.getString(Constants.LIST_MODEL_NAME);
            filter = bundle.getString(Constants.FILTER_NAME);
            brand_id = bundle.getInt(Constants.brand_id);
            setTitle(name);
            getProductList(0, country_id, city_id, user_id, filter, brand_id, 0, 10);


        }
    }


    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size) {
        list.clear();
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
                        list = result.getData();
                        Log.i(TAG, "Log productList" + list.size());
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

        }).getFavorite(category_id, country_id, city_id, user_id, filter, brand_id, page_number, page_size);
    }
}