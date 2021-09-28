package com.ramez.shopp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ramez.shopp.adapter.BookletAdapter;
import com.ramez.shopp.adapter.BrandsAdapter;
import com.ramez.shopp.adapter.KitchenAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrandModel;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityAllBookleteBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllBookleteActivity extends ActivityBase implements BookletAdapter.OnBookletClick, BrandsAdapter.OnBrandClick, KitchenAdapter.OnKitchenClick {
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
    private List<DinnerModel> dinnerModelList;
    private String lang;
    private Boolean isNotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllBookleteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        lang = UtilityApp.getLanguage() == null ? Locale.getDefault().getLanguage() : UtilityApp.getLanguage();


        list = new ArrayList<>();
        dinnerModelList = new ArrayList<>();
        brandsList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActiviy(), 2);
        binding.recycler.setLayoutManager(gridLayoutManager);
        binding.recycler.setHasFixedSize(true);

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());
        user = UtilityApp.getUserData();

        binding.recycler.setItemAnimator(null);
        if (localModel != null) {
            city_id = Integer.parseInt(localModel.getCityId());

        }

        setTitle("");


        getIntentExtra();


        binding.swipeDataContainer.setOnRefreshListener(() -> {

            binding.swipeDataContainer.setRefreshing(false);

            if (type.equals(Constants.BOOKLETS)) {
                getBooklets(city_id);
            } else if (type.equals(Constants.DINNERS)) {
                getDinners(lang);
            } else {
                try {
                    gridLayoutManager.setSpanCount(3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                GetAllBrands(city_id);

            }


        });
        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            if (type.equals(Constants.BOOKLETS)) {
                getBooklets(city_id);
            } else {
                GetAllBrands(city_id);

            }


        });


    }

    @Override
    public void onBackPressed() {
        System.out.println("Log onBackPressed " + isNotify);
        if (isNotify) {
            Intent intent = new Intent(getActiviy(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }

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
        brandsList.clear();
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
                            if (brandModel.getImage() != null || brandModel.getImage2() != null) {
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

    public void getDinners(String lang) {
        dinnerModelList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<DinnerModel>> result = (ResultAPIModel<ArrayList<DinnerModel>>) obj;
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
                        dinnerModelList = result.data;
                        initKitchenAdapter();

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

        }).getDinnersList(lang);
    }


    @Override
    public void onBookletClicked(int position, BookletsModel bookletsModel) {

//        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_BROUSHERS, true));
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.bookletsModel, bookletsModel);
//        specialOfferFragment.setArguments(bundle);
//        fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();


        Intent intent = new Intent(getActiviy(), MainActivity.class);
        intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE);
        bookletsModel.setStoreID(city_id);
        intent.putExtra(Constants.bookletsModel, bookletsModel);
        intent.putExtra(Constants.Inside_app, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            type = bundle.getString(Constants.Activity_type);
            isNotify = bundle.getBoolean(Constants.isNotify,false);

            if (type.equals(Constants.BOOKLETS)) {
                getBooklets(city_id);
            } else if (type.equals(Constants.DINNERS)) {
                getDinners(lang);
            } else {
                gridLayoutManager.setSpanCount(3);
                GetAllBrands(city_id);
            }

        }
    }


    @Override
    public void onBrandClicked(int position, BrandModel brandModel) {
        Intent intent = new Intent(getActiviy(), AllListActivity.class);
        intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands));
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter);
        intent.putExtra(Constants.brand_id, brandModel.getId());
        startActivity(intent);

    }

    private void initKitchenAdapter() {
        KitchenAdapter kitchenAdapter = new KitchenAdapter(getActiviy(), dinnerModelList, this, true, 0);
        binding.recycler.setAdapter(kitchenAdapter);

    }

    public void initAdapter() {
        adapter = new BookletAdapter(getActiviy(), list, list.size(), this);
        binding.recycler.setAdapter(adapter);

    }

    public void initBrandsAdapter() {
        brandsAdapter = new BrandsAdapter(getActiviy(), brandsList, this, 0);
        binding.recycler.setAdapter(brandsAdapter);

    }

    @Override
    public void onKitchenClicked(int position, DinnerModel dinnerModel) {
        Intent intent = new Intent(getActiviy(), RamezKitchenActivity.class);
        intent.putExtra(Constants.DB_DINNER_MODEL, dinnerModel);
        startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {


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