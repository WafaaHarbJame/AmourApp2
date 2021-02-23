package com.ramez.shopp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.impl.constraints.ConstraintListener;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.AllBookleteActivity;
import com.ramez.shopp.Activities.AllListActivity;
import com.ramez.shopp.Activities.BrousherActivity;
import com.ramez.shopp.Activities.CategoryProductsActivity;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Activities.SearchActivity;
import com.ramez.shopp.Adapter.AutomateSlider;
import com.ramez.shopp.Adapter.BannersAdapter;
import com.ramez.shopp.Adapter.BookletAdapter;
import com.ramez.shopp.Adapter.BrandsAdapter;
import com.ramez.shopp.Adapter.CategoryAdapter;
import com.ramez.shopp.Adapter.ProductAdapter;
import com.ramez.shopp.Adapter.SliderAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.ContentValues.TAG;


public class HomeFragment extends FragmentBase implements ProductAdapter.OnItemClick, ZXingScannerView.ResultHandler, CategoryAdapter.OnItemClick, BookletAdapter.OnBookletClick {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    ArrayList<ProductModel> productBestList;
    ArrayList<ProductModel> productSellerList;
    ArrayList<ProductModel> productOffersList;
    LinearLayoutManager bestProductGridLayoutManager;
    LinearLayoutManager bestSellerLayoutManager;
    LinearLayoutManager bestOfferGridLayoutManager;
    String user_id = "0";
    ArrayList<CategoryModel> categoryModelList;
    ArrayList<String> sliderList;
    ArrayList<String> bannersList;
    ArrayList<String> brandsList;
    private FragmentHomeBinding binding;
    private ProductAdapter productBestAdapter;
    private ProductAdapter productSellerAdapter;
    private ProductAdapter productOfferAdapter;
    private int category_id = 0, country_id, city_id;
    // TODO Barcode
    private Barcode barcodeResult;
    private String result;
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private CategoryAdapter categoryAdapter;
    private BookletAdapter bookletAdapter;
    private Activity activity;
    private List<BookletsModel> bookletsList;
    private SliderAdapter sliderAdapter;
    private BannersAdapter bannerAdapter;
    private BrandsAdapter brandsAdapter;
    GridLayoutManager bookletManger;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        productBestList = new ArrayList<>();
        bookletsList = new ArrayList<>();
        sliderList = new ArrayList<>();
        bannersList = new ArrayList<>();
        categoryModelList = new ArrayList<>();
        productSellerList = new ArrayList<>();
        productOffersList = new ArrayList<>();
        brandsList = new ArrayList<>();
        mScannerView = new ZXingScannerView(getActivity());

        activity = getActivity();
        if (UtilityApp.isLogin()) {

            if (UtilityApp.getUserData() != null) {
                MemberModel memberModel = UtilityApp.getUserData();
                user_id = String.valueOf(memberModel.getId());
            }


        }

        country_id = UtilityApp.getLocalData().getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());

        bestProductGridLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        bestOfferGridLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        bestSellerLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
         bookletManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);
        GridLayoutManager brandManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager bannersManger = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        GridLayoutManager categoryManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);


        binding.offerRecycler.setItemAnimator(null);
        binding.bestProductRecycler.setItemAnimator(null);
        binding.bestSellerRecycler.setItemAnimator(null);
        binding.BookletRecycler.setItemAnimator(null);
        binding.catRecycler.setItemAnimator(null);
        binding.brandsRecycler.setItemAnimator(null);
        binding.brandsRecycler.setHasFixedSize(true);
        binding.catRecycler.setHasFixedSize(true);
        binding.bannersRv.setItemAnimator(null);
        binding.bannersRv.setHasFixedSize(true);

        binding.bestSellerRecycler.setLayoutManager(bestSellerLayoutManager);
        binding.bestProductRecycler.setLayoutManager(bestProductGridLayoutManager);
        binding.offerRecycler.setLayoutManager(bestOfferGridLayoutManager);
        binding.BookletRecycler.setLayoutManager(bookletManger);
        binding.catRecycler.setLayoutManager(categoryManger);
        binding.brandsRecycler.setLayoutManager(brandManger);
        binding.bannersRv.setLayoutManager(bannersManger);


        binding.offerRecycler.setHasFixedSize(true);
        binding.bestProductRecycler.setHasFixedSize(true);
        binding.bestSellerRecycler.setHasFixedSize(true);
        binding.BookletRecycler.setHasFixedSize(true);
        binding.brandsRecycler.setHasFixedSize(true);

        GetHomePage();

        sliderList.add("https://www.familyfreshmeals.com/wp-content/uploads/2019/06/Big-Mac-Sliders-Family-Fresh-Meals-Recipe.jpg");
        sliderList.add("https://www.familyfreshmeals.com/wp-content/uploads/2019/06/Big-Mac-Sliders-Family-Fresh-Meals-Recipe.jpg");
        sliderList.add("https://www.familyfreshmeals.com/wp-content/uploads/2019/06/Big-Mac-Sliders-Family-Fresh-Meals-Recipe.jpg");
        sliderList.add("https://www.familyfreshmeals.com/wp-content/uploads/2019/06/Big-Mac-Sliders-Family-Fresh-Meals-Recipe.jpg");


        bannersList.add("https://previews.123rf.com/images/dawool/dawool1803/dawool180300184/97784852-horizontal-shopping-banners-with-cosmetics-vector-illustration-.jpg");
        bannersList.add("https://previews.123rf.com/images/dawool/dawool1803/dawool180300184/97784852-horizontal-shopping-banners-with-cosmetics-vector-illustration-.jpg");

         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");
         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");
         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");
         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");
         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");
         brandsList.add("https://fontmeme.com/images/MM%E2%80%99s-Logo.jpg");

        initSliderAdapter();
        initBannersAdapter();
        initBrandsAdapter();

        AllListener();

        binding.imageSlider.setSliderAdapter(new AutomateSlider(getActivityy(),sliderList));
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.startAutoCycle();


        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }


        return view;
    }

    private void AllListener() {
        binding.searchBut.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivityy(), SearchActivity.class);
            startActivity(intent);

        });


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            GetHomePage();
            getBooklets(city_id);

        });


        binding.barcodeBut.setOnClickListener(view1 -> {

            checkCameraPermission();

        });


        binding.moreBestBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActivityy(), AllListActivity.class);
            intent.putExtra(Constants.LIST_MODEL_NAME, activity.getString(R.string.best_products));
            intent.putExtra(Constants.FILTER_NAME, Constants.featured_filter);
            startActivity(intent);


        });

        binding.moreCategoryBut.setOnClickListener(view -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY));
            FragmentManager fragmentManager = getParentFragmentManager();
            CategoryFragment categoryFragment = new CategoryFragment();
            fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryFragment, "categoryFragment").commit();

        });

        binding.moreBoughtBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActivityy(), AllListActivity.class);
            intent.putExtra(Constants.LIST_MODEL_NAME, activity.getString(R.string.best_sell));
            intent.putExtra(Constants.FILTER_NAME, Constants.quick_filter);
            startActivity(intent);


        });
        binding.moreOfferBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActivityy(), AllListActivity.class);
            intent.putExtra(Constants.LIST_MODEL_NAME, activity.getString(R.string.offers));
            intent.putExtra(Constants.FILTER_NAME, Constants.offered_filter);
            startActivity(intent);


        });

        binding.moreBooklett.setOnClickListener(view -> {
            Intent intent = new Intent(getActivityy(), AllBookleteActivity.class);
            startActivity(intent);

        });
    }

    public void initAdapter() {

        productBestAdapter = new ProductAdapter(getActivityy(), productBestList, this, 10);
        productSellerAdapter = new ProductAdapter(getActivityy(), productSellerList, this, 10);
        productOfferAdapter = new ProductAdapter(getActivityy(), productOffersList, this, 10);

        binding.bestProductRecycler.setAdapter(productBestAdapter);
        binding.bestSellerRecycler.setAdapter(productSellerAdapter);
        binding.offerRecycler.setAdapter(productOfferAdapter);
    }


    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);

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

                        getBooklets(city_id);

                        if (result.getFeatured() != null && result.getFeatured().size() > 0 || result.getQuickProducts() != null && result.getQuickProducts().size() > 0 || result.getOfferedProducts() != null && result.getOfferedProducts().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.searchLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productBestList = result.getFeatured();
                            productSellerList = result.getQuickProducts();
                            productOffersList = result.getOfferedProducts();

                            if (productOffersList.size() == 0) {
                                binding.offerLy.setVisibility(View.GONE);

                            }


                            if (productSellerList.size() == 0) {
                                binding.bestSellerLy.setVisibility(View.GONE);

                            }


                            if (productBestList.size() == 0) {
                                binding.bestProductLy.setVisibility(View.GONE);

                            }
                            Log.i(TAG, "Log productBestList" + productOffersList.size());
                            Log.i(TAG, "Log productSellerList" + productSellerList.size());
                            Log.i(TAG, "Log productOffersList" + productOffersList.size());
                            initAdapter();

                            if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
                                categoryModelList = UtilityApp.getCategories();
                                initCatAdapter();

                            } else {
                                getCategories(city_id);

                            }

                        } else {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.bestProductLy.setVisibility(View.GONE);
                            binding.bestSellerLy.setVisibility(View.GONE);
                            binding.offerLy.setVisibility(View.GONE);

                            if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
                                categoryModelList = UtilityApp.getCategories();
                                initCatAdapter();

                            } else {
                                getCategories(city_id);

                            }


                        }


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }
            }
        }).GetMainPage(0, country_id, city_id, user_id);
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
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getActivityy(), "" + getActivity().getString(R.string.error_in_data), Toast.LENGTH_SHORT).show();

            }
        }).onSameThread().check();
    }

    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActivityy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivityy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getActivityy(), FullScannerActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
            result = rawResult.getText();
            Intent intent = new Intent(getActivityy(), SearchActivity.class);
            //  intent.putExtra(Constants.CODE, "2700093");
            intent.putExtra(Constants.CODE, "2700093");
            intent.putExtra(Constants.SEARCH_BY_CODE_byCode, true);
            startActivity(intent);


        } catch (Exception e) {
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();

    }


    public void getCategories(int storeId) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {

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

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            categoryModelList = result.getData();
                            Log.i(TAG, "Log productBestList" + categoryModelList.size());
                            UtilityApp.setCategoriesData(categoryModelList);

                            initCatAdapter();

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

    private void initCatAdapter() {

        categoryAdapter = new CategoryAdapter(getActivityy(), categoryModelList,categoryModelList.size(), this);
        binding.catRecycler.setAdapter(categoryAdapter);

    }


    private void initBookletAdapter() {
        if(bookletsList.size()>=3){
            bookletManger.setOrientation(RecyclerView.HORIZONTAL);
        }
        else {
            bookletManger.setOrientation(RecyclerView.VERTICAL);

        }
        bookletAdapter = new BookletAdapter(getActivityy(), bookletsList, 4, this);
        binding.BookletRecycler.setAdapter(bookletAdapter);

    }

    @Override
    public void onItemClicked(int position, CategoryModel categoryModel) {

        Intent intent = new Intent(getActivityy(), CategoryProductsActivity.class);
        intent.putExtra(Constants.CAT_LIST, categoryModelList);
        intent.putExtra(Constants.SELECTED_POSITION, categoryModelList.get(position).getId());
        intent.putExtra(Constants.position, position);
        intent.putExtra(Constants.CAT_MODEL, categoryModel);
        startActivity(intent);

    }


    public void getBooklets(int storeId) {
        bookletsList.clear();

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<BookletsModel>> result = (ResultAPIModel<ArrayList<BookletsModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {
                    Log.i(TAG, "Log getBooklets" + result.data.size());

                    binding.BookletRecycler.setVisibility(View.VISIBLE);

                    bookletsList = result.data;
                    initBookletAdapter();


                } else {

                    binding.noBookletsTv.setVisibility(View.VISIBLE);
                    binding.BookletRecycler.setVisibility(View.GONE);

                }


            }


        }).getBookletsList(storeId);
    }

    @Override
    public void onBookletClicked(int position, BookletsModel bookletsModel) {
        Intent intent = new Intent(getActivityy(), BrousherActivity.class);
        intent.putExtra(Constants.bookletsModel, bookletsModel);
        startActivity(intent);

    }


    public void initSliderAdapter() {

        sliderAdapter = new SliderAdapter(getActivityy(), sliderList);
        binding.viewPager.setAdapter(sliderAdapter);
    }

    public void initBannersAdapter() {
        bannerAdapter = new BannersAdapter(getActivityy(), bannersList, null);
        binding.bannersRv.setAdapter(bannerAdapter);

    }


    public void initBrandsAdapter() {

        brandsAdapter = new BrandsAdapter(getActivityy(), brandsList, null);
        binding.brandsRecycler.setAdapter(brandsAdapter);

    }
}