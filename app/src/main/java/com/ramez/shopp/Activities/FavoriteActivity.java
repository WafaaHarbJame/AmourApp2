package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ramez.shopp.Adapter.FavoriteAdapter;
import com.ramez.shopp.Adapter.OfferProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityFavoriteBinding;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FavoriteActivity extends ActivityBase implements OfferProductAdapter.OnItemClick {
    ActivityFavoriteBinding binding;
    ArrayList<ProductModel> productList;
    private OfferProductAdapter productFavAdapter;
    private GridLayoutManager gridLayoutManager;
    private int category_id = 0, country_id, city_id;
    private String user_id, filter;
    private MemberModel user;
    private LocalModel localModel;
    private int brand_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        user = UtilityApp.getUserData();
        user_id = String.valueOf(UtilityApp.getUserData().getId());


        productList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActiviy(), 2);

        binding.favoriteRecycler.setLayoutManager(gridLayoutManager);
        filter = Constants.favourite_filter;


        localModel = UtilityApp.getLocalData();

        setTitle(R.string.fav_products);

        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

        binding.favoriteRecycler.setHasFixedSize(true);
        binding.favoriteRecycler.setItemAnimator(null);
        getFavoriteProducts(category_id, country_id, city_id, user_id, filter, brand_id,0, 10);

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getFavoriteProducts(category_id, country_id, city_id, user_id, filter,brand_id, 0, 10);

        });

    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
            Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
            intent.putExtra(Constants.DB_productModel, productModel);
            startActivity(intent);



    }

    public void initAdapter() {

        productFavAdapter = new OfferProductAdapter(getActiviy(), productList, 0, 0, country_id, city_id, user_id, productList.size(), binding.favoriteRecycler, filter, this, new DataCallback() {
            @Override
            public void dataResult(Object obj, String func, boolean IsSuccess) {
                int size = (int) obj;

                if (size == 0) {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                    binding.noDataLY.noDataTxt.setText(R.string.no_products_fav);

                }
            }
        });
        binding.favoriteRecycler.setAdapter(productFavAdapter);
    }


    public void getFavoriteProducts(int category_id, int country_id, int city_id, String user_id, String filter,int brand_id, int page_number, int page_size) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            FavouriteResultModel result = (FavouriteResultModel) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result.getMessage() != null) {
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
                        productList = result.getData();
                        Log.i(TAG, "Log getFavoriteProducts " + productList.size());
                        initAdapter();

                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataTxt.setText(R.string.no_products_fav);

                    }


                } else {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);


                }
            }

        }).getFavorite(category_id, country_id, city_id, user_id, filter,brand_id, page_number, page_size);
    }
}