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
import java.util.List;

public class AddNewAddressActivity extends ActivityBase {
    //
    public ArrayList<AreasModel> stateModelList;
    ActivityAddNewAddressBinding binding;
    Boolean isEdit = false;
    int addressId;
    AddressModel addressModel;
    int area_id = 0, state_id = 0;
    List<String> stateNames;
    String state_name;
    ArrayAdapter<String> adapter;
    int selectedCityId = 0;
    // Address Phone
    String CountryCode = "+973";
    boolean select_country = false;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private String google_address = "";
    private String phonePrefix = "973";
    private int CHOOSE_LOCATION = 3000;
    private int countryId;
    private LocalModel localModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        stateModelList = new ArrayList<>();
        stateNames = new ArrayList<>();

        setTitle(R.string.new_address);
        localModel=UtilityApp.getLocalData();

        if(UtilityApp.getLocalData()!=null){

            if(localModel.getCountryName()!=null&&localModel.getPhonecode()!=null){
                binding.codeSpinner.setText(localModel.getCountryName().concat( " " +"("+localModel.getPhonecode()+")"));
                CountryCode= "+".concat(String.valueOf(localModel.getPhonecode()));
                phonePrefix= String.valueOf(localModel.getPhonecode());
            }


        }




        binding.locationBut.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActiviy(), MapActivity.class);
            startActivityForResult(intent, CHOOSE_LOCATION);

        });


        getIntentData();

        if(localModel!=null&& localModel.getShortname().equals("KW")){
            String code=localModel.getShortname();
            binding.blockEt.setHint(getString(R.string.block_kw));
        }

        binding.addNewAddressBut.setOnClickListener(view1 -> {

            if (isValidForm() && area_id != 0) {
                CreateNewAddress();
            } else {
                YoYo.with(Techniques.Shake).playOn(binding.stateSpinner);
                Toast(R.string.select_area);
            }

        });

        binding.cancelBtu.setOnClickListener(view1 -> {
            onBackPressed();
        });


        if (isEdit) {
            binding.addNewAddressBut.setVisibility(View.GONE);
            binding.editAddressBut.setVisibility(View.GONE);
            binding.cancelBtu.setVisibility(View.GONE);
            binding.toolBar.mainTitleTxt.setText(R.string.edit_address);
            binding.addNewTv.setVisibility(View.GONE);

        }

        else {
            binding.addNewAddressBut.setVisibility(View.VISIBLE);
            binding.editAddressBut.setVisibility(View.GONE);

        }

        countryId = UtilityApp.getLocalData().getCountryId();
        
        GetAreas(countryId);

        binding.codeSpinner.setOnClickListener(view1 -> {
            CountryCodeDialog countryCodeDialog=new CountryCodeDialog(getActiviy(), 17, (obj, func, IsSuccess) -> {
                CountryModel countryModel= (CountryModel) obj;
                if(countryModel!=null){
                    CountryCode= "+".concat(String.valueOf(countryModel.getPhonecode()));
                    phonePrefix= String.valueOf(countryModel.getPhonecode());
                    binding.codeSpinner.setText(countryModel.getName().concat( " " +"("+countryModel.getPhonecode()+")"));

                }

            });
            countryCodeDialog.show();
        });

        binding.addNewTv.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActiviy(), MapActivity.class);
            startActivityForResult(intent, CHOOSE_LOCATION);
        });


    }


    private void CreateNewAddress() {
        state_id= Integer.parseInt(UtilityApp.getLocalData().getCityId());
        int userId=UtilityApp.getUserData().getId();
        AddressModel addressModel = new AddressModel();
        addressModel.setName(binding.nameEt.getText().toString());
        addressModel.setAreaId(area_id);
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
            }

            else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(),getString(R.string.no_internet_connection));

            }

            else {
                if (IsSuccess) {

                    Intent intent = new Intent(getActiviy(), AddressActivity.class);
                    intent.putExtra(Constants.KEY_ADD_NEW,true);
                    Log.i("TAG","Log addNewAddress intent  "+true);
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
                        binding.addressTv.setText(addressModel.getAddressNickname());
                        // binding.areaEt.setText(addressModel.getAreaDetails());
                        binding.latTv.setText(addressModel.getLatitude().toString());
                        binding.longTv.setText(addressModel.getLongitude().toString());
                        binding.nameEt.setText(addressModel.getName());
                        binding.streetEt.setText(addressModel.getStreetDetails());
//                        binding.codeTv.setText(addressModel.getCountry());
                        binding.phoneTv.setText(addressModel.getMobileNumber());
                        binding.addressTv.setText(addressModel.getFullAddress());
                        binding.flatEt.setText(addressModel.getHouseNo());
                        binding.blockEt.setText(addressModel.getBlock());
                        binding.blockEt.setText(addressModel.getCountry());

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

        return formValidator
                .addField(binding.nameEt,
                        new NonEmptyRule(R.string.enter_name)).validate();

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

            if (func.equals(Constants.ERROR)) {

                Toast(R.string.error_in_data);

            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_get_data);


            } else {
                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        //addressList= result.getData();
                        // initAdapter();
                        ArrayList<AreasModel> list = result.getData();
                        initStateSpinner(list);

                    }


                } else {
                    Toast(R.string.fail_to_get_data);

                }
            }

        }).GetAreasHandle(country_id);
    }

    private void initStateSpinner(ArrayList<AreasModel> data) {
        binding.stateSpinner.setTitle(getString(R.string.select_area));
        binding.stateSpinner.setPositiveButton(getString(R.string.close));
        stateModelList.clear();
        stateModelList = data;
        stateModelList.add(new AreasModel(0, getString(R.string.select_area), getString(R.string.select_area)));

        // Collections.sort(stateModelList);

        for (int i = 0; i < data.size(); i++) {
            stateNames.add(UtilityApp.getLanguage().equalsIgnoreCase(Constants.Arabic) ? data.get(i).getNameAe() : data.get(i).getNameEn());

        }


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stateNames);
        binding.stateSpinner.setAdapter(adapter);

        if (stateModelList != null) {
            int pos = 0;
            for (int i1 = 0; i1 < stateModelList.size(); i1++) {

//                if (addressesDM.getAddressNote().equals(stateModelList.get(i1).getArea_ar()) || addressesDM.getAddressNote().equals(stateModelList.get(i1).getArea_en()))
                //    pos = i1;

            }

            //  binding.stateSpinner.setSelection(pos);
        }


        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item

//                ((TextView) adapter.getChildAt(0)).setTextColor(Color.BLACK);

                if (position > 0) {
                    state_name = UtilityApp.getLanguage().equalsIgnoreCase(Constants.Arabic) ? stateModelList.get(position).getNameAe() : stateModelList.get(position).getNameEn();
                    // binding.etInputAddressNote.setText(state_name);
                    selectedCityId = stateModelList.get(position).getId();
                    Log.i("tag", "selectedCityId" + selectedCityId);
                    area_id=selectedCityId;

                } else {
                    state_name = stateNames.get(position);
                    //  binding.etInputAddressNote.setText(state_name);
                    selectedCityId = 0;
                    Log.i("tag", "selectedCityId" + selectedCityId);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


    }


}