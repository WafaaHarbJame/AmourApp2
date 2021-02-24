package com.ramez.shopp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CountryCodeDialog;
import com.ramez.shopp.Dialogs.StateDialog;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.AddressResultModel;
import com.ramez.shopp.Models.AreasModel;
import com.ramez.shopp.Models.AreasResultModel;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.MapHandler;
import com.ramez.shopp.databinding.ActivityAddNewAddressBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddNewAddressActivity extends ActivityBase {
    public List<AreasModel> stateModelList;
    ActivityAddNewAddressBinding binding;
    Boolean isEdit = false;
    int addressId;
    AddressModel addressModel;
    int state_id = 0;
    List<String> stateNames;
    String state_name="";
    int selectedCityId = 0;
    String CountryCode = "+973";
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String google_address = "";
    private String phonePrefix = "973";
    private int CHOOSE_LOCATION = 3000;
    private int countryId=17;
    private LocalModel localModel;
    private boolean isShowing=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        stateModelList = new ArrayList<>();
        stateNames = new ArrayList<>();

        setTitle(R.string.new_address);
        localModel = UtilityApp.getLocalData();

        if (UtilityApp.getLocalData() != null) {

            if (localModel.getCountryName() != null && localModel.getPhonecode() != null) {
                binding.codeSpinner.setText(localModel.getCountryName().concat(" " + "(" + localModel.getPhonecode() + ")"));
                CountryCode = "+".concat(String.valueOf(localModel.getPhonecode()));
                phonePrefix = String.valueOf(localModel.getPhonecode());
            }


        }


        binding.locationBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActiviy(), MapActivity.class);
            startActivityForResult(intent, CHOOSE_LOCATION);

        });


        getIntentData();

        if (localModel != null && localModel.getShortname().equals("KW")) {
            String code = localModel.getShortname();
            binding.blockEt.setHint(getString(R.string.block_kw));
        }

        binding.addNewAddressBut.setOnClickListener(view1 -> {

            if (isValidForm() && selectedCityId > 0&&isShowing) {
                CreateNewAddress();
            } else {
                if(Objects.requireNonNull(binding.nameEt.getText()).toString().isEmpty()){
                    Toast(R.string.enter_name);
                    binding.nameEt.requestFocus();

                }
                else {
                    YoYo.with(Techniques.Shake).playOn(binding.stateSpinner1);
                    Toast(R.string.select_area);
                }

            }

        });

        binding.cancelBtu.setOnClickListener(view1 -> {
            onBackPressed();
        });


        if (isEdit) {
            binding.addNewAddressBut.setVisibility(View.GONE);
            binding.editAddressBut.setVisibility(View.GONE);
            binding.cancelBtu.setVisibility(View.GONE);
           // binding.toolBar.mainTitleTxt.setText(R.string.edit_address);
            binding.addNewTv.setVisibility(View.GONE);

        } else {
            binding.addNewAddressBut.setVisibility(View.VISIBLE);
            binding.editAddressBut.setVisibility(View.GONE);

        }

        countryId = UtilityApp.getLocalData().getCountryId();

          GetAreas(countryId);

        binding.codeSpinner.setOnClickListener(view1 -> {
            CountryCodeDialog countryCodeDialog = new CountryCodeDialog(getActiviy(), countryId, (obj, func, IsSuccess) -> {
                CountryModel countryModel = (CountryModel) obj;
                if (countryModel != null) {
                    CountryCode = "+".concat(String.valueOf(countryModel.getPhonecode()));
                    phonePrefix = String.valueOf(countryModel.getPhonecode());
                    binding.codeSpinner.setText(countryModel.getName().concat(" " + "(" + countryModel.getPhonecode() + ")"));

                }

            });
            countryCodeDialog.show();
        });

        binding.addNewTv.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActiviy(), MapActivity.class);
            startActivityForResult(intent, CHOOSE_LOCATION);
        });


        binding.stateSpinner1.setOnClickListener(view1 -> {
            StateDialog stateDialog = new StateDialog(getActiviy(), selectedCityId, (obj, func, IsSuccess) -> {
                AreasModel areasModel = (AreasModel) obj;
                if (areasModel != null) {
                    binding.stateSpinner1.setText(areasModel.getStateName());
                    state_name = areasModel.getStateName();
                    selectedCityId = areasModel.getId();

                }
                else {
                    binding.stateSpinner1.setText(state_name);

                }

            });
            stateDialog.show();
            isShowing=true;


        });

    }

    private void CreateNewAddress() {
        state_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());
        int userId = UtilityApp.getUserData().getId();
        AddressModel addressModel = new AddressModel();
        addressModel.setName(binding.nameEt.getText().toString());
        addressModel.setAreaId(selectedCityId);
        addressModel.setState(state_id);
        addressModel.setBlock(binding.blockEt.getText().toString());
        addressModel.setStreetDetails(binding.streetEt.getText().toString());
        addressModel.setHouseNo(binding.buildingEt.getText().toString());
        addressModel.setApartmentNo(binding.flatEt.getText().toString());
        addressModel.setPhonePrefix(phonePrefix);
        addressModel.setMobileNumber(binding.phoneTv.getText().toString());
        addressModel.setLatitude(latitude);
        addressModel.setLongitude(longitude);
        addressModel.setUserId(userId);
        addressModel.setGoogleAddress(binding.addressTv.getText().toString());

        GlobalData.progressDialog(getActiviy(), R.string.add_new_address, R.string.please_wait_creat);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            AddressResultModel result = (AddressResultModel) obj;
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
                String message = result.getMessage();
                GlobalData.errorDialog(getActiviy(), R.string.fail_to_addAddress, message);


            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_get_data);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(), getString(R.string.no_internet_connection));

            } else {
                if (IsSuccess) {

                    Intent intent = new Intent(getActiviy(), AddressActivity.class);
                    intent.putExtra(Constants.KEY_ADD_NEW, true);
                    setResult(RESULT_OK, intent);
                    finish();


                } else {
                    Toast(R.string.fail_to_addAddress);

                }
            }

        }).CreateAddressHandle(addressModel);

    }


    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEdit = bundle.getBoolean(Constants.KEY_EDIT);
            addressId = bundle.getInt(Constants.KEY_ADDRESS_ID, 0);
            GetUserAddress(addressId);

        }
    }


    @SuppressLint("SetTextI18n")
    public void GetUserAddress(int addressId) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
            binding.dataLY.setVisibility(View.VISIBLE);

            AddressResultModel result = (AddressResultModel) obj;

            if (func.equals(Constants.ERROR)) {

                Toast(R.string.error_in_data);
                binding.dataLY.setVisibility(View.VISIBLE);


            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_get_data);
            } else {
                if (IsSuccess) {
                    binding.dataLY.setVisibility(View.VISIBLE);
                    if (result.getData() != null && result.getData().size() > 0) {
                        addressModel = result.getData().get(0);
                        Log.i("tag","Log Block "+addressModel.getBlock());
                        binding.addressTv.setText(addressModel.getFullAddress());
                        // binding.areaEt.setText(addressModel.getAreaDetails());
                        if (addressModel.getLatitude() != null && addressModel.getLongitude() != null) {
                            binding.latTv.setText(addressModel.getLatitude().toString());
                            binding.longTv.setText(addressModel.getLongitude().toString());
                        }
                        binding.nameEt.setText(addressModel.getName());
                        binding.streetEt.setText(addressModel.getStreetDetails());
//                        binding.codeTv.setText(addressModel.getCountry());
                        binding.phoneTv.setText(addressModel.getMobileNumber());
                        binding.addressTv.setText(addressModel.getFullAddress());
                        binding.flatEt.setText(addressModel.getHouseNo());
                        binding.blockEt.setText(addressModel.getBlock());
                        binding.buildingEt.setText(addressModel.getHouseNo());

                    } else {
                        binding.dataLY.setVisibility(View.GONE);

                    }


                } else {
                    Toast(R.string.fail_to_get_data);

                }
            }

        }).GetAddressByIdHandle(addressId);
    }


    private final boolean isValidForm() {
        FormValidator formValidator = FormValidator.Companion.getInstance();

        return formValidator.addField(binding.nameEt, new NonEmptyRule(R.string.enter_name)).validate();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_LOCATION) {
                if (data != null) {
                    String lat = data.getStringExtra(Constants.KEY_LAT);
                    String lng = data.getStringExtra(Constants.KEY_LNG);
                    latitude = Double.valueOf(lat);
                    longitude = Double.valueOf(lng);
                    google_address = MapHandler.getGpsAddress(getActiviy(), latitude, longitude);
                    binding.addressTv.setText(google_address);
                    binding.latTv.setText(lat);
                    binding.longTv.setText(lng);

                }

            }


        }
    }


    public void GetAreas(int country_id) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            AreasResultModel result = (AreasResultModel) obj;
                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {

                        selectedCityId=result.getData().get(0).getId();
                        state_name=result.getData().get(0).getStateName();
                    }
                }


        }).GetAreasHandle(country_id);
    }


}