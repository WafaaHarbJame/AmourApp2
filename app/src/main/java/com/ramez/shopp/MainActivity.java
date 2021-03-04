package com.ramez.shopp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.BuildConfig;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
import com.ramez.shopp.Activities.ActivityBase;
import com.ramez.shopp.Activities.ExtraRequestActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.ConfirmDialog;
import com.ramez.shopp.Fragments.CartFragment;
import com.ramez.shopp.Fragments.CategoryFragment;
import com.ramez.shopp.Fragments.HomeFragment;
import com.ramez.shopp.Fragments.MyAccountFragment;
import com.ramez.shopp.Fragments.OfferFragment;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Utils.ActivityHandler;
import com.ramez.shopp.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;

import ru.nikartm.support.BadgePosition;

import static android.content.ContentValues.TAG;

public class MainActivity extends ActivityBase {
    int cartCount = 0;
    int storeId;
    LocalModel localModel;
    private ActivityMainBinding binding;
    private boolean toggleButton = false;

    @SuppressLint("UnsafeExperimentalUsageError")
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

        //getValidation();

        getIntentExtra();

        localModel = UtilityApp.getLocalData();

        storeId = Integer.parseInt(localModel.getCityId());

        if (UtilityApp.isLogin()) {
            getCartsCount();
        }

        binding.homeButton.setOnClickListener(view1 -> {
            binding.toolBar.backBtn.setVisibility(View.GONE);

            initBottomNav(0);

            binding.toolBar.addExtra.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }

        });

        binding.categoryButton.setOnClickListener(view1 -> {
            binding.toolBar.backBtn.setVisibility(View.GONE);

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

            initBottomNav(2);

            if (UtilityApp.isLogin()) {
                binding.toolBar.addExtra.setVisibility(View.VISIBLE);
                binding.toolBar.deleteBut.setVisibility(View.VISIBLE);


            } else {
                binding.toolBar.addExtra.setVisibility(View.GONE);
                binding.toolBar.deleteBut.setVisibility(View.GONE);

            }


            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();

        });

        binding.offerButton.setOnClickListener(view1 -> {
            binding.toolBar.backBtn.setVisibility(View.GONE);

            initBottomNav(3);

            binding.toolBar.addExtra.setVisibility(View.GONE);
            binding.toolBar.deleteBut.setVisibility(View.GONE);


            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new OfferFragment(), "OfferFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }
        });

        binding.myAccountButton.setOnClickListener(view1 -> {
            binding.toolBar.backBtn.setVisibility(View.GONE);

            initBottomNav(4);

            binding.toolBar.addExtra.setVisibility(View.GONE);
            binding.toolBar.deleteBut.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MyAccountFragment(), "MyAccountFragment").commit();
            if (UtilityApp.isLogin()) {
                getCartsCount();
            }


        });

        binding.toolBar.view2But.setOnClickListener(view11 -> {

            toggleButton = !toggleButton;

            if (toggleButton) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_view, 1));

                binding.toolBar.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view_white));

            } else {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_view, 2));

                binding.toolBar.view2But.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.filter_view2));

            }


        });

        binding.toolBar.sortBut.setOnClickListener(view11 -> {

            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_SORT));


        });

        binding.toolBar.addExtra.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActiviy(), ExtraRequestActivity.class);
            startActivity(intent);
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

        if (event.type.equals(MessageEvent.TYPE_invoice)) {

            binding.toolBar.backBtn.setVisibility(View.VISIBLE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);

            binding.toolBar.backBtn.setOnClickListener(view -> {

                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();
                binding.toolBar.backBtn.setVisibility(View.GONE);


            });

        } else if (event.type.equals(MessageEvent.TYPE_POSITION)) {

            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);

            binding.tab1Txt.setTextColor(ContextCompat.getColor(getActiviy(), R.color.colorPrimaryDark));
            int position = (int) event.data;

            if (position == 0) {
                initBottomNav(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();

            } else if (position == 1) {
                initBottomNav(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CategoryFragment(), "CategoryFragment").commit();

            } else if (position == 2) {
                initBottomNav(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();

            } else if (position == 3) {
                initBottomNav(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new OfferFragment(), "OfferFragment").commit();


            } else if (position == 4) {
                initBottomNav(4);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MyAccountFragment(), "MyAccountFragment").commit();

            }


        } else if (event.type.equals(MessageEvent.TYPE_CATEGORY_PRODUCT)) {
            binding.toolBar.backBtn.setVisibility(View.VISIBLE);
            binding.toolBar.view2But.setVisibility(View.VISIBLE);
            initBottomNav(0);
            binding.toolBar.backBtn.setOnClickListener(view -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);

            });


        } else if (event.type.equals(MessageEvent.TYPE_main)) {
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);

            initBottomNav(0);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();


        } else if (event.type.equals(MessageEvent.TYPE_UPDATE_CART)) {
            getCartsCount();

        } else if (event.type.equals(MessageEvent.TYPE_view)) {
            binding.toolBar.backBtn.setVisibility(View.VISIBLE);
            binding.toolBar.view2But.setVisibility(View.VISIBLE);

        } else if (event.type.equals(MessageEvent.TYPE_search)) {
            binding.toolBar.backBtn.setVisibility(View.VISIBLE);
            binding.toolBar.view2But.setVisibility(View.VISIBLE);
            binding.toolBar.sortBut.setVisibility(View.VISIBLE);

            binding.toolBar.backBtn.setOnClickListener(view -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment(), "HomeFragment").commit();
                binding.toolBar.backBtn.setVisibility(View.GONE);
                binding.toolBar.view2But.setVisibility(View.GONE);
                binding.toolBar.sortBut.setVisibility(View.GONE);


            });


        } else if (event.type.equals(MessageEvent.TYPE_SORT)) {

            binding.toolBar.backBtn.setVisibility(View.VISIBLE);
            binding.toolBar.view2But.setVisibility(View.VISIBLE);
            binding.toolBar.sortBut.setVisibility(View.VISIBLE);
        } else {
            binding.toolBar.backBtn.setVisibility(View.GONE);
            binding.toolBar.deleteBut.setVisibility(View.GONE);
            binding.toolBar.sortBut.setVisibility(View.GONE);
            binding.toolBar.view2But.setVisibility(View.GONE);
            binding.toolBar.addExtra.setVisibility(View.GONE);


        }


    }

    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            boolean TO_CART = getIntent().getBooleanExtra(Constants.CART, false);

            if (TO_CART) {
                initBottomNav(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new CartFragment(), "CartFragment").commit();


            }


        }
    }


    public void getValidation() {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {

                GeneralModel result = (GeneralModel) obj;

                if (result.getMessage() != null) {
                    if (result.getStatus().equals(Constants.OK_STATUS)) {
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

                        new ConfirmDialog(getActiviy(), R.string.updateMessage, R.string.ok, R.string.cancel_label, click, cancel);

                    }
                    Log.i(TAG, "Log getValidation" + result.getMessage());

                }
            }

        }).getValidate(Constants.deviceType, UtilityApp.getAppVersionStr(), BuildConfig.VERSION_CODE);
    }


    @SuppressLint("UnsafeExperimentalUsageError")
    public void putBadge(int cartCount) {

        if (cartCount == 0) {

            binding.toolBar.deleteBut.setVisibility(View.GONE);
            binding.cartCountTv.setVisibility(View.GONE);

        }
        else {
            binding.cartCountTv.setVisibility(View.VISIBLE);
            binding.cartCountTv.setText(String.valueOf(cartCount));

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (UtilityApp.isLogin()) {
            getCartsCount();

        }

    }


}