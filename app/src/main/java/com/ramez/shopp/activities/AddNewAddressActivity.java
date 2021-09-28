package com.ramez.shopp.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
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
import com.ramez.shopp.Utils.ImageHandler;
import com.ramez.shopp.Utils.MapHandler;
import com.ramez.shopp.databinding.ActivityAddNewAddressBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.nlopez.smartlocation.SmartLocation;

public class AddNewAddressActivity extends ActivityBase implements OnMapReadyCallback {
    public List<AreasModel> stateModelList;
    ActivityAddNewAddressBinding binding;
    Boolean isEdit = false;
    int addressId;
    AddressModel addressModel;
    int state_id = 0;
    List<String> stateNames;
    String state_name = "";
    int selectedCityId = 0;
    String CountryCode = "+973";
    private GoogleMap map;
    private String phonePrefix = "973";
    private int countryId = 17;
    private LocalModel localModel;
    private boolean isShowing = false;
    private SupportMapFragment fragment;
    private CameraUpdate cameraUpdate;
    private float zoomLevel = 12.0F;
    private LatLng latLng;
    private double selectedLat = 26.05177032598081;
    private double selectedLng = 50.50513866994304;
    private AutocompleteSupportFragment autocompleteFragment;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> placeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.mapKey), Locale.US);
        }

        stateModelList = new ArrayList<>();
        stateNames = new ArrayList<>();

        latLng = new LatLng(selectedLat, selectedLng);

        setTitle(R.string.new_address);

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());


        if (localModel != null) {

            if (localModel.getCountryName() != null && localModel.getPhonecode() != null) {
                binding.codeSpinner.setText(localModel.getCountryName().concat(" " + "(" + localModel.getPhonecode() + ")"));
                CountryCode = "+".concat(String.valueOf(localModel.getPhonecode()));
                phonePrefix = String.valueOf(localModel.getPhonecode());
            }


        }

        getIntentData();

        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (fragment != null) {
            fragment.getMapAsync(this);
        }

        placeLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result!=null &&result.getResultCode() == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(result.getData());
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(result.getData());
                Log.i("TAG", status.getStatusMessage());
            } else if (result.getResultCode() == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;

        });

        binding.stateSpinnerTv.setInputType(InputType.TYPE_NULL);
        binding.codeSpinner.setInputType(InputType.TYPE_NULL);


        if (localModel != null && localModel.getShortname().equals("KW")) {
            binding.blockEt.setHint(getString(R.string.block_kw));
        }

        binding.myLocationBtn.setOnClickListener(view1 -> {
            checkLocationPermission();

        });

        binding.addNewAddressBut.setOnClickListener(view1 -> {

            if (isValidForm() && selectedCityId > 0 && isShowing) {
                CreateNewAddress();
            } else {
                if (Objects.requireNonNull(binding.nameEt.getText()).toString().isEmpty()) {
                    Toast(R.string.enter_name);
                    binding.nameEt.requestFocus();

                } else if (selectedCityId == 0) {
                    YoYo.with(Techniques.Shake).playOn(binding.stateSpinner1Input);
                    Toast(R.string.select_area);
                }

//                else if(binding.phoneTv.getText().toString().length() >10 ) {
//                    Toast(R.string.verify_phone_number);
//                }

            }

        });


        if (isEdit) {
            binding.addNewAddressBut.setVisibility(View.GONE);

        } else {
            binding.addNewAddressBut.setVisibility(View.VISIBLE);
            checkLocationPermission();
            initPlaceAutoComplete();

        }

        countryId = localModel.getCountryId();

        //GetAreas(countryId);

        binding.codeSpinner.setOnClickListener(view1 -> {
            CountryCodeDialog countryCodeDialog = new CountryCodeDialog(getActiviy(), countryId, (obj, func, IsSuccess) -> {
                CountryModel countryModel = (CountryModel) obj;
                if (countryModel != null) {
                    CountryCode = "+".concat(String.valueOf(countryModel.getPhonecode()));
                    phonePrefix = String.valueOf(countryModel.getPhonecode());
                    binding.codeSpinner.setText(countryModel.getCountryName().concat(" " + "(" + countryModel.getPhonecode() + ")"));

                }

            });
            countryCodeDialog.show();
        });

        binding.stateSpinnerTv.setOnClickListener(view1 -> {
            StateDialog stateDialog = new StateDialog(getActiviy(), selectedCityId, (obj, func, IsSuccess) -> {
                AreasModel areasModel = (AreasModel) obj;
                if (areasModel != null) {
                    binding.stateSpinnerTv.setText(areasModel.getStateName());
                    state_name = areasModel.getStateName();
                    selectedCityId = areasModel.getId();

                } else {
                    binding.stateSpinnerTv.setText(getString(R.string.select_area));

                }

            });
            stateDialog.show();
            isShowing = true;


        });

    }

    private void CreateNewAddress() {
        state_id = Integer.parseInt(localModel.getCityId());
        int userId = UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null
                ? UtilityApp.getUserData().getId() : 0;
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
        addressModel.setLatitude(selectedLat);
        addressModel.setLongitude(selectedLng);
        addressModel.setUserId(userId);
        addressModel.setGoogleAddress(binding.addressTV.getText().toString());

        GlobalData.progressDialog(getActiviy(), R.string.add_new_address, R.string.please_wait_creat);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            AddressResultModel result = (AddressResultModel) obj;
            String message = "error_in_data";

            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();

                }
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
                        if (addressModel != null) {
                            selectedLat = addressModel.getLatitude();
                            selectedLng = addressModel.getLongitude();
                            latLng = new LatLng(selectedLat, selectedLng);
                        }

                        if (map != null)
                            setMapMarker();
                        getLocationAddress();
                        Log.i("tag", "Log Block " + addressModel.getBlock());
                        binding.addressTV.setText(addressModel.getFullAddress());
                        binding.nameEt.setText(addressModel.getName());
                        binding.streetEt.setText(addressModel.getStreetDetails());
//                        binding.codeTv.setText(addressModel.getCountry());
                        binding.phoneTv.setText(addressModel.getMobileNumber());
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

    private boolean isValidForm() {
        FormValidator formValidator = FormValidator.Companion.getInstance();
        return formValidator.addField(binding.nameEt, new NonEmptyRule(R.string.enter_name)).validate();
        // new MaxLengthRule(10, R.string.verify_phone_number))
        //                        .validate();

    }

    public void GetAreas(int country_id) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            AreasResultModel result = (AreasResultModel) obj;
            if (IsSuccess) {
                if (result.getData() != null && result.getData().size() > 0) {

                    selectedCityId = result.getData().get(0).getId();
                    state_name = result.getData().get(0).getStateName();
                }
            }


        }).GetAreasHandle(country_id);
    }

    private void checkLocationPermission() {
        try {
            PermissionCompat.Builder builder = new PermissionCompat.Builder(getActiviy());

            builder.addPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            builder.addPermissionRationale((getString(R.string.app_name)));

            builder.addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                public void onGrant() {
                    if (map != null) {
                        if (!isEdit) {
                            getMyLocation();
                        }
                    }

                }

                public void onDenied(@NotNull String permission) {
                    Toast(R.string.some_permission_denied);
                    Log.e("TAG", permission + "Denied");

                }
            });
            builder.build().request();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    private void getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            binding.loadingLocationLY.setVisibility(View.VISIBLE);

            SmartLocation.with(getActiviy()).location().oneFix().start(location -> {
                binding.loadingLocationLY.setVisibility(View.GONE);

                selectedLat = location.getLatitude();
                selectedLng = location.getLongitude();
                LatLng latLng = new LatLng(selectedLat, selectedLng);

                map.clear();
                map.addMarker((new MarkerOptions()).position(latLng).
                        icon(BitmapDescriptorFactory.fromBitmap(ImageHandler.getBitmap(getActiviy(), R.drawable.location_icons))).title(getString(R.string.my_location)));

                getLocationAddress();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, zoomLevel));
                map.animateCamera(cameraUpdate);


            });
        } else {
            showGPSDisabledAlertToUser();
        }

    }

    private void initPlaceAutoComplete() {

        Places.initialize(getActiviy(), getString(R.string.mapKey), Locale.US);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setHint(getString(R.string.searchaddress));

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);

//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            placeLauncher.launch(intent);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    try {

                        map.clear();
                        selectedLat = place.getLatLng() != null ? place.getLatLng().latitude : 0.0;
                        selectedLng = place.getLatLng() != null ? place.getLatLng().longitude : 0.0;
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(selectedLng, selectedLng));
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(selectedLat, selectedLng), zoomLevel);
                        map.animateCamera(cameraUpdate);
                        getLocationAddress();

                        map.clear();
                        map.addMarker(new MarkerOptions().position(new LatLng(selectedLat, selectedLng)).icon(BitmapDescriptorFactory.fromBitmap(ImageHandler.getBitmap(getActiviy(), R.drawable.location_icons))).title(MapHandler.getGpsAddress(getActiviy(), selectedLat, selectedLng))

                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i("TAG", "An error occurred: status" + status + "");

                }
            });

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autocompleteFragment == null)
            initPlaceAutoComplete();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;


        cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, zoomLevel));
        map.moveCamera(cameraUpdate);

        if (!isEdit) {
            map.setOnMapClickListener(it -> {

                map.clear();

                map.addMarker(new MarkerOptions().position(new LatLng(it.latitude, it.longitude)).icon(BitmapDescriptorFactory.fromBitmap(ImageHandler.getBitmap(getActiviy(), R.drawable.location_icons))).title(getString(R.string.my_location)));

                selectedLat = it.latitude;
                selectedLng = it.longitude;
                getLocationAddress();

            });
        }

        if (addressModel != null) {
            selectedLat = addressModel.getLatitude();
            selectedLng = addressModel.getLongitude();
            setMapMarker();
        }

    }

    private void setMapMarker() {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng).icon(BitmapDescriptorFactory.fromBitmap(ImageHandler.getBitmap(getActiviy(), R.drawable.location_icons))).title(getString(R.string.my_location)));

        cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, zoomLevel));
        map.moveCamera(cameraUpdate);

    }

    private void getLocationAddress() {

        String address = MapHandler.getGpsAddress(getActiviy(), selectedLat, selectedLng);
        Log.i("TAG", "Log My selectedLat: " + selectedLat + "");
        Log.i("TAG", "Log My selectedLng: " + selectedLng + "");
        Log.i("TAG", "Log My Location: " + address + "");

        binding.addressTV.setText(address);


    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActiviy());
        alertDialogBuilder.setMessage(getString(R.string.open_gps)).setCancelable(false).setPositiveButton(getString(R.string.enable), (DialogInterface.OnClickListener) ((dialog, id) -> {
            dialog.cancel();
            Intent callGPSSettingIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
            startActivity(callGPSSettingIntent);
            dialog.cancel();
        }));

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_tex), null);
        AlertDialog alert = alertDialogBuilder.create();

        try {
            alert.show();
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i("TAG", status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


}


