package com.ramez.shopp.activities


import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager.BadTokenException
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator.Companion.getInstance
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData.Toast
import com.ramez.shopp.Classes.GlobalData.errorDialog
import com.ramez.shopp.Classes.GlobalData.hideProgressDialog
import com.ramez.shopp.Classes.GlobalData.progressDialog
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Dialogs.CountryCodeDialog
import com.ramez.shopp.Dialogs.StateDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ImageHandler
import com.ramez.shopp.Utils.MapHandler
import com.ramez.shopp.databinding.ActivityAddNewAddressBinding
import io.nlopez.smartlocation.SmartLocation
import java.lang.Exception
import java.util.*


class AddNewAddressActivity : ActivityBase(), OnMapReadyCallback {
    var stateModelList: List<AreasModel>? = null
    lateinit var binding: ActivityAddNewAddressBinding
    var isEdit = false
    var addressId = 0
    var addressModel: AddressModel? = null
    var stateId = 0
    var stateNames: List<String>? = null
    var stateName = ""
    var selectedCityId = 0
    var countryCode = "+973"
    private var map: GoogleMap? = null
    private var phonePrefix = "973"
    private var countryId = 17
    private var localModel: LocalModel? = null
    private var isShowing = false
    private var fragment: SupportMapFragment? = null
    private var cameraUpdate: CameraUpdate? = null
    private val zoomLevel = 14.0f
    private var selectedLat = 26.05177032598081
    private var selectedLng = 50.50513866994304
    private var autocompleteFragment: AutocompleteSupportFragment? = null
    private var placeLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.mapKey), Locale.US)
        }

        stateModelList = ArrayList()
        stateNames = ArrayList()
        setTitle(R.string.new_address)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )

        if (localModel?.countryName != null && localModel?.phonecode != null) {
            binding.codeSpinner.setText(
                localModel?.countryName.plus(" ( ").plus(localModel?.phonecode).plus(" ) ")
            )
            countryCode = "+" + localModel!!.phonecode.toString()
            phonePrefix = localModel!!.phonecode.toString()
        }

        getIntentData

        val fm = supportFragmentManager
        fragment = fm.findFragmentById(R.id.map) as SupportMapFragment?
        if (fragment != null) {
            fragment?.getMapAsync(this)
        }

        binding.stateSpinnerTv.inputType = InputType.TYPE_NULL
        binding.codeSpinner.inputType = InputType.TYPE_NULL
        if (localModel != null && localModel?.shortname == "KW") {
            binding.blockEt.hint = getString(R.string.block_kw)
        }

        if (isEdit) {
            binding.addNewAddressBut.visibility = View.GONE
        } else {
            binding.addNewAddressBut.visibility = View.VISIBLE
        }
        initPlaceAutoComplete()

        countryId = localModel?.countryId ?: Constants.default_country_id

        initListeners()
    }

    fun initListeners() {

        placeLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.resultCode == RESULT_OK) {
                val place =
                    Autocomplete.getPlaceFromIntent(
                        result.data!!
                    )
                Log.i("TAG", "Place: " + place.name + ", " + place.id)
            } else if (result!!.resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status =
                    Autocomplete.getStatusFromIntent(result.data!!)
                Log.i("TAG", "Log $status.statusMessage")
            } else if (result.resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("TAG", "Log $result.resultCode.toString()")

            }
        }

        binding.myLocationBtn.setOnClickListener { checkLocationPermission() }

        binding.addNewAddressBut.setOnClickListener {
            if (isValidForm && selectedCityId > 0 && isShowing) {
                createNewAddress()
            } else {
                if (Objects.requireNonNull(binding.nameEt.text).toString().isEmpty()) {
                    Toast(R.string.enter_name)
                    binding.nameEt.requestFocus()
                } else if (selectedCityId == 0) {
                    YoYo.with(Techniques.Shake).playOn(binding.stateSpinner1Input)
                    Toast(R.string.select_area)
                }

            }
        }
        binding.codeSpinner.setOnClickListener {
            val countryCodeDialog =
                CountryCodeDialog(
                    activity, countryId, object :
                            DataFetcherCallBack {
                        override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                            val countryModel = obj as CountryModel?
                            if (countryModel != null) {
                                countryCode = "+" + countryModel.phonecode.toString()
                                phonePrefix = countryModel.phonecode.toString()
                                binding.codeSpinner.setText(
                                    countryModel.countryName.plus(" ( ").plus(countryModel.phonecode)
                                        .plus(" ) ")
                                )

                            }
                        }

                    })
            countryCodeDialog.show()
        }
        binding.stateSpinnerTv.setOnClickListener {
            val stateDialog = StateDialog(
                activity,
                selectedCityId, object :
                        DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        val areasModel =
                            obj as AreasModel?
                        if (areasModel != null) {
                            binding.stateSpinnerTv.setText(areasModel.stateName)
                            stateName = areasModel.stateName
                            selectedCityId = areasModel.id
                        } else {
                            binding.stateSpinnerTv.setText(getString(R.string.select_area))
                        }
                    }

                })
            stateDialog.show()
            isShowing = true
        }
    }

    private fun createNewAddress() {
        stateId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        val userId =
            if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) UtilityApp.getUserData().id else 0
        val addressModel = AddressModel()
        addressModel.name = binding.nameEt.text.toString()
        addressModel.areaId = selectedCityId
        addressModel.state = stateId
        addressModel.block = binding.blockEt.text.toString()
        addressModel.streetDetails = binding.streetEt.text.toString()
        addressModel.houseNo = binding.buildingEt.text.toString()
        addressModel.apartmentNo = binding.flatEt.text.toString()
        addressModel.phonePrefix = phonePrefix
        addressModel.mobileNumber = binding.phoneTv.text.toString()
        addressModel.latitude = selectedLat
        addressModel.longitude = selectedLng
        addressModel.userId = userId
        addressModel.googleAddress = binding.addressTV.text.toString()

        progressDialog(activity, R.string.add_new_address, R.string.please_wait_creat)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                val result = obj as AddressResultModel?
                var message: String? = "error_in_data"
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.fail_to_addAddress, message)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_get_data)
                } else if (func == Constants.NO_CONNECTION) {
                    Toast(activity, getString(R.string.no_internet_connection))
                } else {
                    if (IsSuccess) {
                        val intent = Intent(activity, AddressActivity::class.java)
                        intent.putExtra(Constants.KEY_ADD_NEW, true)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Toast(R.string.fail_to_addAddress)
                    }
                }
            }


        }).CreateAddressHandle(addressModel)
    }

    private val getIntentData: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                isEdit = bundle.getBoolean(Constants.KEY_EDIT)
                addressId = bundle.getInt(Constants.KEY_ADDRESS_ID, 0)
                getUserAddress(addressId)
            }
        }

    private fun getUserAddress(addressId: Int) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                binding.dataLY.visibility = View.VISIBLE
                val result = obj as AddressResultModel?
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                    binding.dataLY.visibility = View.VISIBLE
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_get_data)
                } else {
                    if (IsSuccess) {
                        binding.dataLY.visibility = View.VISIBLE
                        if (result != null && result.data != null && result.data.size > 0) {
                            addressModel = result.data[0]

                            selectedLat = addressModel?.latitude ?: 0.0
                            selectedLng = addressModel?.longitude ?: 0.0

                            if (map != null)
                                setMapMarker(getString(R.string.my_location), true)
                            setLocationAddressName
                            Log.i("tag", "Log Block " + addressModel?.block)
                            binding.addressTV.setText(addressModel?.fullAddress)
                            binding.nameEt.setText(addressModel?.name)
                            binding.streetEt.setText(addressModel?.streetDetails)
                            //                        binding.codeTv.setText(addressModel.getCountry());
                            binding.phoneTv.setText(addressModel?.mobileNumber)
                            binding.flatEt.setText(addressModel?.houseNo)
                            binding.blockEt.setText(addressModel?.block)
                            binding.buildingEt.setText(addressModel?.houseNo)


                        } else {
                            binding.dataLY.visibility = View.GONE
                        }
                    } else {
                        Toast(R.string.fail_to_get_data)
                    }
                }
            }

        }).GetAddressByIdHandle(addressId)
    }

    private val isValidForm: Boolean
        get() {
            val formValidator = getInstance()
            return formValidator.addField(binding.nameEt, NonEmptyRule(R.string.enter_name)).validate()

        }

    private fun checkLocationPermission() {
        try {
            val builder = PermissionCompat.Builder(activity)
            builder.addPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            builder.addPermissionRationale(getString(R.string.app_name))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    if (map != null) {
                        if (!isEdit) {
                            myLocation
                        }
                    }
                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                    Log.e("TAG", permission + "Denied")
                }
            })
            builder.build().request()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    private val myLocation: Unit
        get() {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                binding.loadingLocationLY.visibility = View.VISIBLE
                SmartLocation.with(activity).location().oneFix().start { location: Location ->
                    binding.loadingLocationLY.visibility = View.GONE
                    selectedLat = location.latitude
                    selectedLng = location.longitude
                    val latLng = LatLng(selectedLat, selectedLng)
                    map?.clear()
                    map?.addMarker(
                        MarkerOptions().position(latLng).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                ImageHandler.getBitmap(
                                    activity,
                                    R.drawable.location_icons
                                )
                            )
                        ).title(getString(R.string.my_location))
                    )
                    setLocationAddressName
                    val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            latLng,
                            zoomLevel
                        )
                    )
                    map?.animateCamera(cameraUpdate)
                }
            } else {
                showGPSDisabledAlertToUser()
            }
        }

    private fun initPlaceAutoComplete() {
//        Places.initialize(activiy, getString(R.string.mapKey), Locale.US)
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        if (autocompleteFragment != null) {
            autocompleteFragment?.setHint(getString(R.string.searchaddress))
            autocompleteFragment?.setPlaceFields(
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
                )
            )

            autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    selectPlace(
                        place.latLng?.latitude ?: 0.0,
                        place.latLng?.longitude ?: 0.0,
                        place.name ?: ""
                    )
                }

                override fun onError(status: Status) {
                    Log.i("TAG", "An error occurred: status$status")
                }
            })
        }
    }

    fun selectPlace(lat: Double, lng: Double, name: String) {
//        try {
        map?.clear()
        selectedLat = lat
        selectedLng = lng
        setMapMarker(name, true)
        setLocationAddressName

    }

    private fun openAutoCompleteActivity() {

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        placeLauncher?.launch(intent)
    }

    override fun onResume() {
        super.onResume()
//        if (autocompleteFragment == null) initPlaceAutoComplete()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (!isEdit) {
            map?.setOnMapClickListener {
                selectedLat = it.latitude
                selectedLng = it.longitude

                setMapMarker(getString(R.string.my_location), false)
                setLocationAddressName
            }

            // to open search when open first time to add new address
//            openAutoCompleteActivity()
            checkLocationPermission()
        }

        setMapMarker(getString(R.string.my_location), true)


    }

    private fun setMapMarker(title: String, moveCamera: Boolean) {
        map?.clear()
        map?.addMarker(
            MarkerOptions()
                .position(LatLng(selectedLat, selectedLng)).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        ImageHandler.getBitmap(
                            activity, R.drawable.location_icons
                        )
                    )
                ).title(title)
        )
        if (moveCamera) {
            cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    LatLng(
                        selectedLat,
                        selectedLng
                    ), zoomLevel
                )
            )
            map?.moveCamera(cameraUpdate!!)
        }

    }

    private val setLocationAddressName: Unit
        get() {
            val address = MapHandler.getGpsAddress(activity, selectedLat, selectedLng)
            Log.i("TAG", "Log My selectedLat: $selectedLat")
            Log.i("TAG", "Log My selectedLng: $selectedLng")
            Log.i("TAG", "Log My Location: $address")
            binding.addressTV.setText(address)
        }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(
            activity
        )
        alertDialogBuilder.setMessage(getString(R.string.open_gps)).setCancelable(false)
            .setPositiveButton(
                getString(R.string.enable)
            ) { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                val callGPSSettingIntent = Intent("android.settings.LOCATION_SOURCE_SETTINGS")
                startActivity(callGPSSettingIntent)
                dialog.cancel()
            }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_tex), null)
        val alert = alertDialogBuilder.create()
        try {
            alert.show()
        } catch (e: BadTokenException) {
            //use a log message
        }
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}


