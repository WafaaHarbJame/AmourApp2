package com.ramez.shopp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Activities.AboutActivity;
import com.ramez.shopp.Activities.AddressActivity;
import com.ramez.shopp.Activities.ChangeCityBranchActivity;
import com.ramez.shopp.Activities.ChangeLangCurrencyActivity;
import com.ramez.shopp.Activities.ChangePassActivity;
import com.ramez.shopp.Activities.ConditionActivity;
import com.ramez.shopp.Activities.ContactSupportActivity;
import com.ramez.shopp.Activities.EditProfileActivity;
import com.ramez.shopp.Activities.FavoriteActivity;
import com.ramez.shopp.Activities.FullScannerActivity;
import com.ramez.shopp.Activities.MyOrderActivity;
import com.ramez.shopp.Activities.PriceCheckerResultActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Activities.RatingActivity;
import com.ramez.shopp.Activities.RegisterLoginActivity;
import com.ramez.shopp.Activities.RewardsActivity;
import com.ramez.shopp.Activities.SplashScreenActivity;
import com.ramez.shopp.Activities.TermsActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.SoicalLink;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.ConfirmDialog;
import com.ramez.shopp.Dialogs.InfoDialog;
import com.ramez.shopp.Models.CountryDetailsModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ProfileData;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.ActivityHandler;
import com.ramez.shopp.Utils.FileUtil;
import com.ramez.shopp.databinding.FragmentMyAccountBinding;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

public class MyAccountFragment extends FragmentBase {
    boolean isLogin = false;
    MemberModel memberModel;
    int user_id = 0;
    private FragmentMyAccountBinding binding;
    private CheckLoginDialog checkLoginDialog;
    private SoicalLink soicalLink;
    private String whats_link = "";
    private String facebook_link = "";
    private String instagram_links = "";
    private String twitter_links = "";
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private int SEARCH_CODE = 2000;
    private String CODE = "";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        isLogin = UtilityApp.isLogin();

        binding.SupportBtn.setVisibility(View.GONE);


        if (UtilityApp.getLinks() != null) {
            soicalLink = UtilityApp.getLinks();
            twitter_links = soicalLink.getTwitterLink();
            facebook_link = soicalLink.getFacebookLink();
            instagram_links = soicalLink.getInstagramLink();
            whats_link = soicalLink.getWhatsappLink();

        } else {
            if (UtilityApp.getLocalData().getShortname() != null) {
                getLinks(UtilityApp.getLocalData().getShortname());
            }

        }


        binding.facebookBut.setOnClickListener(view1 -> {

            try {
                if (facebook_link != null) {
//                boolean installed = ActivityHandler.isPackageExist(getActivityy(),"com.facebook.katana");
//                if(installed) {
//
//                    System.out.println("App is already installed on your phone");
//                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
//                    startActivity(intent);
//                } else {
//                    Toast(getString(R.string.please_install_facebook));
//                    System.out.println("App is not currently installed on your phone");
//                }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
                    startActivity(intent);
                }

            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("Error " + e.getMessage());
            }


        });


        binding.whatsBut.setOnClickListener(view1 -> {

            try {
                if (whats_link != null) {
                    boolean installed = ActivityHandler.isPackageExist(getActivityy(), "com.whatsapp");
                    boolean installedBusiness = ActivityHandler.isPackageExist(getActivityy(), "com.whatsapp.w4b");
                    if (installed) {
                        System.out.println("App is already installed on your phone");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whats_link));
                        startActivity(intent);
                    }

                   else if (installedBusiness) {
                        System.out.println("App is already installed on your phone");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whats_link));
                        startActivity(intent);
                    }


                    else {
                        Toast(getString(R.string.please_install_whats));
                        System.out.println("App is not currently installed on your phone");
                    }


                }

            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("Error " + e.getMessage());
            }


        });


        binding.twitterBut.setOnClickListener(view1 -> {
            try {

                if (twitter_links != null) {
                    boolean installed = ActivityHandler.isPackageExist(getActivityy(), "com.twitter.android");
                    if (installed) {

                        System.out.println("App is already installed on your phone");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_links));
                        startActivity(intent);
                    } else {
                        Toast(getString(R.string.please_twitter));
                        System.out.println("App is not currently installed on your phone");
                    }

                }
            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("Error " + e.getMessage());
            }


        });

        binding.instBut.setOnClickListener(view1 -> {
            try {

                if (instagram_links != null) {
                    boolean installed = ActivityHandler.isPackageExist(getActivityy(), "com.instagram.android");
                    if (installed) {

                        System.out.println("App is already installed on your phone");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram_links));
                        startActivity(intent);
                    } else {
                        Toast(getString(R.string.please_install_instagram));
                        System.out.println("App is not currently installed on your phone");
                    }

                }

            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("Error " + e.getMessage());
            }


        });


        if (UtilityApp.isLogin()) {
            binding.viewLogin.setVisibility(View.GONE);
            binding.logoutText.setText(R.string.logout);
            binding.editProfileBu.setVisibility(View.VISIBLE);

            if (UtilityApp.getUserData() != null) {

                memberModel = UtilityApp.getUserData();
                if (memberModel != null) {
                    initData(memberModel);

                }
//                if(memberModel.getRegisterType().equals(Constants.BY_SOCIAL)){
//                    binding.changePassBtn.setVisibility(View.GONE);
//                }


            } else {
                if (memberModel != null && memberModel.getId() != null) {
                    getUserData(memberModel.getId(),memberModel.getStoreId());

                }
            }

        } else {

            binding.logoutText.setText(R.string.text_login_login);
            binding.viewLogin.setVisibility(View.VISIBLE);
            binding.editProfileBu.setVisibility(View.GONE);
            binding.addressBtn.setVisibility(View.GONE);
            binding.changePassBtn.setVisibility(View.GONE);

        }


        binding.termsBtn.setOnClickListener(view1 -> {
            startTermsActivity();
        });


        binding.conditionsBtn.setOnClickListener(view1 -> {
            startConditionActivity();

        });

        binding.aboutUsBtn.setOnClickListener(view1 -> {
            startAboutActivity();

        });
        binding.rateBtn.setOnClickListener(view1 -> {

            if (isLogin) {
                startRateAppActivity();
            } else {
                showDialog(R.string.to_rate_app);
            }
        });


        binding.priceCheckerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();

            }
        });





        binding.shareBtn.setOnClickListener(view1 -> {

            final String appPackageName = getActivityy().getPackageName();
            // getPackageName() from Context or Activity object
            ActivityHandler.shareTextUrl(getActivityy(), getString(R.string.app_share_text1), String.valueOf(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), "https://apps.apple.com/bh/app/ramez-%D8%B1%D8%A7%D9%85%D8%B2/id1509927576", "");

        });

        binding.changePassBtn.setOnClickListener(view1 -> {
            if (isLogin) {
                startChangeActivity();
            } else {
                showDialog(R.string.to_change_pass);
            }


        });

        binding.favProductBut.setOnClickListener(view1 -> {
            if (isLogin) {
                startFavProductActivity();
            } else {
                showDialog(R.string.to_show_products);
            }


        });

        binding.myOrderBut.setOnClickListener(view1 -> {

            if (isLogin) {
                startOrderActivity();
            } else {
                showDialog(R.string.to_show_orders);
            }


        });

        binding.ramezRewardBtn.setOnClickListener(v -> {
            CheckLoyal();

                }
                );

        binding.editProfileBu.setOnClickListener(view1 -> {

            startEditProfileActivity();

        });



        binding.addressBtn.setOnClickListener(view1 -> {
            if (isLogin) {
                startAddressActivity();
            } else {
                showDialog(R.string.to_show_address);
            }


        });
        binding.changeCityBtn.setOnClickListener(view1 -> {

            startChangeBranch();


        });

        binding.changeLangBtn.setOnClickListener(view1 -> {
            startChangeLang();
        });

        binding.SupportBtn.setOnClickListener(view1 -> {
            if (isLogin) {
                startSupport();
            } else {
                showDialog(R.string.to_contact_support);
            }


        });

        binding.logoutBtn.setOnClickListener(view1 -> {

            if (UtilityApp.isLogin()) {
                MemberModel memberModel = UtilityApp.getUserData();
                if(memberModel!=null&memberModel.getId()!=null){
                    signOut(memberModel);

                }

            } else {
                startLogin();

            }

        });
        return view;
    }

    private void initData(MemberModel memberModel) {
        if (memberModel != null && memberModel.getId() != null) {
            user_id = memberModel.getId();
            binding.usernameTV.setText(memberModel.getName());
            binding.emailTv.setText(memberModel.getEmail());
            Glide.with(getActivityy()).asBitmap().load(memberModel.getProfilePicture()).placeholder(R.drawable.avatar).into(binding.userImg);

        }
            }

    private void showDialog(int message) {
        CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.LoginFirst, message, R.string.ok, R.string.cancel, null, null);
        checkLoginDialog.show();
        checkLoginDialog.show();
    }

    private void startLogin() {
        Intent intent = new Intent(getActivityy(), RegisterLoginActivity.class);
        intent.putExtra(Constants.LOGIN, true);
        startActivity(intent);
    }

    private void startSupport() {
        Intent intent = new Intent(getActivityy(), ContactSupportActivity.class);
        startActivity(intent);
    }

    private void startChangeLang() {
        Intent intent = new Intent(getActivityy(), ChangeLangCurrencyActivity.class);
        startActivity(intent);
    }

    private void startChangeBranch() {
        Intent intent = new Intent(getActivityy(), ChangeCityBranchActivity.class);
        startActivity(intent);
    }

    private void startAddressActivity() {
        Intent intent = new Intent(getActivityy(), AddressActivity.class);
        startActivity(intent);
    }

    private void startOrderActivity() {
        Intent intent = new Intent(getActivityy(), MyOrderActivity.class);
        startActivity(intent);
    }

    private void startRewardsActivity() {
        Intent intent = new Intent(getActivityy(), RewardsActivity.class);
        startActivity(intent);
    }


    private void startEditProfileActivity() {
        Intent intent = new Intent(getActivityy(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void startTermsActivity() {
        Intent intent = new Intent(getActivityy(), TermsActivity.class);
        startActivity(intent);
    }

    private void startConditionActivity() {
        Intent intent = new Intent(getActivityy(), ConditionActivity.class);
        startActivity(intent);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(getActivityy(), AboutActivity.class);
        startActivity(intent);
    }

    private void startRateAppActivity() {
        Intent intent = new Intent(getActivityy(), RatingActivity.class);
        startActivity(intent);
    }

    private void startChangeActivity() {
        Intent intent = new Intent(getActivityy(), ChangePassActivity.class);
        startActivity(intent);
    }

    private void startFavProductActivity() {
        Intent intent = new Intent(getActivityy(), FavoriteActivity.class);
        startActivity(intent);
    }

    public void signOut(MemberModel memberModel) {
        ConfirmDialog.Click click = new ConfirmDialog.Click() {
            @Override
            public void click() {
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (isVisible()) {

                        if (func.equals(Constants.ERROR)) {
                        Toast(R.string.fail_to_sign_out);
                    } else if (func.equals(Constants.FAIL)) {
                        Toast(R.string.fail_to_sign_out);
                    } else if (func.equals(Constants.NO_CONNECTION)) {
                        GlobalData.Toast(getActivityy(), R.string.no_internet_connection);
                    } else {

                        if (IsSuccess) {

                            UtilityApp.logOut();
                            GlobalData.Position = 0;

                            Intent intent = new Intent(getActivityy(), SplashScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast(R.string.fail_to_sign_out);
                        }
                    }

                }
            }).logOut(memberModel);
            }
        };

        new ConfirmDialog(getActivityy(),getString( R.string.want_to_signout), R.string.ok, R.string.cancel_label, click, null,false);

    }


    public void getUserData(int user_id,int store_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (isVisible()) {
                ResultAPIModel<ProfileData> result = (ResultAPIModel<ProfileData>) obj;
                String message = getString(R.string.fail_to_get_data);

                if (IsSuccess) {

                    MemberModel memberModel = UtilityApp.getUserData();
                    memberModel.setName(result.data.getName());
                    memberModel.setEmail(result.data.getEmail());
                    memberModel.setId(result.data.getId());
                    memberModel.setLoyalBarcode(result.data.getLoyalBarcode());
                    memberModel.setProfilePicture(result.data.getProfilePicture());
                    initData(memberModel);
                    UtilityApp.setUserData(memberModel);


                }
            }


        }).getUserDetails(user_id,store_id);
    }

    @Override
    public void onResume() {
        if (UtilityApp.isLogin()) {
            memberModel = UtilityApp.getUserData();
            initData(memberModel);
        }

        super.onResume();
    }


    public void getLinks(String shortName) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SoicalLink> result = (ResultAPIModel<SoicalLink>) obj;

            if (isVisible()) {

                if (IsSuccess) {
                    if (result.data != null) {
                        soicalLink = result.data;
                        UtilityApp.SetLinks(soicalLink);
                        if (soicalLink.getTwitterLink() != null) {
                            twitter_links = soicalLink.getTwitterLink();

                        }
                        if (soicalLink.getFacebookLink() != null) {
                            facebook_link = soicalLink.getFacebookLink();

                        }

                        if (soicalLink.getInstagramLink() != null) {
                            instagram_links = soicalLink.getInstagramLink();

                        }

                        if (soicalLink.getWhatsappLink() != null) {
                            whats_link = soicalLink.getWhatsappLink();

                        }

                    }

                }
            }


        }).getLinks(shortName);
    }

    private void checkCameraPermission() {
        Dexter.withContext(getActivityy()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startScan();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getActivityy(), "" + getActivityy().getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(error -> Toast.makeText(getActivityy(), "" + getActivityy().getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

     if (requestCode == SEARCH_CODE) {

            if (data != null) {
                CODE = data.getStringExtra(Constants.CODE);
                Intent intent = new Intent(getActivityy(), PriceCheckerResultActivity.class);
                ProductModel productModel=new ProductModel();
                intent.putExtra(Constants.DB_productModel, productModel);
                startActivity(intent);

            }


        }
    }


    private void CheckLoyal() {
         CountryDetailsModel countryDetailsModel= DBFunction.getLoyal();
        if (countryDetailsModel == null) {
            if( UtilityApp.getLocalData()!=null&&UtilityApp.getLocalData().getShortname()!=null)
            getCountryDetail(UtilityApp.getLocalData().getShortname());

        } else {
            boolean hasLoyal=countryDetailsModel.hasLoyal;
            if(hasLoyal){
                startRewardsActivity();

            }
            else
            Toasty.warning(getActivityy(),getString(R.string.no_active), Toast.LENGTH_SHORT, true).show();

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
                }


            }

        }).getCountryDetail(shortName);
    }




}