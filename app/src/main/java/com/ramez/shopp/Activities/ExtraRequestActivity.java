package com.ramez.shopp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.PickImageDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.AddExtraCall;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.FileUtil;
import com.ramez.shopp.databinding.ActivityExtraRequestBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.OkHttpClient;

public class ExtraRequestActivity extends ActivityBase {
    ActivityExtraRequestBinding binding;
    PickImageDialog pickImageDialog;
    int REQUEST_PICK_IMAGE = 11;
    File selectedPhotoFil = null;
    private int count = 1;
    private int user_id, store_id;
    private MemberModel user;
    private LocalModel localModel;
    private ChoosePhotoHelper choosePhotoHelper;
    private Uri selectedPhotoUri;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private int SEARCH_CODE = 2000;
    private String country;
    private String CODE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExtraRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("");

        user = UtilityApp.getUserData();
        localModel = UtilityApp.getLocalData();

        if (UtilityApp.getUserData() != null) {
            store_id = Integer.parseInt(localModel.getCityId());
            user_id = UtilityApp.getUserData().getId();

        }

        initListener();

    }

    private void initListener() {

        binding.plusCartBtn.setOnClickListener(v -> {
            count = Integer.parseInt(binding.productCartQTY.getText().toString());
            if (count == 1) {
                binding.minusCartBtn.setVisibility(View.VISIBLE);
                binding.deleteCartBtn.setVisibility(View.GONE);
            }

            count++;
            binding.productCartQTY.setText(String.valueOf(count));


        });


        binding.minusCartBtn.setOnClickListener(v -> {
            count = Integer.parseInt(binding.productCartQTY.getText().toString());
            binding.productCartQTY.setText(String.valueOf(count));
            if (count == 1) {
                binding.minusCartBtn.setVisibility(View.GONE);
                binding.deleteCartBtn.setVisibility(View.VISIBLE);

            } else {
                binding.minusCartBtn.setVisibility(View.VISIBLE);
                binding.deleteCartBtn.setVisibility(View.GONE);
                count--;
                if (count == 1) {
                    binding.minusCartBtn.setVisibility(View.GONE);
                    binding.deleteCartBtn.setVisibility(View.VISIBLE);
                }


            }
            binding.plusCartBtn.setVisibility(View.VISIBLE);
            binding.productCartQTY.setVisibility(View.VISIBLE);
            binding.productCartQTY.setText(String.valueOf(count));

        });


        binding.addToCartBtn.setOnClickListener(v -> {
            if (user_id > 0) {
                count = Integer.parseInt(binding.productCartQTY.getText().toString());

                AddExtraCall addExtraCall = new AddExtraCall();
                if (Objects.requireNonNull(binding.tvProductDesc.getText()).toString().isEmpty()) {
                    Toast(getString(R.string.please_add_desc));

                } else {
                    addExtraCall.description = binding.tvProductDesc.getText().toString();
                    addExtraCall.userId = user_id;
                    addExtraCall.barcode = CODE;
                    addExtraCall.qty = Integer.parseInt(binding.productCartQTY.getText().toString());
                    addExtraCall.storeId = store_id;
                    if (selectedPhotoFil != null) {
                        AddRequestWithPhoto(addExtraCall, selectedPhotoFil);

                    } else {
                        AddRequestWithOutPhoto(addExtraCall);

                    }
                }
            } else {
                UtilityApp.logOut();
                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


        });


        binding.deleteCartBtn.setOnClickListener(v -> {

        });


        binding.addImage.setOnClickListener(view -> {
            openPicker();

        });

        binding.scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(getActiviy());
                checkCameraPermission();
            }
        });


    }

    private final void openPicker() {
        try {
            PermissionCompat.Builder builder = new PermissionCompat.Builder((getActiviy()));
            builder.addPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            builder.addPermissionRationale(getString(R.string.should_allow_permission));

            builder.addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                public void onGrant() {

                    pickImage();
                }

                public void onDenied(@NotNull String permission) {
                    Toast(R.string.some_permission_denied);

                }
            });
            builder.build().request();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    private void pickImage() {
        pickImageDialog = new PickImageDialog(getActiviy(), (obj, func, IsSuccess) -> {

            if (func.equals(Constants.CAPTURE)) {

                choosePhotoHelper = ChoosePhotoHelper.with(getActiviy()).asUri().build(uri -> {

                    selectedPhotoUri = uri;
                    try {

                        selectedPhotoFil = FileUtil.from(getActiviy(), uri);

                        Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.addImage);

                        selectedPhotoFil = new Compressor(getActiviy()).setQuality(65).compressToFile(selectedPhotoFil);
//                        Log.i(getClass().getSimpleName(), "Log Image Size "+selectedPhotoFil.getPath().length());
//
//                        long length = selectedPhotoFil.length();
//                        length = length/1024;
//
//                        Log.i(getClass().getSimpleName(), "Log File Path "+selectedPhotoFil.getPath() + ", File size : " + length +" KB");
//                        Log.i(getClass().getSimpleName(), "Log Image Size "+selectedPhotoFil.getPath().length());


                    } catch (IOException e) {
                        e.printStackTrace();
                        GlobalData.errorDialog(getActiviy(), R.string.upload_photo, getString(R.string.textTryAgain));

                    }

                });

                choosePhotoHelper.takePhoto();


            } else if (func.equals(Constants.PICK)) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.selectImage)), REQUEST_PICK_IMAGE);

            }

        });
        pickImageDialog.show();

    }


//    public void uploadPhoto(AddExtraCall addExtraCall, File photo) {
//        GlobalData.progressDialog(getActiviy(), R.string.add_specail_order, R.string.please_wait_to_add_request);
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            AddExtraResponse result = (AddExtraResponse) obj;
//            String message = getString(R.string.fail_to_get_data);
//
//            GlobalData.hideProgressDialog();
//
//            if (func.equals(Constants.ERROR)) {
//
//                if (result.getMessage() != null) {
//                    message = result.getMessage();
//                }
//
//                GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);
//
//            } else if (func.equals(Constants.FAIL)) {
//
//                GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);
//
//
//            } else if (func.equals(Constants.NO_CONNECTION)) {
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
//                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
//
//            } else {
//                if (IsSuccess) {
//
//
//                    AwesomeSuccessDialog successDialog = new AwesomeSuccessDialog(getActiviy());
//                    successDialog.setTitle(R.string.add_specail_order).setMessage(R.string.success_update)
//                            .setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).show().setOnDismissListener(dialogInterface -> {
//                        navigateToCartScreen();
//                    });
//                    successDialog.show();
//
//
//                } else {
//
//                    GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);
//
//
//                }
//            }
//
//        }).AddExtrat(addExtraCall, photo);
//    }

    private void navigateToCartScreen() {
        GlobalData.REFRESH_CART = true;
        Intent intent = new Intent(getActiviy(), MainActivity.class);
        intent.putExtra(Constants.CART, true);
        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (choosePhotoHelper != null)
            choosePhotoHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {

            try {

                if (data.getData() != null) {
                    selectedPhotoUri = data.getData();

                    selectedPhotoFil = FileUtil.from(getActiviy(), selectedPhotoUri);
                    selectedPhotoFil = new Compressor(getActiviy()).setQuality(65).compressToFile(selectedPhotoFil);


                    long length = selectedPhotoFil.length();

                    length = length / 1024;
                    Log.i(getClass().getSimpleName(), "Log File Path " + selectedPhotoFil.getPath() + ", File size : " + length + " KB");
                    Log.i(getClass().getSimpleName(), "Log Image Size " + selectedPhotoFil.getPath().length());

                    Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.addImage);

                }


            } catch (Exception e) {
                e.printStackTrace();
                GlobalData.errorDialog(getActiviy(), R.string.upload_photo, getString(R.string.textTryAgain));
            }
        } else if (requestCode == SEARCH_CODE) {

            if (data != null) {
                CODE = data.getStringExtra(Constants.CODE);
                binding.barcodeTv.setText(getString(R.string.Barcode) + " " + CODE);
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    private void AddRequestWithPhoto(AddExtraCall addExtraCall, File photo) {

        Log.i("tag", "Log  userId " + addExtraCall.userId);

        GlobalData.progressDialog(getActiviy(), R.string.add_specail_order, R.string.please_wait_to_add_request);

        if (UtilityApp.getLocalData().getShortname() != null) {
            country = UtilityApp.getLocalData().getShortname();

        } else {
            country = GlobalData.COUNTRY;

        }


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

//AndroidNetworking.initialize(getActiviy(),okHttpClient);
        AndroidNetworking.upload(GlobalData.BetaBaseURL + country + GlobalData.grocery +
                GlobalData.Api + "v4/Carts/AddExtrat").addMultipartFile("file", photo)
                .addHeaders("ApiKey", Constants.api_key)
                .addHeaders("device_type", Constants.deviceType)
                .addHeaders("app_version", UtilityApp.getAppVersionStr())
                .addHeaders("token", UtilityApp.getToken())

                .addQueryParameter("qty", String.valueOf(addExtraCall.qty))
                .addQueryParameter("barcode", String.valueOf(addExtraCall.barcode))
                .addQueryParameter("description", String.valueOf(addExtraCall.description))
                .addQueryParameter("user_id", String.valueOf(addExtraCall.userId))
                .addQueryParameter("store_id", String.valueOf(addExtraCall.storeId))
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build().

                        setUploadProgressListener((bytesUploaded, totalBytes) -> {
                            // do anything with progress
                        }).getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GlobalData.hideProgressDialog();
                        Log.i("tag", "Log data response " + response);

                        String message = getString(R.string.fail_to_add_extra_order);


                        try {
                            JSONObject jsonObject = response;
                            int status = jsonObject.getInt("status");
                            if (status == 200) {
                                UtilityApp.updateCart(1, 1);

                                AwesomeSuccessDialog successDialog = new AwesomeSuccessDialog(getActiviy());
                                successDialog.setTitle(R.string.add_specail_order).setMessage(R.string.success_update)
                                        .setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).show().setOnDismissListener(dialogInterface -> {
                                    navigateToCartScreen();
                                });
                                successDialog.show();


                            } else {
                                message = jsonObject.getString("message");
                                GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getMessage() != null) {
                            GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, error.getMessage());

                        }

                    }
                });
    }


    private void AddRequestWithOutPhoto(AddExtraCall addExtraCall) {

        Log.i("tag", "Log  userId " + addExtraCall.userId);

        GlobalData.progressDialog(getActiviy(), R.string.add_specail_order, R.string.please_wait_to_add_request);

        if (UtilityApp.getLocalData().getShortname() != null) {
            country = UtilityApp.getLocalData().getShortname();

        } else {
            country = GlobalData.COUNTRY;

        }


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.post(GlobalData.BetaBaseURL + country + GlobalData.grocery +
                GlobalData.Api + "v4/Carts/AddExtrat")
                .addHeaders("ApiKey", Constants.api_key)
                .addHeaders("device_type", Constants.deviceType)
                .addHeaders("app_version", UtilityApp.getAppVersionStr())
                .addHeaders("token", UtilityApp.getToken())
                .addQueryParameter("qty", String.valueOf(addExtraCall.qty))
                .addQueryParameter("barcode", String.valueOf(addExtraCall.barcode))
                .addQueryParameter("description", String.valueOf(addExtraCall.description))
                .addQueryParameter("user_id", String.valueOf(addExtraCall.userId))
                .addQueryParameter("store_id", String.valueOf(addExtraCall.storeId))
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build().

                setUploadProgressListener((bytesUploaded, totalBytes) -> {
                    // do anything with progress
                }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                GlobalData.hideProgressDialog();
                Log.i("tag", "Log data response " + response);

                String message = getString(R.string.fail_to_add_extra_order);


                try {
                    JSONObject jsonObject = response;
                    int status = jsonObject.getInt("status");
                    if (status == 200) {
                        UtilityApp.updateCart(1, 1);

                        AwesomeSuccessDialog successDialog = new AwesomeSuccessDialog(getActiviy());
                        successDialog.setTitle(R.string.add_specail_order).setMessage(R.string.success_update)
                                .setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).show().setOnDismissListener(dialogInterface -> {
                            navigateToCartScreen();
                        });
                        successDialog.show();


                    } else {
                        message = jsonObject.getString("message");
                        GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(ANError error) {
                if (error.getMessage() != null) {
                    GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, error.getMessage());

                }

            }
        });
    }

    private void checkCameraPermission() {
        Dexter.withContext(getActiviy()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startScan();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getActiviy(), "" + getActiviy().getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(error -> Toast.makeText(getActiviy(), "" + getActiviy().getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActiviy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActiviy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {

            Intent intent = new Intent(getActiviy(), FullScannerActivity.class);
            startActivityForResult(intent, SEARCH_CODE);

        }

    }


}