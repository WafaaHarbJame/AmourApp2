package com.ramez.shopp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.PickImageDialog;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProfileData;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.FileUtil;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityEditProfileBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class EditProfileActivity extends ActivityBase {

    MemberModel memberModel;
    PickImageDialog pickImageDialog;
    int REQUEST_PICK_IMAGE = 11;
    int userId;
    File selectedPhotoFil = null;
    private ActivityEditProfileBinding binding;
    private String country;
    private ChoosePhotoHelper choosePhotoHelper;
    private Uri selectedPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.text_title_edit_profile);

        memberModel = UtilityApp.getUserData();
        userId = memberModel.getId();
        if (memberModel != null) {
            binding.userNameTv.setText(memberModel.getName());
            binding.edtUserName.setText(memberModel.getName());
            binding.etEmail.setText(memberModel.getEmail());
            binding.edtPhoneNumber.setText(memberModel.getMobileNumber());
            Log.i("tag", "Log data" + memberModel.getProfilePicture());

            Glide.with(getActiviy()).asBitmap().load(memberModel.getProfilePicture()).placeholder(R.drawable.avatar).into(binding.userImg);


        } else {
            LocalModel localModel = UtilityApp.getLocalData();
            int store_id = Integer.parseInt(localModel.getCityId());
            getUserData(userId, store_id);
        }


        binding.saveBut.setOnClickListener(view1 -> {

            updateProfile();

        });

        binding.editPhotoBut.setOnClickListener(view1 -> {

            openPicker();

        });


    }

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

                    Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.userImg);
                    uploadPhoto(userId, selectedPhotoFil);

                }


            } catch (Exception e) {
                e.printStackTrace();
                GlobalData.errorDialog(getActiviy(), R.string.upload_photo, getString(R.string.textTryAgain));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void pickImage() {
        pickImageDialog = new PickImageDialog(getActiviy(), (obj, func, IsSuccess) -> {

            if (func.equals(Constants.CAPTURE)) {

                choosePhotoHelper = ChoosePhotoHelper.with(getActiviy()).asUri().build(uri -> {

                    selectedPhotoUri = uri;
                    try {

                        selectedPhotoFil = FileUtil.from(getActiviy(), uri);

                        Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.userImg);

                        selectedPhotoFil = new Compressor(getActiviy()).setQuality(65).compressToFile(selectedPhotoFil);

//                        Log.i("tag","Log selectedPhotoFil  " + selectedPhotoFil);
//                        Log.i("tag","Log uri "+uri);

                        uploadPhoto(userId, selectedPhotoFil);

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

        try {
            pickImageDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final void openPicker() {
        try {
            PermissionCompat.Builder builder = new PermissionCompat.Builder((getActiviy()));
            builder.addPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE});
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

    private void updateProfile() {

        final String name = NumberHandler.arabicToDecimal(binding.edtUserName.getText().toString());
        final String email = NumberHandler.arabicToDecimal(binding.etEmail.getText().toString());

        memberModel.setName(name);
        memberModel.setEmail(email);

        GlobalData.progressDialog(getActiviy(), R.string.update_profile, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();

            LoginResultModel result = (LoginResultModel) obj;
            if (func.equals(Constants.ERROR)) {

                String message = getString(R.string.failtoupdate_profile);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                GlobalData.errorDialog(getActiviy(), R.string.failtoupdate_profile, message);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.errorDialog(getActiviy(), R.string.failtoupdate_profile, getString(R.string.no_internet_connection));


            } else {
                if (IsSuccess) {

                    MemberModel user = result.getData();
                    UtilityApp.setUserData(user);


                    GlobalData.successDialog(getActiviy(), getString(R.string.update_profile), getString(R.string.success_update));


                } else {
                    Toast(getString(R.string.failtoupdate_profile));

                }
            }


        }).updateProfile(memberModel);

    }

    private void uploadPhoto(int userId, File photo) {

        Log.i("tag", "Log  userId " + userId);

        GlobalData.progressDialog(getActiviy(), R.string.upload_photo, R.string.please_wait_to_upload_photo);

        if (UtilityApp.getLocalData().getShortname() != null) {
            country = UtilityApp.getLocalData().getShortname();

        } else {
            country = GlobalData.COUNTRY;

        }

        AndroidNetworking.upload(GlobalData.BetaBaseURL + country + GlobalData.grocery +
                GlobalData.Api + "v6/Account/UploadPhoto" + "?user_id=" + userId).addMultipartFile("file", photo)

                .addHeaders("ApiKey", Constants.api_key)
                .addHeaders("device_type", Constants.deviceType)
                .addHeaders("app_version", UtilityApp.getAppVersionStr())
                .addHeaders("token", UtilityApp.getToken())

                .setPriority(Priority.HIGH).build().
                setUploadProgressListener((bytesUploaded, totalBytes) -> {
                    // do anything with progress
                }).getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                GlobalData.hideProgressDialog();
                Log.i("tag", "Log data response " + response);

                String message = getString(R.string.failtoupdate_profile);

                if (response.equals(Constants.ERROR)) {
                    GlobalData.errorDialog(getActiviy(), R.string.failtoupdate_profile, message);
                } else {

                    String data = null;

                    try {
                        JSONObject jsonObject = response;
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            data = jsonObject.getString("data");
                            Log.i("tag", "Log data result " + data);

                            if (data != null) {
                                memberModel.setProfilePicture(data);
                                UtilityApp.setUserData(memberModel);
                                GlobalData.successDialog(getActiviy(), getString(R.string.upload_photo), getString(R.string.success_update));

                            }


                        } else {
                            message = jsonObject.getString("message");
                            GlobalData.errorDialog(getActiviy(), R.string.failtoupdate_profile, message);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onError(ANError error) {
                // handle error
            }
        });
    }

    public void getUserData(int user_id, int store_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ProfileData> result = (ResultAPIModel<ProfileData>) obj;
            String message = getString(R.string.fail_to_get_data);

            if (func.equals(Constants.ERROR)) {
                UtilityApp.logOut();
                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else if (func.equals(Constants.FAIL)) {
                UtilityApp.logOut();
                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                //  Toasty.error(getActiviy(),R.string.no_internet_connection, Toast.LENGTH_SHORT, true).show();

            } else if (IsSuccess) {
                MemberModel memberModel = UtilityApp.getUserData();
                if (result != null && result.data != null) {
                    memberModel.setName(result.data.getName());
                    memberModel.setEmail(result.data.getEmail());
                    memberModel.setLoyalBarcode(result.data.getLoyalBarcode());
                    memberModel.setProfilePicture(result.data.getProfilePicture());
                    UtilityApp.setUserData(memberModel);
                    binding.userNameTv.setText(memberModel.getName());
                    binding.edtUserName.setText(memberModel.getName());
                    binding.etEmail.setText(memberModel.getEmail());
                    binding.edtPhoneNumber.setText(memberModel.getMobileNumber());
                    Glide.with(getActiviy()).asBitmap().load(memberModel.getProfilePicture()).placeholder(R.drawable.avatar).into(binding.userImg);


                }


            }


        }).getUserDetails(user_id, store_id);
    }


}