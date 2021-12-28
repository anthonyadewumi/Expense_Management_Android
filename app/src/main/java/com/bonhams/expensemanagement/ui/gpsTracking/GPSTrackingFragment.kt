package com.bonhams.expensemanagement.ui.gpsTracking

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.GpsMileageDetail
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.utils.AppPreferences
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class GPSTrackingFragment : Fragment(),LocationListener {

    private var contextActivity: BaseActivity? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var googleMap: GoogleMap
    private lateinit var progDialog: ProgressDialog
    private val locationPermissionCode = 2
    private var Latitude = 0.0
    private var Longitude = 0.0
    private var startlocation = ""
    private var stoplocation = ""
    private var startDate = ""
    private var stopDate= ""
    var FASTEST_INTERVAL: Long = 8 * 1000 // 8 SECOND
    var UPDATE_INTERVAL: Long = 2000 // 2 SECOND
    var FINE_LOCATION_REQUEST: Int = 888
    lateinit var locationRequest: LocationRequest

    private val callback = OnMapReadyCallback { map ->
      //  obtieneLocalizacion()
        getLocation()

        googleMap=map

       /* val currentlocation = LatLng(Latitude, Longitude)
        map.addMarker(MarkerOptions().position(currentlocation).title("Marker in current location"))
        map.moveCamera(CameraUpdateFactory.newLatLng(currentlocation))*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gps_tracking, container, false)
        contextActivity = activity as? BaseActivity

        (contextActivity as MainActivity).setAppbarTitle(getString(R.string.start_gps))
        (contextActivity as MainActivity).showBottomNavbar(false)
        (contextActivity as MainActivity).showAppbarBackButton(true)

        val startStop = view.findViewById(R.id.mStartStop) as AppCompatButton?
        progDialog= ProgressDialog(requireContext())
        progDialog.setTitle("Getting current location...")
        progDialog.show()
        println("map ready start")
        if(AppPreferences.gpsStart == "Stop"){
            startStop?.text="Stop"
            startlocation= AppPreferences.gpsStartLocation
            startDate=AppPreferences.gpsStartDate
        }

        startStop?.setOnClickListener {
            if(startStop.text.toString() == "Start"){

                println("current date :"+getCurrentDate())
                getLocation()
               startDate=getCurrentDate()
                startlocation= getCompleteAddressString(Latitude,Longitude)

                startStop.text="Stop"
                AppPreferences.gpsStartDate=startDate
                AppPreferences.gpsStartLocation=startlocation
                AppPreferences.gpsStart="Stop"
            }else{
                getLocation()

                startStop.text="Start"
                AppPreferences.gpsStart="Start"
                stoplocation= getCompleteAddressString(Latitude,Longitude)
                stopDate=getCurrentDate()

                val details= GpsMileageDetail(startDate,stopDate,startlocation,stoplocation)
                val fragment = NewMileageClaimFragment()
                fragment.setAutoMileageDetails(details)
                addFragment(fragment)
            }
        }

        //getLocation()
        return view
    }

    @SuppressLint("MissingPermission")
    fun initLocationUpdate() {

        // Check API revision for New Location Update
        //https://developers.google.com/android/guides/releases#june_2017_-_version_110

        //init location request to start retrieving location update
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL

        //Create LocationSettingRequest object using locationRequest
        val locationSettingBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        locationSettingBuilder.addLocationRequest(locationRequest)
        val locationSetting: LocationSettingsRequest = locationSettingBuilder.build()

        //Need to check whether location settings are satisfied
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(requireContext())
        settingsClient.checkLocationSettings(locationSetting)
        //More info :  // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                //super.onLocationResult(p0)
                if (locationResult != null) {
                    onLocationChangeds(locationResult.lastLocation)
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
            }
        },
            Looper.myLooper())

    }

   /* fun onLocationChanged(location: Location) {
        // New location has now been determined
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        println("location :"+msg)

        toast.setText(msg)
        toast.show()
    }*/


    private fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd MMM yy")
        //val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return sdf.format(Date())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?



        mapFragment?.getMapAsync(callback)
    }

    fun addFragment(fragment: Fragment) {
        contextActivity?.supportFragmentManager?.beginTransaction()?.add(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
    }
    private fun obtieneLocalizacion(){
       /* fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                var latitude = location?.latitude
                var longitude = location?.longitude
                println("latitude :$latitude")
                println("longitude :$longitude")
            }*/
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        statusCheck()
        if ((ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

          /*  ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), locationPermissionCode
            )*/
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), locationPermissionCode
            )
           // ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else {

            initLocationUpdate()

          /*  locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f,
                locationListener
            );*/
        }
    }
    fun onLocationChangeds(location: Location) {
        // New location has now been determined
        Latitude=location.latitude
        Longitude=location.longitude
        try {
            if(progDialog.isShowing)
                progDialog.dismiss()
        } catch (e: Exception) {
        }

        val currentlocation = LatLng(Latitude, Longitude)
        googleMap.clear()
        googleMap.setMinZoomPreference(11f)
        googleMap.addMarker(MarkerOptions().position(currentlocation).title("Marker in current location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation))
        println("currentlocation $currentlocation")
    }
   override fun onLocationChanged(location: Location) {
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                getLocation()
            }
            else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Latitude=location.latitude
            Longitude=location.longitude
            try {
                if(progDialog.isShowing)
                progDialog.dismiss()
            } catch (e: Exception) {
            }

            val currentlocation = LatLng(Latitude, Longitude)
            googleMap.clear()
            googleMap.setMinZoomPreference(11f)
            googleMap.addMarker(MarkerOptions().position(currentlocation).title("Marker in current location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation))
            println("currentlocation $currentlocation")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        println("get location LATITUDE:$LATITUDE")
        println("get location LONGITUDE:$LONGITUDE")
        var strAdd = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            var result: String? = null

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                sb.append(address.locality).append("\n")
                sb.append(address.postalCode).append("\n")
                sb.append(address.countryName)
                result = sb.toString()
                println("get location result:$result")

            }

            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("loction address", strReturnedAddress.toString())
            } else {
                Log.w("Curent rloction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(" loction address", "Canont get Address!")
        }
        return strAdd
    }
    fun statusCheck() {
        val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}