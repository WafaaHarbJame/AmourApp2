package com.ramez.shopp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidnetworking.BuildConfig;
import com.onesignal.OneSignal;
import com.ramez.shopp.Activities.ActivityBase;
import com.ramez.shopp.Activities.AllBookleteActivity;
import com.ramez.shopp.Activities.ExtraRequestActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.ConfirmDialog;
import com.ramez.shopp.Fragments.CartFragment;
import com.ramez.shopp.Fragments.CategoryFragment;
import com.ramez.shopp.Fragments.CategoryProductsFragment;
import com.ramez.shopp.Fragments.HomeFragment;
import com.ramez.shopp.Fragments.MyAccountFragment;
import com.ramez.shopp.Fragments.OfferFragment;
import com.ramez.shopp.Fragments.SearchFragment;
import com.ramez.shopp.Fragments.SpecialOfferFragment;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Utils.ActivityHandler;
import com.ramez.shopp.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends ActivityBase {
    int cartCount = 0;
    int storeId = 7263;
    LocalModel localModel;
    private ActivityMainBinding binding;
    private boolean toggleButton = false;
    String country_name = "BH";
    ArrayList<CategoryModel> categoryModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
        binding.toolBar.backBtn.setVisibility(View.GONE);
        binding.toolBar.view2But.setVisibility(View.GONE);

        binding.homeButn.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.home_clicked));

        categoryModelList = new ArrayList<>();

        getValidation();

        initListeners();

        getIntentExtra();

        localModel = UtilityApp.getLocalData();
        if (localModel != null && localModel.getCityId() != null) {
            storeId = Integer.parseInt(localModel.getCityId());
            country_name = localModel.getShortname();
            OneSignal.sendTag(Constants.COUNTRY, country_name);


        }


        if (UtilityApp.isLogin()) {
            getCartsCount();
        }

        AnalyticsHandler.APP_OPEN();


    }

    private void initListeners() {
        binding.homeButton.setOnClickListener(view1 -> {

            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);

            try {
                initBottomNav(0);
            } catch (NumberFormatException ex) { // handle your exception
            }
            binding.toolBar.addExtra.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }

        });

        binding.categoryButton.setOnClickListener(view1 -> {
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.mainSearchBtn.setVisibility(View.GONE);

            initBottomNav(1);

            binding.toolBar.addExtra.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CategoryFragment(), "CategoryFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }
        });

        binding.cartButton.setOnClickListener(view1 -> {

            binding.cartCountTv.setVisibility(View.GONE);
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.mainSearchBtn.setVisibility(View.GONE);

            initBottomNav(2);

            if (UtilityApp.isLogin()) {
                binding.toolBar.addExtra.setVisibility(View.VISIBLE);
            } else {
                binding.toolBar.addExtra.setVisibility(View.GONE);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();

        });

        binding.offerButton.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type_offer));

            binding.toolBar.mainSearchBtn.setVisibility(View.GONE);
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.addExtra.setVisibility(View.GONE);

            initBottomNav(3);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new OfferFragment(), "OfferFragment").commit();

            if (UtilityApp.isLogin()) {
                getCartsCount();
            }
        });

        binding.myAccountButton.setOnClickListener(view1 -> {
            binding.toolBar.sortBut.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.mainSearchBtn.setVisibility(View.GONE);

            initBottomNav(4);

            binding.toolBar.addExtra.setVisibility(View.GONE);


            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MyAccountFragment(), "MyAccountFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }


        });

        binding.toolBar.view2But.setOnClickListener(view11 -> {

            toggleButton = !toggleButton;

            if (toggleButton) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_view, 1));

                binding.toolBar.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view2));

            } else {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_view, 2));

                binding.toolBar.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view_white));

            }

        });

        binding.toolBar.mainSearchBtn.setOnClickListener(view -> {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_search));

        });


        binding.toolBar.sortBut.setOnClickListener(view11 -> {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_SORT));

        });

        binding.toolBar.addExtra.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActiviy(), ExtraRequestActivity.class);
            startActivity(intent);
        });

        OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "Log User:" + userId);
            if (registrationId != null)

                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            Log.d("debug", "Log token one signal second  :" + registrationId);
            Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());

        });

    }

    private void initBottomNav(int pos) {
        binding.homeButn.setImageDrawable(ContextCompat.getDrawable(getActiviy(), pos == 0 ? R.drawable.home_clicked : R.drawable.home_icon));
        binding.categoryBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), pos == 1 ? R.drawable.category_click : R.drawable.category_icon));
        binding.cartBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), pos == 2 ? R.drawable.cart_icon_bottom : R.drawable.cart_icon_before));
        binding.offerBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), pos == 3 ? R.drawable.offer_clicked : R.drawable.offer_icon));
        binding.myAccountBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), pos == 4 ? R.drawable.my_account_clciked : R.drawable.myaccount_icon));
        binding.tab1Txt.setTextColor(ContextCompat.getColor(getActiviy(), pos == 0 ? R.color.colorPrimary : R.color.font_gray));
        binding.tab2Txt.setTextColor(ContextCompat.getColor(getActiviy(), pos == 1 ? R.color.colorPrimary : R.color.font_gray));
        binding.tab3Txt.setTextColor(ContextCompat.getColor(getActiviy(), pos == 2 ? R.color.colorPrimary : R.color.font_gray));
        binding.tab4Txt.setTextColor(ContextCompat.getColor(getActiviy(), pos == 3 ? R.color.colorPrimary : R.color.font_gray));
        binding.tab5Txt.setTextColor(ContextCompat.getColor(getActiviy(), pos == 4 ? R.color.colorPrimary : R.color.font_gray));
    }

    private void getCartsCount() {
        cartCount = UtilityApp.getCartCount();
//        System.out.println("Log cart count " + cartCount);
        putBadge(cartCount);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);

        if (UtilityApp.isLogin()) {
            getCartsCount();

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {

        binding.toolBar.mainSearchBtn.setVisibility(View.GONE);

        switch (event.type) {
            case MessageEvent.TYPE_invoice:
                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);
                binding.toolBar.addExtra.setVisibility(View.GONE);
                binding.toolBar.backBtn.setOnClickListener(view -> {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();
                    binding.toolBar.backBtn.setVisibility(View.GONE);

                });

                break;
            case MessageEvent.TYPE_BROUSHERS:
                //   boolean is_Home = (boolean) event.data;

                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);
                binding.toolBar.addExtra.setVisibility(View.GONE);

                binding.toolBar.backBtn.setOnClickListener(view -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                    binding.toolBar.backBtn.setVisibility(View.GONE);
                });

                break;
            case MessageEvent.TYPE_POSITION:

                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);

                binding.tab1Txt.setTextColor(ContextCompat.getColor(getActiviy(), R.color.colorPrimaryDark));
                int position = (int) event.data;

                Fragment selectedFragment;
                if (position == 0) {
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                    selectedFragment = new HomeFragment();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.REFRESH_CART));
                } else if (position == 1) {
                    selectedFragment = new CategoryFragment();
                } else if (position == 2) {
                    selectedFragment = new CartFragment();
                } else if (position == 3) {
                    selectedFragment = new OfferFragment();
                } else if (position == 4) {
                    selectedFragment = new MyAccountFragment();
                } else {
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                    selectedFragment = new HomeFragment();
                }

                initBottomNav(position);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, selectedFragment, "MyAccountFragment").commit();

                break;
            case MessageEvent.TYPE_CATEGORY_PRODUCT:
                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                initBottomNav(0);

                binding.toolBar.backBtn.setOnClickListener(view -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                    binding.toolBar.backBtn.setVisibility(View.GONE);
                    binding.toolBar.view2But.setVisibility(View.GONE);
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);

                });

                break;
            case MessageEvent.Type_offer:
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                break;
            case MessageEvent.TYPE_main:
                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);

                initBottomNav(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();

                break;
            case MessageEvent.TYPE_UPDATE_CART:
                getCartsCount();

                break;
            case MessageEvent.TYPE_view:
                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                break;
            case MessageEvent.TYPE_search:
//                binding.toolBar.mainSearchBtn.setVisibility(View.GONE);
                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                binding.toolBar.sortBut.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new SearchFragment(), "searchFragment").commit();

                binding.toolBar.backBtn.setOnClickListener(view -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                    binding.toolBar.backBtn.setVisibility(View.GONE);
                    binding.toolBar.view2But.setVisibility(View.GONE);
                    binding.toolBar.sortBut.setVisibility(View.GONE);
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                });
                break;
            case MessageEvent.TYPE_SORT:

                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                binding.toolBar.sortBut.setVisibility(View.VISIBLE);

                break;
            default:
                binding.toolBar.backBtn.setVisibility(View.GONE);

                binding.toolBar.sortBut.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.addExtra.setVisibility(View.GONE);

                break;
        }

    }

    private void getIntentExtra() {
        String searchTEXT = "";
        Bundle bundle = getIntent().getExtras();
        boolean from_inside_app = false;

        if (bundle != null) {
            boolean TO_CART = bundle.getBoolean(Constants.CART, false);
            String fragmentType = bundle.getString(Constants.KEY_OPEN_FRAGMENT, "");
//            boolean FRAG_HOME=bundle.getBoolean(Constants.FRAG_HOME);
            int subCatId = getIntent().getExtras().getInt(Constants.SUB_CAT_ID);
            BookletsModel bookletsModel = (BookletsModel) getIntent().getExtras().getSerializable(Constants.bookletsModel);
            searchTEXT = bundle.getString(Constants.inputType_text);
            from_inside_app = bundle.getBoolean(Constants.Inside_app);
            boolean TO_FRAG_HOME = bundle.getBoolean(Constants.TO_FRAG_HOME);


            if (TO_CART) {

                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_REFRESH));
                binding.cartButton.performClick();

            }

            if (TO_FRAG_HOME) {
                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);
                binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                binding.homeButton.performClick();
            } else if (fragmentType.equals(Constants.FRAG_CATEGORY_DETAILS)) {
                if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
                    categoryModelList = UtilityApp.getCategories();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                CategoryProductsFragment categoryProductsFragment = new CategoryProductsFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable(Constants.CAT_LIST, categoryModelList);
                bundle1.putInt(Constants.SUB_CAT_ID, subCatId);
                categoryProductsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryProductsFragment, "categoryProductsFragment").commit();

                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);

                binding.toolBar.backBtn.setOnClickListener(view -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                    binding.toolBar.backBtn.setVisibility(View.GONE);
                    binding.toolBar.view2But.setVisibility(View.GONE);
                    binding.toolBar.sortBut.setVisibility(View.GONE);
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                });

            } else if (fragmentType.equals(Constants.FRAG_CATEGORIES)) {
                binding.categoryButton.performClick();
            } else if (fragmentType.equals(Constants.FRAG_OFFERS)) {
                binding.offerButton.performClick();

            } else if (fragmentType.equals(Constants.FRAG_HOME)) {

                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);
                binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                binding.homeButton.performClick();


            } else if (fragmentType.equals(Constants.FRAG_SEARCH)) {

//                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_search));
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.inputType_text, searchTEXT);
                FragmentManager fragmentManager = getSupportFragmentManager();
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle2);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commit();

                binding.toolBar.backBtn.setVisibility(View.VISIBLE);
                binding.toolBar.view2But.setVisibility(View.VISIBLE);
                binding.toolBar.sortBut.setVisibility(View.VISIBLE);

                binding.toolBar.backBtn.setOnClickListener(view -> {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                    binding.toolBar.backBtn.setVisibility(View.GONE);
                    binding.toolBar.view2But.setVisibility(View.GONE);
                    binding.toolBar.sortBut.setVisibility(View.GONE);
                    binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                });


            } else if (fragmentType.equals(Constants.FRAG_BROSHORE)) {

                if (bookletsModel != null && bookletsModel.getId() != 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable(Constants.bookletsModel, bookletsModel);
                    bundle2.putBoolean(Constants.TO_BROSHER, true);
                    specialOfferFragment.setArguments(bundle2);
                    fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();

                    binding.toolBar.backBtn.setVisibility(View.VISIBLE);

                    boolean finalFrom_inside_app = from_inside_app;
                    binding.toolBar.backBtn.setOnClickListener(view -> {
                        if (finalFrom_inside_app) {
                            Intent intent = new Intent(getActiviy(), AllBookleteActivity.class);
                            intent.putExtra(Constants.Activity_type, Constants.BOOKLETS);
                            startActivity(intent);


                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                            binding.toolBar.backBtn.setVisibility(View.GONE);
                            binding.toolBar.view2But.setVisibility(View.GONE);
                            binding.toolBar.sortBut.setVisibility(View.GONE);
                            binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
                        }

                    });
                }


            }
        } else {
            binding.toolBar.mainSearchBtn.setVisibility(View.VISIBLE);
        }

    }

    public void getValidation() {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {

                GeneralModel result = (GeneralModel) obj;

                if (result != null && result.getMessage() != null) {
                    if (result.getStatus() == (Constants.OK_STATUS)) {
                        Log.i(TAG, "Log getValidation" + result.getMessage());

                    } else {

                        ConfirmDialog.Click click = new ConfirmDialog.Click() {
                            @Override
                            public void click() {
                                ActivityHandler.OpenGooglePlay(getActiviy());
                                //finish();


                            }
                        };

                        ConfirmDialog.Click cancel = new ConfirmDialog.Click() {
                            @Override
                            public void click() {
                                finish();


                            }
                        };

                        new ConfirmDialog(getActiviy(), getString(R.string.updateMessage), R.string.ok, R.string.cancel_label, click, cancel, false);

                    }
                    Log.i(TAG, "Log getValidation" + result.getMessage());

                }
            }

        }).getValidate(Constants.deviceType, UtilityApp.getAppVersionStr(), BuildConfig.VERSION_CODE);
    }


    @SuppressLint("UnsafeExperimentalUsageError")
    public void putBadge(int cartCount) {

        if (cartCount == 0) {


            binding.cartCountTv.setVisibility(View.GONE);

        } else {
            binding.cartCountTv.setVisibility(View.VISIBLE);
            binding.cartCountTv.setText(String.valueOf(cartCount));

        }

    }


}