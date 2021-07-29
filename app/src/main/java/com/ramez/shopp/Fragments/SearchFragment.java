package com.ramez.shopp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Adapter.MostSearchAdapter;
import com.ramez.shopp.Adapter.OfferProductAdapter;
import com.ramez.shopp.Adapter.ProductAdapter;
import com.ramez.shopp.Adapter.SearchProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.AutoCompeteResult;
import com.ramez.shopp.Models.AutoCompleteModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.MostSearchModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.ActivityHandler;
import com.ramez.shopp.databinding.SearchFagmentBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class SearchFragment extends FragmentBase implements SearchProductAdapter.OnItemClick, MostSearchAdapter.OnTagClick, ProductAdapter.OnItemClick, OfferProductAdapter.OnItemClick {

    private static final int ZBAR_CAMERA_PERMISSION = 1;
    SearchFagmentBinding binding;
    ArrayList<ProductModel> productList;
    ArrayList<ProductModel> offerList;
    GridLayoutManager gridLayoutManager;
    boolean searchByCode = false;
    int numColumn = 2;
    ArrayList<ProductModel> productOffersList;
    boolean isVisible;
    private ArrayList<AutoCompleteModel> data = null;
    private ArrayList<String> autoCompleteList;
    private SearchProductAdapter adapter;
    private int country_id, city_id;
    private String user_id = "0", filter, result, SearchText, searchQuery;
    private MemberModel user;
    private LocalModel localModel;
    private Call searchCall;
    private Runnable runnable;
    private Handler handler;
    private boolean toggleButton = false;
    private int SEARCH_CODE = 2000;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SearchFagmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        productList = new ArrayList<>();
        offerList = new ArrayList<>();
        data = new ArrayList<>();
        autoCompleteList = new ArrayList<>();
        productOffersList = new ArrayList<>();
        binding.searchEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT);

        binding.searchEt.setFocusable(true);
        binding.searchEt.requestFocusFromTouch();
        binding.searchEt.setThreshold(1);

        gridLayoutManager = new GridLayoutManager(getActivityy(), numColumn);
        binding.recycler.setLayoutManager(gridLayoutManager);

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setItemAnimator(null);

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());

        country_id = UtilityApp.getLocalData() != null && UtilityApp.getLocalData().getCountryId() != null ?
                UtilityApp.getLocalData().getCountryId() : UtilityApp.getDefaultLocalData(getActivityy()).getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData() != null && UtilityApp.getLocalData().getCityId() != null ?
                UtilityApp.getLocalData().getCityId() : UtilityApp.getDefaultLocalData(getActivityy()).getCityId());



        if (UtilityApp.isLogin() && UtilityApp.getUserData()!=null &&UtilityApp.getUserData().getId()!=null) {
            user = UtilityApp.getUserData();
            user_id = String.valueOf(user.getId());

        }


        GridLayoutManager bestOfferGridLayoutManager = new GridLayoutManager(getActivityy(), 2, RecyclerView.VERTICAL, false);
        binding.offerRecycler.setLayoutManager(bestOfferGridLayoutManager);
        binding.offerRecycler.setHasFixedSize(true);


        getIntentExtra();

        GetHomePage();


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            String text = binding.searchEt.getText().toString();
            searchTxt(country_id, city_id, user_id, text, 0, 10);

        });


        binding.barcodeBut.setOnClickListener(view1 -> {
            hideSoftKeyboard(getActivity());
            checkCameraPermission();

        });


        binding.searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                String text = v.getText().toString();
                searchTxt(country_id, city_id, user_id, text, 0, 10);
                ActivityHandler.hideKeyboard(getActivity());
                binding.searchEt.dismissDropDown();

                return true;

            }
            return false;

        });

        handler = new Handler();
        runnable = () -> {
            autoComplete(country_id, city_id, user_id, searchQuery, 0, 10);
        };

        binding.searchEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    binding.closeBtn.setText(R.string.fal_search);
                } else {
                    binding.closeBtn.setText(R.string.fal_times);
                    searchQuery = s.toString();
                    handler.postDelayed(runnable, 500);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (searchCall != null && searchCall.isExecuted()) searchCall.cancel();
                handler.removeCallbacks(runnable);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        binding.searchEt.setOnItemClickListener((adapterView, view12, position, l) -> {
            binding.offerLy.setVisibility(View.GONE);
            String text = autoCompleteList.get(position).toString();
            searchTxt(country_id, city_id, user_id, text, 0, 10);

        });

        binding.closeBtn.setOnClickListener(view1 -> {
            if (binding.closeBtn.getText().equals(getString(R.string.fal_times))) {
                binding.offerLy.setVisibility(View.VISIBLE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                productList.clear();
                binding.searchEt.setText("");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.searchEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT);

    }

    public void initAdapter() {

        adapter = new SearchProductAdapter(getActivityy(), productList, country_id, city_id, user_id, binding.recycler, binding.searchEt.getText().toString(), this, numColumn);
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    public void onPause() {
        isVisible = false;
        super.onPause();
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
        if (searchCall != null && searchCall.isExecuted()) searchCall.isExecuted();
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {

        AnalyticsHandler.selectItem(productModel.getName());
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);


    }

    public void searchBarcode(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        productList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                binding.closeBtn.setVisibility(View.VISIBLE);
                binding.closeBtn.setText(R.string.fal_times);

                FavouriteResultModel result = (FavouriteResultModel) obj;
                String message = getActivityy().getString(R.string.fail_to_get_data);

                binding.offerLy.setVisibility(View.GONE);
                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
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

                            binding.offerLy.setVisibility(View.GONE);
                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productList = result.getData();
                            AnalyticsHandler.ViewSearchResult(filter);
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
        }).barcodeSearch(country_id, city_id, user_id, filter, page_number, page_size);
    }

    public void searchTxt(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {
        AnalyticsHandler.searchEvent(filter);

        productList.clear();
        offerList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                FavouriteResultModel result = (FavouriteResultModel) obj;
                String message = getActivityy().getResources().getString(R.string.fail_to_get_data);

                binding.offerLy.setVisibility(View.GONE);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
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
                        binding.offerLy.setVisibility(View.GONE);

                        Log.i(TAG, "Log productList Search " + result.getData());

                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productList = result.getData();
                            AnalyticsHandler.ViewSearchResult(filter);
                            initAdapter();


                        } else {

                            binding.dataLY.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

                        }


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }
            }

        }).searchTxt(country_id, city_id, user_id, filter, page_number, page_size);
    }

    private void getIntentExtra() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            result = bundle.getString(Constants.CODE);
            SearchText = bundle.getString(Constants.inputType_text);
            searchByCode = bundle.getBoolean(Constants.SEARCH_BY_CODE_byCode, false);
            if (searchByCode) {
                searchBarcode(country_id, city_id, user_id, result, 0, 10);

            } else if (SearchText != null) {
                searchTxt(country_id, city_id, user_id, SearchText, 0, 10);

            }


        }
    }

    public void autoComplete(int country_id, int city_id, String user_id, String text, int page_number, int page_size) {
        data.clear();

        DataFeacher dataFeacher = new DataFeacher(false, (obj, func, IsSuccess) -> {
            AutoCompeteResult result = (AutoCompeteResult) obj;

            if (IsSuccess) {
                if (result != null && result.getData().size() > 0) {

                    binding.dataLY.setVisibility(View.VISIBLE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                    data = result.getData();
                    getAutoNames();

                } else {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                }
            }

        });
        searchCall = dataFeacher.autocomplete(country_id, city_id, user_id, text, page_number, page_size);

    }

    private void getAutoNames() {
        autoCompleteList.clear();
        for (int i = 0; i < data.size(); i++) {
            autoCompleteList.add(data.get(i).getDataName());
        }

        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, autoCompleteList);
            binding.searchEt.setAdapter(adapter);

            if (isVisible) binding.searchEt.showDropDown();
        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {

        if (event.type.equals(MessageEvent.TYPE_view)) {
            numColumn = (int) event.data;
            initAdapter();
            try {
                gridLayoutManager.setSpanCount(numColumn);

            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();


        } else if (event.type.equals(MessageEvent.TYPE_SORT)) {

            Collections.sort(productList, Collections.reverseOrder());

        } else if (event.type.equals(MessageEvent.TYPE_search)) {
            searchByCode = true;
            result = (String) event.data;
            searchBarcode(country_id, city_id, user_id, result, 0, 10);


        }

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
                Toast.makeText(getActivityy(), "" + getActivity().getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(error -> Toast.makeText(getActivityy(), "" + getActivity().getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActivityy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivityy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getActivityy(), FullScannerActivity.class);
            startActivityForResult(intent, SEARCH_CODE);
        }

    }

    @Override
    public void onTagClicked(int position, MostSearchModel mostSearchModel) {
        searchTxt(country_id, city_id, user_id, mostSearchModel.getTagName(), 0, 10);


    }


    public void initOffer() {
        //productOfferAdapter = new OfferProductAdapter(getActivityy(), productOffersList, this, 10);
        OfferProductAdapter productOfferAdapter = new OfferProductAdapter(getActivityy(), productOffersList, 0, 0, country_id, city_id, user_id,
                productOffersList.size(), binding.offerRecycler, Constants.offered_filter, this, new DataCallback() {
            @Override
            public void dataResult(Object obj, String func, boolean IsSuccess) {

            }
        }, 2);
        binding.offerRecycler.setAdapter(productOfferAdapter);
    }

    public void GetHomePage() {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
        binding.searchLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {


            if (isVisible()) {

                MainModel result = (MainModel) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
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
                        binding.searchLY.setVisibility(View.VISIBLE);
                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.searchLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        binding.offerRecycler.setVisibility(View.VISIBLE);


                        if (result.getOfferedProducts() != null && result.getOfferedProducts().size() > 0) {
                            Log.i("tag", "Log Offer List" + offerList.size());

                            productOffersList = result.getOfferedProducts();
                            initOffer();

                        } else {
                            binding.noOffers.setVisibility(View.VISIBLE);
                            binding.offerRecycler.setVisibility(View.GONE);

                        }


                    }
                }
            }
        }).GetMainPage(0, country_id, city_id, user_id);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {

                if (data != null) {
                    boolean searchByCode = data.getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false);
                    String CODE = data.getStringExtra(Constants.CODE);
                    result = CODE;
                    searchBarcode(country_id, city_id, user_id, result, 0, 10);


                }


            }

        }
    }

}