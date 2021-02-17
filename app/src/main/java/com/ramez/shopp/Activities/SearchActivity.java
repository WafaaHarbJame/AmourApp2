package com.ramez.shopp.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ramez.shopp.Adapter.SearchProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.AutoCompeteResult;
import com.ramez.shopp.Models.AutoCompleteModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class SearchActivity extends ActivityBase implements SearchProductAdapter.OnItemClick {

    ActivitySearchBinding binding;
    ArrayList<ProductModel> productList;
    ArrayList<ProductModel> offerList;

    GridLayoutManager gridLayoutManager;
    boolean searchByCode = false;
    int numColumn = 2;
    private ArrayList<AutoCompleteModel> data = null;
    private ArrayList<String> autoCompleteList;
    private SearchProductAdapter adapter;
    private int country_id, city_id;
    private String user_id = "0", filter, result, searchQuery;
    private MemberModel user;
    private LocalModel localModel;
    private Call searchCall;
    private Runnable runnable;
    private Handler handler;
    private boolean toggleButton = false;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        productList = new ArrayList<>();
        offerList = new ArrayList<>();
        data = new ArrayList<>();
        autoCompleteList = new ArrayList<>();


        binding.searchEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT);

        binding.searchEt.setFocusable(true);
        binding.searchEt.requestFocusFromTouch();
        binding.searchEt.setThreshold(1);


        gridLayoutManager = new GridLayoutManager(getActiviy(), numColumn);
        binding.recycler.setLayoutManager(gridLayoutManager);

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(null);

        if (UtilityApp.isLogin()) {

            user = UtilityApp.getUserData();
            user_id = String.valueOf(user.getId());

        }


        localModel = UtilityApp.getLocalData();
        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

        getIntentExtra();

        binding.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            String text = binding.searchEt.getText().toString();
            searchTxt(country_id, city_id, user_id, text, 0, 10);

        });


        binding.searchEt.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                String text = v.getText().toString();
                searchTxt(country_id, city_id, user_id, text, 0, 10);
                hideKeyboard(getActiviy());

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
                binding.closeBtn.setText(R.string.fal_times);
                searchQuery = s.toString();
                handler.postDelayed(runnable, 500);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (searchCall != null && searchCall.isExecuted()) searchCall.cancel();
                handler.removeCallbacks(runnable);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        binding.view2But.setOnClickListener(view1 -> {

            toggleButton = !toggleButton;

            if (toggleButton) {
                numColumn = 1;
                gridLayoutManager.setSpanCount(numColumn);
                gridLayoutManager.requestLayout();
                initAdapter();
//                final int spanSize =gridLayoutManager.getSpanCount();
//                if (spanSize > numColumn) {
//                    throw new IllegalArgumentException("Item at position "  + " requires " +
//                            spanSize + " spans but GridLayoutManager has only " + numColumn
//                            + " spans.");
//                }
                binding.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view1));

            } else {
                numColumn = 2;
                gridLayoutManager.setSpanCount(numColumn);
                gridLayoutManager.requestLayout();
                initAdapter();

//                final int spanSize =gridLayoutManager.getSpanCount();
//                if (spanSize > numColumn) {
//                    throw new IllegalArgumentException("Item at position "  + " requires " +
//                            spanSize + " spans but GridLayoutManager has only " + numColumn
//                            + " spans.");
//                }

                binding.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view2));

            }
            adapter.notifyDataSetChanged();


        });


        binding.priceBut.setOnClickListener(view1 -> {

            // Collections.sort(productList);
            Collections.sort(productList, Collections.reverseOrder());

        });


        binding.searchEt.setOnItemClickListener((adapterView, view12, position, l) -> {
            String text = autoCompleteList.get(position).toString();
            searchTxt(country_id, city_id, user_id, text, 0, 10);

        });

        binding.closeBtn.setOnClickListener(view1 -> {
            productList.clear();
            binding.searchEt.setText("");

        });


    }

    public void initAdapter() {

        adapter = new SearchProductAdapter(getActiviy(), productList, country_id, city_id, user_id, binding.recycler, binding.searchEt.getText().toString(), this, numColumn);
        binding.recycler.setAdapter(adapter);

        binding.categoriesCountTv.setText(String.valueOf(productList.size()));
        binding.offerCountTv.setText(String.valueOf(offerList.size()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (searchCall != null && searchCall.isExecuted()) searchCall.isExecuted();
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
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
            FavouriteResultModel result = (FavouriteResultModel) obj;
            String message = getActiviy().getString(R.string.fail_to_get_data);

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

        }).barcodeSearch(country_id, city_id, user_id, filter, page_number, page_size);
    }

    public void searchTxt(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        productList.clear();
        offerList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            FavouriteResultModel result = (FavouriteResultModel) obj;
            String message = getActiviy().getString(R.string.fail_to_get_data);

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
                    Log.i(TAG, "Log productList Search " + result.getData());

                    if (result.getData() != null && result.getData().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        productList = result.getData();
                        Log.i(TAG, "Log productList Search " + productList.size());
                        binding.categoriesCountTv.setText(String.valueOf(productList.size()));
                        getOffersProducts(productList);
                        //initAdapter();


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

        }).searchTxt(country_id, city_id, user_id, filter, page_number, page_size);
    }

    private void showLoginDialog() {
        CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
        checkLoginDialog.show();
    }

    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            result = getIntent().getStringExtra(Constants.CODE);
            searchByCode = getIntent().getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false);
            searchBarcode(country_id, city_id, user_id, result, 0, 10);


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
                    Log.i(TAG, "Log autoComplete list " + productList.size());

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActiviy(), android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        binding.searchEt.setAdapter(adapter);

        binding.searchEt.showDropDown();

    }

    private int getOffersProducts(ArrayList<ProductModel> productList) {
        int size = 0;
        if (productList != null) {

            for (int i = 0; i < productList.size(); i++) {
                ProductModel productModel = productList.get(i);
                if (productModel.getProductBarcodes().get(0).getIsSpecial()) {
                    offerList.add(productModel);
                }
            }


            size = offerList.size();
        }

        binding.offerCountTv.setText(String.valueOf(offerList.size()));

        initAdapter();

        return size;

    }


}