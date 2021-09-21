package com.ramez.shopp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.AllBookleteActivity;
import com.ramez.shopp.Activities.AllListActivity;
import com.ramez.shopp.Activities.ChooseNearCity;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Activities.RamezKitchenActivity;
import com.ramez.shopp.Activities.RewardsActivity;
import com.ramez.shopp.Adapter.AutomateSlider;
import com.ramez.shopp.Adapter.BannersAdapter;
import com.ramez.shopp.Adapter.BookletAdapter;
import com.ramez.shopp.Adapter.BrandsAdapter;
import com.ramez.shopp.Adapter.CategoryAdapter;
import com.ramez.shopp.Adapter.KitchenAdapter;
import com.ramez.shopp.Adapter.MainSliderAdapter;
import com.ramez.shopp.Adapter.ProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.SelectBranchDialog;
import com.ramez.shopp.Dialogs.WhatsUpDialog;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrandModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.CityModel;
import com.ramez.shopp.Models.CountryDetailsModel;
import com.ramez.shopp.Models.DeliveryResultModel;
import com.ramez.shopp.Models.DeliveryTime;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.ActivityHandler;
import com.ramez.shopp.Utils.DateHandler;
import com.ramez.shopp.databinding.FragmentHomeBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class HomeFragment extends FragmentBase implements ProductAdapter.OnItemClick, CategoryAdapter.OnItemClick, BookletAdapter.OnBookletClick, AutomateSlider.OnSliderClick, BrandsAdapter.OnBrandClick, BannersAdapter.OnBannersClick, MainSliderAdapter.OnSliderClick, KitchenAdapter.OnKitchenClick {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    String countryCode = "";

    ArrayList<ProductModel> productBestList;
    ArrayList<ProductModel> productSellerList;
    ArrayList<ProductModel> productOffersList;
    ArrayList<ProductModel> productRecentsList;
    LinearLayoutManager bestProductGridLayoutManager;
    LinearLayoutManager bestSellerLayoutManager;
    LinearLayoutManager bestOfferGridLayoutManager;
    LinearLayoutManager recentLayoutManager;
    String user_id = "0";
    ArrayList<CategoryModel> categoryModelList;
    ArrayList<Slider> sliderList;
    ArrayList<Slider> bannersList;
    ArrayList<BrandModel> brandsList;
    GridLayoutManager bookletManger;
    GridLayoutManager brandManger;

    ArrayList<CityModel> cityModelArrayList;

    private int SEARCH_CODE = 2000;
    private FragmentHomeBinding binding;
    private ProductAdapter productBestAdapter;
    private ProductAdapter productSellerAdapter;
    private ProductAdapter productOfferAdapter;
    private ProductAdapter productRecentAdapter;
    private int category_id = 0, country_id, city_id;
    // TODO Barcode
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private CategoryAdapter categoryAdapter;
    private BookletAdapter bookletAdapter;
    private Activity activity;
    private ArrayList<BookletsModel> bookletsList;
    private MainSliderAdapter sliderAdapter;
    private BannersAdapter bannerAdapter;
    private BrandsAdapter brandsAdapter;
    private ArrayList<DinnerModel> list;
    private String lang;
    private LocalModel localModel;
    private CityModel selectedCityModel;
    private TotalPointModel totalPointModel;
    private CountryDetailsModel countryDetailsModel;

    private SelectBranchDialog selectBranchDialog;

    private ActivityResultLauncher<Intent> changeBranchLauncher = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());

        productBestList = new ArrayList<>();
        bookletsList = new ArrayList<>();
        sliderList = new ArrayList<>();
        bannersList = new ArrayList<>();
        productRecentsList = new ArrayList<>();
        list = new ArrayList<>();
        categoryModelList = new ArrayList<>();
        productSellerList = new ArrayList<>();
        productOffersList = new ArrayList<>();
        brandsList = new ArrayList<>();

        mScannerView = new ZXingScannerView(getActivity());

        lang = UtilityApp.getLanguage() == null ? Locale.getDefault().getLanguage() : UtilityApp.getLanguage();

        activity = getActivity();
        if (UtilityApp.isLogin()) {

            if (UtilityApp.getUserData() != null) {
                MemberModel memberModel = UtilityApp.getUserData();
                user_id = String.valueOf(memberModel.getId());
                getTotalPoint();
            }


        }


        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

        getCityList(country_id);
        getDeliveryTimeListNew(city_id);
        CheckLoyal(localModel.getShortname());

        bestProductGridLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        bestOfferGridLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        bestSellerLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        recentLayoutManager = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        bookletManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);
        brandManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager bannersManger = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        GridLayoutManager categoryManger = new GridLayoutManager(getActivityy(), 2, RecyclerView.HORIZONTAL, false);

        LinearLayoutManager kitchenLy = new LinearLayoutManager(getActivityy(), RecyclerView.HORIZONTAL, false);
        binding.kitchenRecycler.setLayoutManager(kitchenLy);
        binding.kitchenRecycler.setItemAnimator(null);
        binding.kitchenRecycler.setHasFixedSize(true);


        binding.recentlyRecycler.setItemAnimator(null);
        binding.recentlyRecycler.setHasFixedSize(true);
        binding.recentlyRecycler.setLayoutManager(recentLayoutManager);

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


        AllListener();

        binding.branchLY.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivityy(), ChooseNearCity.class);
            intent.putExtra(Constants.COUNTRY_ID, localModel.getCountryId());
            changeBranchLauncher.launch(intent);


//            if (selectBranchDialog == null) {
//                selectBranchDialog = new SelectBranchDialog(getActivity(), selectedCityModel, cityModelArrayList, new DataFetcherCallBack() {
//                    @Override
//                    public void Result(Object obj, String func, boolean IsSuccess) {
//                        selectedCityModel = (CityModel) obj;
//
//                        city_id = selectedCityModel.getId();
//                        localModel.setCityId(String.valueOf(city_id));
//                        UtilityApp.setLocalData(localModel);
//
////                        setBranchData();
//                        if (getActivity() != null)
//                            getActivity().recreate();
//
//                    }
//                });
//                selectBranchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        selectBranchDialog = null;
//                    }
//                });
//            }

        });

        binding.totalPointLY.setOnClickListener(view1 -> {

            if (UtilityApp.isLogin()) {
                Intent intent = new Intent(getActivityy(), RewardsActivity.class);
                startActivity(intent);
            } else {
                Toasty.warning(getActivityy(), R.string.you_not_signin, Toast.LENGTH_SHORT, true).show();

            }


        });


        if (UtilityApp.isLogin()) {
            boolean isFirstLogin = UtilityApp.isFirstLogin();

            if (isFirstLogin) {
                UtilityApp.setIsFirstLogin(false);
                WhatsUpDialog whatsUpDialog = new WhatsUpDialog(getActivityy(), R.string.Whatsapp_Live_Support, R.string.is_Active, R.string.ok, R.string.cancel, null, null);
                whatsUpDialog.show();

            }
        }


        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                binding.pager.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });


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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        changeBranchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                activityResult -> {
                    // Handle the returned Uri
                    if (getActivity() != null)
                        getActivity().recreate();


                });

    }

    private void AllListener() {

        binding.searchBut.setOnClickListener(view1 -> {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_search,false));


        });


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            GetHomePage();

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

        binding.moreRecentBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActivityy(), AllListActivity.class);
            intent.putExtra(Constants.LIST_MODEL_NAME, activity.getString(R.string.best_products));
            intent.putExtra(Constants.FILTER_NAME, Constants.new_filter);
            startActivity(intent);


        });


        binding.moreCategoryBut.setOnClickListener(view -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_POSITION, 1));
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
            intent.putExtra(Constants.Activity_type, Constants.BOOKLETS);
            startActivity(intent);

        });

        binding.moreKitchenBut.setOnClickListener(view -> {
            Intent intent = new Intent(getActivityy(), AllBookleteActivity.class);
            intent.putExtra(Constants.Activity_type, Constants.DINNERS);
            startActivity(intent);

        });

        binding.moreBrandBut.setOnClickListener(view -> {
            Intent intent = new Intent(getActivityy(), AllBookleteActivity.class);
            intent.putExtra(Constants.Activity_type, Constants.BRANDS);
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

    public void initRecentAdapter() {

        productRecentAdapter = new ProductAdapter(getActivityy(), productRecentsList, this, 10);
        binding.recentlyRecycler.setAdapter(productRecentAdapter);
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);

    }


    public void GetHomePage() {

        Log.i(TAG, "Log getSliders ");
        Log.i(TAG, "Log getSliders country_id " + country_id);
        Log.i(TAG, "Log getSliders  city_id " + city_id);

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
//                        binding.searchLY.setVisibility(View.VISIBLE);

                        if (UtilityApp.getDinners() != null && UtilityApp.getDinners().size() > 0) {
                            list = UtilityApp.getDinners();
                            initKitchenAdapter();

                        } else {
                            getDinners(lang);

                        }

                        if (UtilityApp.getSliders() != null && UtilityApp.getSliders().size() > 0
                                && UtilityApp.getBanners() != null && UtilityApp.getBanners().size() > 0) {
                            sliderList = UtilityApp.getSliders();
                            bannersList = UtilityApp.getBanners();

                        } else {

                            UtilityApp.setSliderData(null);
                            UtilityApp.setBannerData(null);

                            if (result.getSliders().size() > 0) {

                                for (int i = 0; i < result.getSliders().size(); i++) {
                                    Slider slider = result.getSliders().get(i);
                                    if (slider.getType() == 0) {
                                        sliderList.add(slider);


                                    } else {
                                        bannersList.add(slider);

                                    }

                                }


                                if (sliderList.size() > 0) {
                                    UtilityApp.setSliderData(sliderList);

                                }

                                if (bannersList.size() > 0) {
                                    UtilityApp.setBannerData(bannersList);

                                }

                            }


                        }

                        initSliderAdapter();
                        initBannersAdapter();

                        getBooklets(city_id);
                        GetAllBrands(city_id);
                        getProductList(0, country_id, city_id, user_id, Constants.new_filter, 0, 0, 10);

                        if (result.getFeatured() != null && result.getFeatured().size() > 0 ||
                                result.getQuickProducts() != null && result.getQuickProducts().size() > 0
                                || result.getOfferedProducts() != null && result.getOfferedProducts().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
//                            binding.searchLY.setVisibility(View.VISIBLE);
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


                            if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
                                categoryModelList = UtilityApp.getCategories();
                                initCatAdapter();

                            } else {
                                getCategories(city_id);

                            }
                            initAdapter();


                        } else {


                            if (productOffersList.size() == 0) {
                                binding.offerLy.setVisibility(View.GONE);

                            }


                            if (productSellerList.size() == 0) {
                                binding.bestSellerLy.setVisibility(View.GONE);

                            }


                            if (productBestList.size() == 0) {
                                binding.bestProductLy.setVisibility(View.GONE);

                            }


                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);


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

    public void getDeliveryTimeListNew(int storeId) {

        String countryCode = "";
        if (localModel.getShortname() != null)
            countryCode = localModel.getShortname();
        else countryCode = GlobalData.COUNTRY;

        String url = GlobalData.BaseURL + countryCode + "/GroceryStoreApi/api/v6/Orders/nextDeliveryTime?";
        Log.d(TAG, "Log Get first " + url);
        Log.d(TAG, "Log  store_id " + storeId);


        String token = UtilityApp.getToken() != null ? UtilityApp.getToken() : "token";

        AndroidNetworking.get(url).setTag("test").setPriority(Priority.HIGH).
                addHeaders("ApiKey", Constants.api_key)
                .addHeaders("device_type", Constants.deviceType)
                .addHeaders("app_version", UtilityApp.getAppVersionStr())
                .addHeaders("token", token).
                addQueryParameter("store_id", String.valueOf(storeId)).build()
                .getAsObject(DeliveryResultModel.class, new ParsedRequestListener<DeliveryResultModel>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(DeliveryResultModel result) {

                        if (isVisible()) {

                            if (result != null && result.getData() != null) {
                                DeliveryTime firstTime = result.getData();
                                String deliveryDay = DateHandler.FormatDate4(firstTime.getDate(), "yyyy-MM-dd", "EEE", UtilityApp.getLanguage());

                                if (UtilityApp.getLanguage().equals(Constants.Arabic))
                                    binding.deliveryTimeTv.setText(firstTime.getTime() + " " + deliveryDay);
                                else
                                    binding.deliveryTimeTv.setText(deliveryDay + " " + firstTime.getTime());


                            }

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        GlobalData.Toast(getActivityy(), anError.getMessage());
                    }


                });
    }

    private void getTotalPoint() {

        totalPointModel = DBFunction.getTotalPoints();

        if (totalPointModel == null) {

            callGetTotalPoints();

        } else {
            callGetTotalPoints();
            setTotalPointsData();
        }
    }


    private void CheckLoyal(String shortName) {
        countryDetailsModel = DBFunction.getLoyal();

        if (countryDetailsModel == null) {

            getCountryDetail(shortName);

        } else {
            boolean hasLoyal = countryDetailsModel.hasLoyal;
            showLoyalLy(hasLoyal);


        }

    }

    private void showLoyalLy(boolean hasLoyal) {
        if (hasLoyal) {
            binding.totalPointLY.setVisibility(View.VISIBLE);
        } else {
            binding.totalPointLY.setVisibility(View.GONE);

        }


    }


    private void getCountryDetail(String shortName) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<CountryDetailsModel> result = (ResultAPIModel<CountryDetailsModel>) obj;

            if (result != null && result.isSuccessful()) {
                if (result != null && result.data != null) {
                    CountryDetailsModel countryDetailsModel = result.data;
                    Log.i(getClass().getSimpleName(), "Log  getCountryDetail call hasLoyal " + countryDetailsModel.hasLoyal);
                    DBFunction.setLoyal(countryDetailsModel);
                    showLoyalLy(countryDetailsModel.hasLoyal);

                }


            }

        }).getCountryDetail(shortName);
    }


    private void callGetTotalPoints() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<TotalPointModel> result = (ResultAPIModel<TotalPointModel>) obj;

            if (result != null && result.isSuccessful() && result.data != null) {
                totalPointModel = result.data;
                DBFunction.setTotalPoints(totalPointModel);
                setTotalPointsData();

            }

        }).getTotalPoint(Integer.parseInt(user_id));


    }

    private void setTotalPointsData() {

        binding.totalPointTv.setText(String.valueOf(totalPointModel.points));


    }

    private void callCityListApi() {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                CityModelResult result = (CityModelResult) obj;
                if (result.getData() != null && result.getData().size() > 0) {
                    cityModelArrayList = new ArrayList<>(result.getData());
                    searchSelectedCity();
                    setBranchData();
                }
            }

        }).CityHandle(country_id, getActivity());
    }

    private void getCityList(int country_id) {

        Log.i("TAG", "Log country_id" + country_id);

        callCityListApi();
    }

    private void searchSelectedCity() {

        for (CityModel cityModel : cityModelArrayList) {
            if (city_id == cityModel.getId()) {
                selectedCityModel = cityModel;
                break;
            }
        }

    }

    private void setBranchData() {

        if (selectedCityModel != null) {
            binding.branchNameTv.setText(selectedCityModel.getCityName());
        }
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
                        if (result != null && result.getData() != null && result.getData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            categoryModelList = result.getData();
                            Log.i(TAG, "Log productBestList" + categoryModelList.size());
                            UtilityApp.setCategoriesData(categoryModelList);

                            initCatAdapter();

                        } else {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.categoryLy.setVisibility(View.GONE);

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

    public void GetAllBrands(int storeId) {
        brandsList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {

                ResultAPIModel<ArrayList<BrandModel>> result = (ResultAPIModel<ArrayList<BrandModel>>) obj;
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
                            Log.i(TAG, "Log getBrands" + result.data.size());
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


                            if (brandsList.size() == 0) {
                                binding.brandLy.setVisibility(View.GONE);

                            }
                            initBrandsAdapter();

                        } else {

                            binding.brandLy.setVisibility(View.GONE);

                        }


                    } else {

                        binding.brandLy.setVisibility(View.GONE);


                    }
                }
            }

        }).GetAllBrands(storeId);
    }

    private void initCatAdapter() {

        categoryAdapter = new CategoryAdapter(getActivityy(), categoryModelList, 10, this, false);
        binding.catRecycler.setAdapter(categoryAdapter);

    }

    private void initBookletAdapter() {
        if (bookletsList.size() >= 3) {
            bookletManger.setOrientation(RecyclerView.HORIZONTAL);
        } else {
            bookletManger.setOrientation(RecyclerView.VERTICAL);

        }
        bookletAdapter = new BookletAdapter(getActivityy(), bookletsList, 4, this);
        binding.BookletRecycler.setAdapter(bookletAdapter);

    }

    private void initKitchenAdapter() {
        KitchenAdapter kitchenAdapter = new KitchenAdapter(getActivityy(), list, this, false, 10);
        binding.kitchenRecycler.setAdapter(kitchenAdapter);

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

                    binding.bookletLyLy.setVisibility(View.GONE);
                    binding.speLy.setVisibility(View.GONE);


                }


            } else {
                binding.bookletLyLy.setVisibility(View.GONE);
                binding.speLy.setVisibility(View.GONE);


            }


        }).getBookletsList(storeId);
    }

    public void getDinners(String lang) {
        list.clear();

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<DinnerModel>> result = (ResultAPIModel<ArrayList<DinnerModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {
                    Log.i(TAG, "Log getDinners size " + result.data.size());

                    binding.kitchenRecycler.setVisibility(View.VISIBLE);

                    list = result.data;
                    UtilityApp.setDinnersData(list);


                    initKitchenAdapter();

                } else {

                    binding.ramezKitchenLy.setVisibility(View.GONE);
                    binding.dataLY.setVisibility(View.VISIBLE);

                }


            } else {
                binding.ramezKitchenLy.setVisibility(View.GONE);

            }


        }).getDinnersList(lang);
    }


    @Override
    public void onBookletClicked(int position, BookletsModel bookletsModel) {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_BROUSHERS, true));
        FragmentManager fragmentManager = getParentFragmentManager();
        SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
        Bundle bundle = new Bundle();
        bookletsModel.setStoreID(city_id);
        bundle.putSerializable(Constants.bookletsModel, bookletsModel);

        specialOfferFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();


    }


    public void initSliderAdapter() {
        sliderAdapter = new MainSliderAdapter(getActivityy(), sliderList, this);
        binding.viewPager.setAdapter(sliderAdapter);

    }

    public void initBannersAdapter() {
        bannerAdapter = new BannersAdapter(getActivityy(), bannersList, this);
        binding.bannersRv.setAdapter(bannerAdapter);

    }


    public void initBrandsAdapter() {

        brandsAdapter = new BrandsAdapter(getActivityy(), brandsList, this, 10);
        binding.brandsRecycler.setAdapter(brandsAdapter);

    }

    @Override
    public void onBrandClicked(int position, BrandModel brandModel) {
        Intent intent = new Intent(getActivityy(), AllListActivity.class);
        intent.putExtra(Constants.LIST_MODEL_NAME, activity.getString(R.string.Brands));
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter);
        intent.putExtra(Constants.brand_id, brandModel.getId());
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int position, CategoryModel categoryModel) {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY_PRODUCT));
        FragmentManager fragmentManager = getParentFragmentManager();

        CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
//        bundle.putInt(Constants.SELECTED_POSITION, categoryModelList.get(position).getId());
//        bundle.putInt(Constants.position, position);
//        bundle.putSerializable(Constants.CAT_MODEL, categoryModel);
        bundle.putInt(Constants.MAIN_CAT_ID, categoryModel.getId());
        categoryProductsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();


    }


    @Override
    public void onBannersClicked(int position, Slider slider) {

        if (slider.getReffrenceType() == 1) {
            Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
            intent.putExtra(Constants.product_id, slider.getReffrence());
            intent.putExtra(Constants.FROM_BROSHER, true);
            startActivity(intent);

        } else if (slider.getReffrenceType() == 2) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY_PRODUCT));
            FragmentManager fragmentManager = getParentFragmentManager();
            CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
            bundle.putInt(Constants.SUB_CAT_ID, Integer.parseInt(slider.getReffrence()));
            categoryProductsFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();


        } else if (slider.getReffrenceType() == 3) {
            String url = slider.getReffrence();
            ActivityHandler.OpenBrowser(getActivityy(), url);

        } else if (slider.getReffrenceType() == 5) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_BROUSHERS, true));
            FragmentManager fragmentManager = getParentFragmentManager();
            SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
            Bundle bundle = new Bundle();
            BookletsModel bookletsModel = new BookletsModel();
            bookletsModel.setId(Integer.parseInt(slider.getReffrence()));
            bookletsModel.setStoreID(city_id);
            bundle.putSerializable(Constants.bookletsModel, bookletsModel);
            specialOfferFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();


        } else if (slider.getReffrenceType() == 6) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY_PRODUCT));
            FragmentManager fragmentManager = getParentFragmentManager();
            CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
            bundle.putInt(Constants.SUB_CAT_ID, Integer.parseInt(slider.getReffrence()));
            categoryProductsFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();


        }


    }

    @Override
    public void onSliderClicked(int position, Slider slider) {
        Log.i("tag", "Log Reffrence" + slider.getReffrence());
        Log.i("tag", "Log ReffrenceType" + slider.getReffrenceType());

        if (slider.getReffrenceType() == 1) {

            Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
            intent.putExtra(Constants.product_id, slider.getReffrence());
            intent.putExtra(Constants.FROM_BROSHER, true);
            startActivity(intent);

        } else if (slider.getReffrenceType() == 2) {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY_PRODUCT));
            FragmentManager fragmentManager = getParentFragmentManager();
            CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
            String subCatId = slider.getReffrence() != null ? slider.getReffrence() : "0";
            bundle.putInt(Constants.SUB_CAT_ID, Integer.parseInt(subCatId));
            categoryProductsFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();


        } else if (slider.getReffrenceType() == 3) {
            Log.i("tag", "Log getReffrence" + slider.getReffrence());
            String url = slider.getReffrence();
            ActivityHandler.OpenBrowser(getActivityy(), url);

        } else if (slider.getReffrenceType() == 5) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_BROUSHERS, true));
            FragmentManager fragmentManager = getParentFragmentManager();
            SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
            Bundle bundle = new Bundle();
            BookletsModel bookletsModel = new BookletsModel();
            bookletsModel.setId(Integer.parseInt(slider.getReffrence()));
            bundle.putSerializable(Constants.bookletsModel, bookletsModel);
            specialOfferFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();


        } else if (slider.getReffrenceType() == 6) {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_CATEGORY_PRODUCT));
            FragmentManager fragmentManager = getParentFragmentManager();

            CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
            bundle.putInt(Constants.SUB_CAT_ID, Integer.parseInt(slider.getReffrence()));
            categoryProductsFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {

                if (data != null) {
                    boolean SEARCH_BY_CODE_byCode = data.getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false);
                    String CODE = data.getStringExtra(Constants.CODE);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CODE, CODE);
                    bundle.putBoolean(Constants.SEARCH_BY_CODE_byCode, SEARCH_BY_CODE_byCode);
                    searchFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commitAllowingStateLoss();

                }


            }

        }


    }


    @Override
    public void onKitchenClicked(int position, DinnerModel dinnerModel) {
        Intent intent = new Intent(getActivityy(), RamezKitchenActivity.class);
        intent.putExtra(Constants.DB_DINNER_MODEL, dinnerModel);
        startActivity(intent);

    }


    public int getItemPosition(int id) {
        for (int position = 0; position < categoryModelList.size(); position++)
            if (categoryModelList.get(position).getId() == id)

                return position;

        return 0;
    }


    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size) {
        productRecentsList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                FavouriteResultModel result = (FavouriteResultModel) obj;
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
                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.recentlyRecycler.setVisibility(View.VISIBLE);
                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productRecentsList = result.getData();
                            Log.i(TAG, "Log productList new " + productRecentsList.size());
                            initRecentAdapter();
                        } else {

                            binding.recentlyRecycler.setVisibility(View.GONE);
                            binding.recentlyLy.setVisibility(View.GONE);
                        }


                    } else {

                        binding.recentlyRecycler.setVisibility(View.GONE);
                        binding.recentlyLy.setVisibility(View.GONE);


                    }
                }
            }

        }).getFavorite(category_id, country_id, city_id, user_id, filter, brand_id, page_number, page_size);
    }


}