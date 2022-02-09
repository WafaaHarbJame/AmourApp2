package com.ramez.shopp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.onesignal.OneSignal;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.GlobalData;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.activities.ConfirmActivity;
import com.ramez.shopp.activities.ConfirmPhoneActivity;
import com.ramez.shopp.databinding.FragmentLoginBinding;

import java.util.Random;

public class LoginFragment extends FragmentBase {

    final String TAG = "Log";
    String FCMToken;
    String CountryCode = "+966";
    boolean select_country = false;
    String country_name = "BH";
    String city_id = "7263";
    LocalModel localModel;
    int cartNumber=0;
    int storeId, userId;
    MemberModel user;
    int countryId;
    private FragmentLoginBinding binding;
    private ViewPager viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        viewPager = container.findViewById(R.id.viewPager);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        localModel = UtilityApp.getLocalData() != null ?
                UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());
        countryId=localModel.getCountryId();

        getDeviceToken();

        CountryCode= String.valueOf(localModel.getPhonecode());
        String intro= GlobalData.INSTANCE.INSTANCE.getIntro(CountryCode);
        Log.i(getClass().getSimpleName(),"Log get  Intro "+intro);
        binding.edtPhoneNumber.setHint(intro);

        initListeners();
    }

    private void initListeners() {

        binding.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
        binding.textForgotPassword.setOnClickListener(view1 -> {
            startRestPassword();
        });

        binding.skipButton.setOnClickListener(view1 -> {
            startMain();
        });

        binding.loginBut.setOnClickListener(view1 -> {
            if (isValidForm()) {
                loginUser();

            }


        });


        binding.loginGoogleBut.setOnClickListener(view1 -> {
//            googleSignIn();


        });

        binding.loginFacebookBut.setOnClickListener(view1 -> {
//            facebookSignIn();
        });

        binding.registerBut.setOnClickListener(view1 -> {
            startLogin();


        });


        binding.loginTwitterBut.setOnClickListener(view1 -> {
            //  twitterSignIn();
        });

        binding.showPassBut.setOnClickListener(view1 -> {

            if (binding.edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

    }

    private void loginUser() {

        final String mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString());
        final String passwordStr = NumberHandler.arabicToDecimal(binding.edtPassword.getText().toString());

        final MemberModel memberModel = new MemberModel();
        memberModel.setMobileNumber(mobileStr);
        memberModel.setPassword(passwordStr);
        memberModel.setDeviceType(Constants.deviceType);
        memberModel.setDeviceToken(FCMToken);
        memberModel.setDeviceId(UtilityApp.getUnique());
        memberModel.setUserType(Constants.user_type);
        memberModel.setStoreId(Integer.parseInt(localModel.getCityId()));
        memberModel.setCity(Integer.parseInt(localModel.getCityId()));

        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.text_login_login, R.string.please_wait_login);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                LoginResultModel result = (LoginResultModel) obj;

                switch (func) {
                    case Constants.ERROR:
                    case Constants.FAIL: {
                        String message = getString(R.string.fail_signin);
                        if (result != null && result.getMessage() != null) {
                            message = result.getMessage();
                        }
                        GlobalData.INSTANCE.hideProgressDialog();
                        GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.text_login_login, message);
                        break;
                    }
                    case Constants.NO_CONNECTION:

                        GlobalData.INSTANCE.hideProgressDialog();
                        GlobalData.INSTANCE.Toast(getActivityy(), R.string.no_internet_connection);

                        break;
                    default:
                        if (IsSuccess) {

                            if (result != null) {
                                if (result.getStatus() == 106) {
                                    GlobalData.INSTANCE.hideProgressDialog();
                                    Intent intent = new Intent(getActivityy(), ConfirmActivity.class);
                                    intent.putExtra(Constants.KEY_MOBILE, mobileStr);
                                    intent.putExtra(Constants.verify_account, true);
                                    intent.putExtra(Constants.KEY_PASSWORD, passwordStr);
                                    startActivity(intent);

                                } else if (result.getStatus() == 0) {

                                    GlobalData.INSTANCE.hideProgressDialog();
                                    String message = getString(R.string.fail_signin);
                                    if (result.getMessage() != null) {
                                        message = result.getMessage();
                                    }

                                    GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.text_login_login, message);

                                } else if (result.getStatus() == 200) {

                                    MemberModel user = result.getData();
                                    if (user != null) {
                                        user.setUserType(Constants.user_type);
                                        user.setPassword(passwordStr);

                                    }



                                    UtilityApp.setUserData(user);
                                    UtilityApp.setUserToken(result.getToken());
                                    UtilityApp.setRefreshToken(result.getRefreshToken());
                                    Log.i(TAG, "Log token  user token " + UtilityApp.getUserToken());
                                    Log.i(TAG, "Log token refersh token " + UtilityApp.getRefreshToken());

                                    UtilityApp.setIsFirstLogin(true);
                                    if (UtilityApp.getUserData() != null)
                                    {
                                        UpdateToken();
                                    }
                                } else {
                                    GlobalData.INSTANCE.hideProgressDialog();

                                    String message = getString(R.string.fail_signin);
                                    if (result.getMessage() != null) {
                                        message = result.getMessage();
                                    }

                                    GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.text_login_login, message);
                                }
                            }


                        } else {
                            GlobalData.INSTANCE.hideProgressDialog();

                            Toast(getString(R.string.fail_signin));

                        }
                        break;
                }
            }

        }).loginHandle(memberModel);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startRestPassword() {

        Intent intent = new Intent(getActivityy(), ConfirmPhoneActivity.class);
        intent.putExtra(Constants.reset_account, true);
        startActivity(intent);
    }

    private void startLogin() {
        viewPager.setCurrentItem(0);


    }

    private final boolean isValidForm() {
        FormValidator formValidator = FormValidator.Companion.getInstance();

        return formValidator.addField(binding.edtPhoneNumber, new NonEmptyRule(getString(R.string.enter_phone_number))).addField(binding.edtPassword, new NonEmptyRule(R.string.enter_password)).validate();


    }

    private void getDeviceToken() {

        FCMToken = UtilityApp.getFCMToken();
        if (FCMToken == null) {
            String FCMToken= OneSignal.getDeviceState().getUserId();
            UtilityApp.setFCMToken(FCMToken);

//            OneSignal.idsAvailable((userId, registrationId) -> {
//                Log.d("debug", "Log User:" + userId);
//                if (registrationId != null)
//                    FCMToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
//                UtilityApp.setFCMToken(FCMToken);
//
//                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
//                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());
//
//            });

        }

    }

    public void startMain() {
        Intent intent = new Intent(getActivityy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivityy().finish();

    }

    private void UpdateToken() {

//        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.text_login_login, R.string.please_wait_login);
        MemberModel memberModel = UtilityApp.getUserData();
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                ResultAPIModel<String> result = (ResultAPIModel) obj;
                if (func.equals(Constants.ERROR)) {
                    String message = getString(R.string.fail_signin);
                    GlobalData.INSTANCE.hideProgressDialog();

                    if (result != null && result.message != null) {
                        message = result.message;
                    }
                    GlobalData.INSTANCE.hideProgressDialog();

                    Toast(message);

                } else {
                    if (IsSuccess) {
                        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());
                        storeId = Integer.parseInt(localModel.getCityId());
                        user = UtilityApp.getUserData();
                        userId = user.getId();

                        getCarts(storeId, userId);

                    } else {
                        Toast(getString(R.string.fail_signin));
                        GlobalData.INSTANCE.hideProgressDialog();

                    }
                }

            }

        }).UpdateTokenHandle(memberModel);

    }

    private void facebookSignIn() {

//        if (AccessToken.getCurrentAccessToken() != null) {
//            signOut();
//        }
//
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
//
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onCancel() {
//                Log.d(TAG, " Log facebook:onCancel");
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "Log facebook:onError", error);
//
//            }
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//
//
//            }
//        });
    }

    private void twitterSignIn() {

//        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
//        provider.addCustomParameter("lang", UtilityApp.getLanguage());
//
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//
//        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());
//
//        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTosAndPrivacyPolicyUrls("https://example.com/terms.html", "https://example.com/privacy.html").build(),
//
//                TWITTER_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


//        if (resultCode == RESULT_OK) {
//            if (requestCode == RC_SIGN_IN) {
//
//                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//                try {
//
//                    GoogleSignInAccount account = task.getResult(ApiException.class);
//                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                    firebaseAuthWithGoogle(task.getResult().getIdToken());
//
//                } catch (ApiException e) {
//
//                    Log.w(TAG, "Google sign in failed", e);
//
//                }
//            } else if (requestCode == TWITTER_SIGN_IN) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                Log.i(TAG, "Log" + user.getPhoneNumber());
//                Log.i(TAG, "Log" + user.getEmail());
//                Log.i(TAG, "Log" + user.getDisplayName());
//
//
//            } else {
//                callbackManager.onActivityResult(requestCode, resultCode, data);
//
//            }
//        }

    }


    private void googleSignIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void RegisterUser(String nameStr, String emailStr) {
        country_name = localModel.getShortname();
        CountryCode = String.valueOf(localModel.getPhonecode());
        city_id = localModel.getCityId();

        Random r = new Random();
        int randomNumber = r.nextInt(100000000);

        MemberModel memberModel = new MemberModel();
        memberModel.setMobileNumber(String.valueOf(randomNumber));
        memberModel.setPassword(String.valueOf(randomNumber));
        memberModel.setName(nameStr);
        memberModel.setEmail(emailStr);
        memberModel.setCity(Integer.parseInt(city_id));
        memberModel.setCountry(country_name);
        memberModel.setDeviceToken(FCMToken);
        memberModel.setDeviceId(UtilityApp.getUnique());
        memberModel.setDeviceType(Constants.deviceType);
        memberModel.setPrefix(CountryCode);
        memberModel.setUserType(Constants.user_type);


        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.register, R.string.please_wait_register);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                GlobalData.INSTANCE.hideProgressDialog();
                LoginResultModel result = (LoginResultModel) obj;
                if (func.equals(Constants.ERROR)) {
                    String message = getString(R.string.fail_register);
                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
                    GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.fail_register, message);
                } else {
                    if (IsSuccess) {
                        Log.i("TAG", "Log otp " + result.getOtp());
                        MemberModel user = result.getData();
                        //  user.setRegisterType(Constants.BY_SOCIAL);
                        UtilityApp.setUserData(user);
                        Intent intent = new Intent(getActivityy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Toast(getString(R.string.fail_register));

                    }
                }
            }


        }).RegisterHandle(memberModel);


    }

    private void firebaseAuthWithGoogle(String idToken) {
//
//        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.text_login_login, R.string.please_wait_login);
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivityy(), task -> {
//            GlobalData.INSTANCE.hideProgressDialog();
//
//            if (task.isSuccessful()) {
//
//                Log.d(TAG, "signInWithCredential:success");
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                Log.i(TAG, "Log nameStr" + user.getDisplayName());
//                Log.i(TAG, "Log getEmail" + user.getEmail());
//                Log.i(TAG, "Log Uid" + user.getUid());
//                Log.i(TAG, "Log getServerAuthCode" + user.getIdToken(true));
//                Log.i(TAG, "Log getPhotoUrl" + user.getPhotoUrl());
//
//                RegisterUser(user.getDisplayName(), user.getEmail());
//
//            } else {
//
//                Log.w(TAG, "signInWithCredential:failure", task.getException());
//                GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.fail_register, getString(R.string.fail_register));
//
//            }
//
//            GlobalData.INSTANCE.hideProgressDialog();
//        });
    }


    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivityy(), task -> {
//            if (task.isSuccessful()) {
//
//                Log.d(TAG, "Log signInWithCredential:success");
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                RegisterUser(user.getDisplayName(), user.getEmail());
//
//            } else {
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "Log signInWithCredential:failure", task.getException());
//                Toast("Authentication failed");
//                GlobalData.INSTANCE.errorDialog(getActivityy(), R.string.fail_register, getString(R.string.fail_register));
//
//            }
//
//            // ...
//        });
    }


    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            RegisterUser(currentUser.getDisplayName(), currentUser.getEmail());
//
//        }


    }


    public void signOut() {
//        firebaseAuth.signOut();
//        LoginManager.getInstance().logOut();
    }


    public void getCarts(int storeId, int userId) {

//        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.text_login_login, R.string.please_wait_login);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                GlobalData.INSTANCE.hideProgressDialog();
                CartResultModel cartResultModel = (CartResultModel) obj;
                String message = getString(R.string.fail_to_get_data);

                if (IsSuccess) {
                    if (cartResultModel.getData() != null && cartResultModel.getData().getCartData() != null && cartResultModel.getData().getCartData().size() > 0) {
                        cartNumber = cartResultModel.getData().getCartCount();
                        Log.i(getClass().getSimpleName(),"Log cartNumber"+cartNumber);
                        UtilityApp.setCartCount(cartNumber);
                        Intent intent = new Intent(getActivityy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        UtilityApp.setCartCount(0);
                        Intent intent = new Intent(getActivityy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    getActivity().finish();


                }
                else {
                    GlobalData.INSTANCE.hideProgressDialog();

                }
            }

        }).GetCarts(storeId, userId);
    }


}