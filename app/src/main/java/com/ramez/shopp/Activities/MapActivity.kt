package com.ramez.shopp.Activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ImageHandler
import com.ramez.shopp.Utils.MapHandler
import com.wang.avi.AVLoadingIndicatorView
import io.nlopez.smartlocation.SmartLocation
import java.util.*

class MapActivity : ActivityBase(), OnMapReadyCallback {

    var map: GoogleMap? = null
    var fragment: SupportMapFragment? = null
    var cameraUpdate: CameraUpdate? = null
    var zoomLevel = 12f

    private var latLng: LatLng? = null
    private var mapType: Int = 0
    private var selectedLat = 26.05177032598081
    private var selectedLng = 50.50513866994304
    private var autocompleteFragment: AutocompleteSupportFragment? = null

    var isGrantPermission = false

    lateinit var myLocationBtn:TextView
    lateinit var confirmBtn:TextView
    lateinit var tvPhysicalAddress:TextView
    lateinit var loadingLocationLY:AVLoadingIndicatorView


    companion object {
        const val REQUEST_SELECT_LOCATION = 122
        const val REQUEST_SELECT_DESTINATION_LOCATION = 123

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)
        myLocationBtn=findViewById(R.id.myLocationBtn)
        confirmBtn=findViewById(R.id.confirmBtn)
        tvPhysicalAddress=findViewById(R.id.tvPhysicalAddress)
        loadingLocationLY=findViewById(R.id.loadingLocationLY)

        latLng = LatLng(selectedLat, selectedLng)

        checkLocationPermission()
        setTitle(R.string.map_str)

        initPlaceAutoComplete()

        val fm: FragmentManager = supportFragmentManager
        fragment = fm.findFragmentById(R.id.map) as SupportMapFragment
        fragment?.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        cameraUpdate =
                CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                                latLng,
                                zoomLevel
                        )
                )
        map?.moveCamera(cameraUpdate)

        var mapMarker = 0
        var markerTitle = ""

        when (mapType) {
            1 -> {
                if (isGrantPermission)
                    getMyLocation()

                mapMarker = R.drawable.location_icons
                markerTitle = getString(R.string.my_location)

            }
            2 -> {

                mapMarker = R.drawable.location_icons
                markerTitle = getString(R.string.my_location)

            }

            else -> {
                mapMarker = R.drawable.location_icons
                markerTitle = getString(R.string.my_location)
            }
        }

        map?.addMarker(
                MarkerOptions()
                        .position(latLng!!)
                        .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                        ImageHandler.getBitmap(
                                                getActiviy(), mapMarker
                                        )
                                )
                        )
                        .title(markerTitle)

        )

        map?.setOnMapClickListener {

            map?.clear()
            map?.addMarker(
                    MarkerOptions()
                            .position(
                                    LatLng(it.latitude, it.longitude)
                            )
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            ImageHandler.getBitmap(
                                                    getActiviy(), mapMarker
                                            )
                                    )
                            )
                            .title(markerTitle)

            )
            selectedLat = it.latitude
            selectedLng = it.longitude
            tvPhysicalAddress.text = MapHandler.getPhysicalLocation(applicationContext, selectedLat.toString() + "", selectedLng.toString() + "")


        }

        myLocationBtn.setOnClickListener {

            checkLocationPermission()

        }

        confirmBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Constants.KEY_LAT, selectedLat.toString())
            intent.putExtra(Constants.KEY_LNG, selectedLng.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()

        }





    }

    private fun checkLocationPermission() {
        try {
            val builder = PermissionCompat.Builder(getActiviy())
            builder.addPermissions(
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
            )
            builder.addPermissionRationale(getString(R.string.app_name))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    isGrantPermission = true
                    if (map != null)
                        getMyLocation()
                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                }
            })
            builder.build().request()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getMyLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            loadingLocationLY.visibility = View.VISIBLE

            SmartLocation.with(activiy).location()
                    .oneFix()
                    .start { location ->
                        loadingLocationLY.visibility = View.GONE

                        val lat: Double = location.latitude
                        val lng: Double = location.longitude
                        map?.clear()
                        map?.addMarker(
                                MarkerOptions()
                                        .position(
                                                LatLng(lat, lng)
                                        )
                                        .icon(
                                                BitmapDescriptorFactory.fromBitmap(
                                                        ImageHandler.getBitmap(
                                                                activiy, R.drawable.location_icons
                                                        )
                                                )
                                        )
                                        .title(getString(R.string.my_location))
                        )
                        tvPhysicalAddress.text = MapHandler.getPhysicalLocation(applicationContext, lat.toString(), lng.toString())

                        val latLn = LatLng(lat, lng)
                        val cameraUpdate =
                                CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.fromLatLngZoom(
                                                latLn,
                                                zoomLevel
                                        )
                                );
                        map?.animateCamera(cameraUpdate)


                    }

        } else {
            showGPSDisabledAlertToUser()
        }


    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(getActiviy())
        alertDialogBuilder.setMessage(getString(R.string.open_gps))
                .setCancelable(false)
                .setPositiveButton(
                        getString(R.string.enable)
                ) { dialog, id ->
                    dialog.cancel()
                    val callGPSSettingIntent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                    dialog.cancel()
                }
        alertDialogBuilder.setNegativeButton(
                getString(R.string.cancel_tex)
        ) { dialog, id -> dialog.cancel() }
        val alert: AlertDialog = alertDialogBuilder.create()
        alert.show()
    }

    private fun initPlaceAutoComplete() {
        // TODO: Places initialize
        Places.initialize(applicationContext, getString(R.string.mapKey), Locale.US)
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setHint(getString(R.string.searchaddress))
        assert(autocompleteFragment != null)
        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("TAG", "onPlaceSelected: " + place.latLng + ", " + place.id + ", " + place.name)
                try {
                    map?.clear()
                    selectedLng = place.latLng!!.longitude
                    selectedLat = place.latLng!!.latitude
                    val markerOptions = MarkerOptions()
                    markerOptions.position(LatLng(selectedLng, selectedLng))
                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(selectedLat, selectedLng), zoomLevel)
                    map?.animateCamera(cameraUpdate)
                    tvPhysicalAddress.text = MapHandler.getPhysicalLocation(applicationContext, selectedLat.toString() + "", selectedLng.toString() + "")
                    var mapMarker = R.drawable.location_icons

                    map?.addMarker(
                            MarkerOptions()
                                    .position(
                                            LatLng(selectedLat, selectedLng)
                                    )
                                    .icon(
                                            BitmapDescriptorFactory.fromBitmap(
                                                    ImageHandler.getBitmap(
                                                            getActiviy(), mapMarker
                                                    )
                                            )
                                    )
                                    .title( MapHandler.getPhysicalLocation(applicationContext, selectedLat.toString() + "", selectedLng.toString() + ""))

                    )


                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $status")
            }
        })
    }


}
