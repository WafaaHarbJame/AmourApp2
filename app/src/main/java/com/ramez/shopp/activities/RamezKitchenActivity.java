package com.ramez.shopp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.adapter.RecipeAdapter;
import com.ramez.shopp.adapter.RecipeSliderAdapter;
import com.ramez.shopp.adapter.SuggestedProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.Recipe;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;
import com.ramez.shopp.Models.SingleDinnerModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityRamezKitchenBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RamezKitchenActivity extends ActivityBase implements SuggestedProductAdapter.OnItemClick {
    ActivityRamezKitchenBinding binding;
    int user_id = 0;
    ArrayList<String> sliderList;
    ArrayList<ProductModel> productList;
    ArrayList<ReviewModel> reviewList;
    String productName = "";
    SingleDinnerModel dinnerModel;
    String currency;
    private int country_id, city_id;
    private RecipeSliderAdapter sliderAdapter;
    private LinearLayoutManager productLayoutManager;
    private int storeId;
    private int dinner_id;
    private String lang;
    List<Recipe> recipes;
    private LocalModel localModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRamezKitchenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MemberModel memberModel = UtilityApp.getUserData();

        if (UtilityApp.getLanguage() != null) {
            lang = UtilityApp.getLanguage();
        } else {
            lang = Locale.getDefault().getLanguage();
        }
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());

        country_id =localModel.getCountryId();

        city_id = Integer.parseInt(localModel.getCityId());
        sliderList = new ArrayList<>();
        productList = new ArrayList<>();
        reviewList = new ArrayList<>();
        recipes = new ArrayList<>();
        storeId = Integer.parseInt(localModel.getCityId());
        currency = localModel.getCurrencyCode();

        productLayoutManager = new LinearLayoutManager(getActiviy(), RecyclerView.HORIZONTAL, false);
        binding.offerRecycler.setLayoutManager(productLayoutManager);
        binding.offerRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActiviy());
        binding.recipeRecycler.setLayoutManager(linearLayoutManager);
        binding.recipeRecycler.setHasFixedSize(true);


        binding.offerRecycler.setItemAnimator(null);


        binding.processCartBut.setOnClickListener(v -> {
            Intent intent = new Intent(getActiviy(), MainActivity.class);
            intent.putExtra(Constants.CART, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActiviy(), MainActivity.class);
            intent.putExtra(Constants.CART, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        getIntentExtra();

        if (UtilityApp.isLogin()) {
            binding.fabCart.setVisibility(View.VISIBLE);
            user_id = Integer.parseInt(String.valueOf(memberModel.getId()));
            binding.tVCartSize.setText(String.valueOf(UtilityApp.getCartCount()));

        } else {
            binding.fabCart.setVisibility(View.GONE);

        }

        binding.productDescTv.setExpandInterpolator(new OvershootInterpolator());
        binding.productDescTv.setCollapseInterpolator(new OvershootInterpolator());

        getSingleDinner(dinner_id, lang);

        initListener();

    }

    private void initListener() {

        binding.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });


        binding.showAllBut.setOnClickListener(v -> {

            binding.showAllBut.setText(binding.productDescTv.isExpanded() ? R.string.ShowAll : R.string.Show_less);
            binding.productDescTv.toggle();

        });
    }

    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            DinnerModel dinnerModel = (DinnerModel) bundle.getSerializable(Constants.DB_DINNER_MODEL);
            if (dinnerModel != null) {
                dinner_id = dinnerModel.getId();
                productName = dinnerModel.getDescription();
                binding.productNameTv.setText(productName);
                binding.mainTitleTv.setText(productName);
            }


        }


    }

    public void initAdapter() {

        sliderAdapter = new RecipeSliderAdapter(this, sliderList);
        binding.viewPager.setAdapter(sliderAdapter);
    }


    public void initRecipeAdapter() {

        RecipeAdapter recipeAdapter = new RecipeAdapter(this, recipes, 0, (obj, func, IsSuccess) -> {

        });
        binding.recipeRecycler.setAdapter(recipeAdapter);
    }


    @Override
    public void onItemClicked(int position, ProductModel productModel) {

    }

    @SuppressLint("SetTextI18n")
    public void getSingleDinner(int dinner_id, String lan) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
        binding.processCartBut.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SingleDinnerModel> result = (ResultAPIModel<SingleDinnerModel>) obj;
            binding.processCartBut.setVisibility(View.VISIBLE);

            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result != null) {
                    message = result.message;
                }
                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.processCartBut.setVisibility(View.GONE);


            } else if (func.equals(Constants.FAIL)) {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.processCartBut.setVisibility(View.GONE);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);
                binding.processCartBut.setVisibility(View.GONE);


            } else {
                if (IsSuccess) {
                    if (result.data != null && result.data.getRecipes().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        dinnerModel = result.data;
                        recipes = dinnerModel.getRecipes();

                        if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
                            productName = dinnerModel.getDescription();

                        } else {
                            productName = dinnerModel.getDescription();

                        }

                        binding.productNameTv.setText(productName);
                        binding.mainTitleTv.setText(productName);

                        if (dinnerModel.getFullDescription() != null && dinnerModel.getDescription() != null) {

//                            if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                                binding.productDescTv.setText(Html.fromHtml(dinnerModel.getFullDescription().toString()));
//
//                            } else {
//                                binding.productDescTv.setText(Html.fromHtml(dinnerModel.getFullDescription().toString()));
//
//                            }

                            binding.productDescTv.setText(Html.fromHtml(dinnerModel.getFullDescription()));

                        }


                        sliderList = result.data.getImages();


                        initAdapter();
                        initRecipeAdapter();

                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                        binding.processCartBut.setVisibility(View.GONE);

                    }


                } else {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);
                    binding.processCartBut.setVisibility(View.GONE);


                }
            }

        }).getSingleDinner(dinner_id, lan);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {

        if (event.type.equals(MessageEvent.TYPE_main)) {
            binding.backBtn.setOnClickListener(view -> {
                Intent intent = new Intent(getActiviy(), MainActivity.class);
                startActivity(intent);
            });

        } else if (event.type.equals(MessageEvent.TYPE_READ_CART)) {

            if (UtilityApp.isLogin()) {
                binding.tVCartSize.setText(String.valueOf(UtilityApp.getCartCount()));
            }
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            DinnerModel dinnerModel = (DinnerModel) bundle.getSerializable(Constants.DB_DINNER_MODEL);


            if (dinnerModel != null) {
                dinner_id = dinnerModel.getId();

            }

            if (dinnerModel != null & dinnerModel.getDescription() != null) {
                productName = dinnerModel.getDescription();

            }


            binding.productNameTv.setText(productName);

            getSingleDinner(dinner_id, lang);


            binding.productNameTv.setText(productName);

            binding.mainTitleTv.setText(productName);


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


}