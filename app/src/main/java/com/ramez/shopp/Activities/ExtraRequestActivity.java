package com.ramez.shopp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.bumptech.glide.Glide;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AddExtraResponse;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.PickImageDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.AddExtraCall;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.FileUtil;
import com.ramez.shopp.databinding.ActivityExtraRequestBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExtraRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("");

        user = UtilityApp.getUserData();
        localModel = UtilityApp.getLocalData();
        store_id = Integer.parseInt(localModel.getCityId());
        user_id = UtilityApp.getUserData().getId();

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

            binding.addToCartBtn.setVisibility(View.GONE);
            binding.minusCartBtn.setVisibility(View.VISIBLE);
            binding.plusCartBtn.setVisibility(View.VISIBLE);
            binding.productCartQTY.setVisibility(View.VISIBLE);

            count = Integer.parseInt(binding.productCartQTY.getText().toString());

            if (count == 1) {
                binding.minusCartBtn.setVisibility(View.GONE);
                binding.deleteCartBtn.setVisibility(View.VISIBLE);

            }
            AddExtraCall addExtraCall = new AddExtraCall();
            addExtraCall.description = binding.tvProductDesc.getText().toString();
            addExtraCall.userId = user_id;
            addExtraCall.barcode = "";
            addExtraCall.qty = Integer.parseInt(binding.productCartQTY.getText().toString());
            addExtraCall.storeId = store_id;
            uploadPhoto(addExtraCall, selectedPhotoFil);

        });


        binding.deleteCartBtn.setOnClickListener(v -> {

        });


        binding.addImage.setOnClickListener(view -> {
            openPicker();

        });


    }


    private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                Log.i("tag", "Log " + UtilityApp.getCartCount());
                UtilityApp.updateCart(1, 10);
                binding.addToCartBtn.setVisibility(View.GONE);
                binding.productCartQTY.setText(String.valueOf(quantity));

            } else {

                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.fail_to_add_cart), getString(R.string.fail_to_update_cart));

            }


        }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
    }

    private void updateCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (IsSuccess) {

                binding.productCartQTY.setText(String.valueOf(quantity));

            } else {
                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.fail_to_add_cart), getString(R.string.fail_to_update_cart));


            }

        }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);
    }

    private void deleteCart(View v, int position, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                UtilityApp.updateCart(2, 10);
                Log.i("tag", "Log " + UtilityApp.getCartCount());
                binding.productCartQTY.setText(String.valueOf(0));
                initSnackBar(getString(R.string.success_delete_from_cart));


            } else {
                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.fail_to_add_cart), getString(R.string.fail_to_delete_cart));


            }


        }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
    }

    private void initSnackBar(String message) {
        Toast.makeText(getActiviy(), message, Toast.LENGTH_SHORT).show();

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
//                        Picasso.get().load(selectedPhotoUri).error(R.drawable.avatar).into(binding.addImage);

                        Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.addImage);

                        selectedPhotoFil = new Compressor(getActiviy()).compressToFile(selectedPhotoFil);

                        Log.i("tag", "Log selectedPhotoFil  " + selectedPhotoFil);
                        Log.i("tag", "Log uri " + uri);
//                        AddExtraCall addExtraCall = new AddExtraCall();
//                        addExtraCall.description = binding.tvProductDesc.getText().toString();
//                        addExtraCall.userId = user_id;
//                        addExtraCall.barcode = "";
//                        addExtraCall.qty = Integer.parseInt(binding.productCartQTY.getText().toString());
//                        addExtraCall.storeId = store_id;
//
//                        uploadPhoto(addExtraCall, selectedPhotoFil);

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


    public void uploadPhoto(AddExtraCall addExtraCall, File photo) {
        GlobalData.progressDialog(getActiviy(), R.string.add_specail_order, R.string.please_wait_to_upload_photo);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            AddExtraResponse result = (AddExtraResponse) obj;
            String message = getString(R.string.fail_to_get_data);

            GlobalData.hideProgressDialog();

            if (func.equals(Constants.ERROR)) {

                if (result.getMessage() != null) {
                    message = result.getMessage();
                }

                GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);

            } else if (func.equals(Constants.FAIL)) {

                GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);

            } else {
                if (IsSuccess) {


                    AwesomeSuccessDialog successDialog = new AwesomeSuccessDialog(getActiviy());
                    successDialog.setTitle(R.string.add_specail_order).setMessage(R.string.success_update)
                            .setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).show().setOnDismissListener(dialogInterface -> {
                        navigateToCartScreen();
                    });
                    successDialog.show();



                } else {

                    GlobalData.errorDialog(getActiviy(), R.string.add_specail_order, message);


                }
            }

        }).AddExtrat(addExtraCall, photo);
    }

    private void navigateToCartScreen() {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_POSITION,2));
        Intent intent = new Intent(getActiviy(), MainActivity.class);
        intent.putExtra(Constants.CART, true);
        startActivity(intent);
        finish();
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

                    Glide.with(getActiviy()).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(binding.addImage);

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

}