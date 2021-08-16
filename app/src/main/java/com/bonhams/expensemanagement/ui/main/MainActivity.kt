package com.bonhams.expensemanagement.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.NavDrawerAdapter
import com.bonhams.expensemanagement.data.model.NavDrawerItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailFragment
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.gpsTracking.GPSTrackingFragment
import com.bonhams.expensemanagement.ui.home.HomeFragment
import com.bonhams.expensemanagement.ui.login.LoginActivity
import com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail.MileageDetailFragment
import com.bonhams.expensemanagement.ui.myProfile.MyProfileFragment
import com.bonhams.expensemanagement.ui.notification.NotificationFragment
import com.bonhams.expensemanagement.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var navDrawerAdapter: NavDrawerAdapter
    val TAG = "MainActivity"

    private var navDrawerItems = arrayListOf(
        NavDrawerItem(R.drawable.ic_home, "Expense", -1),
        NavDrawerItem(R.drawable.ic_nav_expense_plus, "Manually Create", 1),
        NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2),
        NavDrawerItem(R.drawable.ic_home, "Mileage", -1),
        NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3),
        NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4),
        NavDrawerItem(R.drawable.ic_profile, "Others", -1),
        NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5),
        NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6),
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setupNavDrawer()
        setupViewModel()
        setupClickListeners()
        setupAppbar()

        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            changeFragment(fragment)
        }
    }

    override fun onBackPressed() {
        if (navDrawer.isDrawerOpen(GravityCompat.START)) {
            navDrawer.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true)
            when (menuItem.itemId) {
                R.id.bottom_nav_home -> {
                    setupAppbar()
                    showBottomNavbar(true)
                    val fragment = HomeFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_camera -> {
                    setAppbarTitle(getString(R.string.scan_receipt))
                    showBottomNavbar(true)
                    val fragment = ClaimDetailFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_notifications -> {
                    setAppbarTitle(getString(R.string.notifications))
                    showBottomNavbar(true)
                    val fragment = NotificationFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_my_profile -> {
                    setAppbarTitle(getString(R.string.profile))
                    showBottomNavbar(true)
                    val fragment = MyProfileFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun setupClickListeners(){
        layoutBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        ivMenu.setOnClickListener(View.OnClickListener {
            if (!navDrawer.isDrawerOpen(GravityCompat.START)) {
                navDrawer.openDrawer(GravityCompat.START)
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
        
        viewModel.responseLogout?.observe(this, Observer {
            Log.d(TAG, "setupViewModel: ${it.message}")
        })
    }
    
    private fun setupAppbar(){
        appbarTitle.visibility = View.GONE
        layoutAppBarMenu.visibility = View.VISIBLE
        layoutGreeting.visibility = View.VISIBLE
        ivMenu.visibility = View.VISIBLE
        layoutBack.visibility = View.GONE
        layoutAppBarSearch.visibility = View.VISIBLE
    }

    fun setAppbarTitle(title: String){
        appbarTitle.visibility = View.VISIBLE
        appbarTitle.text = title
        layoutGreeting.visibility = View.GONE
        ivMenu.visibility = View.VISIBLE
        layoutBack.visibility = View.GONE
        layoutAppBarSearch.visibility = View.VISIBLE
    }

    fun showAppbarBackButton(show: Boolean){
        appbarTitle.visibility = if(show) View.VISIBLE else View.GONE
        layoutGreeting.visibility = View.GONE
        ivMenu.visibility = View.GONE
        layoutBack.visibility = if(show) View.VISIBLE else View.GONE
        showAppbarSearch(false)
    }

    fun showAppbarSearch(show: Boolean){
        layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.GONE
        ivSearch.visibility = if(show) View.VISIBLE else View.GONE
        appbarEdit.visibility = View.GONE
    }


    fun showAppbarEdit(show: Boolean){
        layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.GONE
        appbarEdit.visibility = if(show) View.VISIBLE else View.GONE
        ivSearch.visibility = View.GONE
    }

    fun showBottomNavbar(show: Boolean){
        bottomNavigationView.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun setupNavDrawer(){
        tvNavDrawerAppVersion.text = "Version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        // Setup Recyclerview's Layout
        navDrawerRv.layoutManager = LinearLayoutManager(this)
        navDrawerRv.setHasFixedSize(true)

        // Add Item Touch Listener
        navDrawerRv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                Log.d(TAG, "navDrawerRv onClick: $position")
                Log.d(TAG, "navDrawerRv onClick code: ${navDrawerItems[position].code}")
                when (navDrawerItems[position].code) {
                    1 -> { // Manually Create
                        setAppbarTitle(getString(R.string.create_new_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewClaimFragment()
                        addFragment(fragment)
                    }
                    2 -> { // Scan Receipt
                        setAppbarTitle(getString(R.string.scan_receipt))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = HomeFragment()
                        addFragment(fragment)
                    }
                    3 -> { // Manually Create
                        setAppbarTitle(getString(R.string.create_mileage_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = MileageDetailFragment()
                        addFragment(fragment)
                    }
                    4 -> { // Start GPS
                        setAppbarTitle(getString(R.string.start_gps))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = GPSTrackingFragment()
                        addFragment(fragment)
                    }
                    5 -> { // My Account
                        setAppbarTitle(getString(R.string.profile))
                        showBottomNavbar(true)
                        showAppbarBackButton(true)
                        val fragment = MyProfileFragment()
                        addFragment(fragment)
                    }
                    6 -> { // Logout
                        showLogoutAlert()
                    }
                    else -> {
                        setupAppbar()
                        showBottomNavbar(true)
                        val fragment = HomeFragment()
                        changeFragment(fragment)
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    navDrawer.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

//        updateAdapter(0)
        navDrawerAdapter = NavDrawerAdapter(navDrawerItems, -1)
        navDrawerRv.adapter = navDrawerAdapter
        navDrawerAdapter.notifyDataSetChanged()
    }

    /*private fun updateAdapter(highlightItemPos: Int) {
        navDrawerAdapter = NavDrawerAdapter(navDrawerItems, highlightItemPos)
        navDrawerRv.adapter = navDrawerAdapter
        navDrawerAdapter.notifyDataSetChanged()
    }*/

    fun showLogoutAlert(){
        Log.d(TAG, "showLogoutAlert: ${resources.getString(R.string.logout)}")
        /*val dialog = CustomAlertDialog(this@MainActivity)
        dialog.apply {
            showNegativeButton(false)
            setTitle(resources.getString(R.string.logout))
            setPositiveText(resources.getString(R.string.logout))
            setNegativeText(resources.getString(R.string.cancel))
            setCallback(object: CustomAlertDialog.DialogButtonCallback {
                override fun onPositiveClick() {
                    Log.d(TAG, "onPositiveClick: ")
                    dialog.dismiss()
                    logoutUser()
                }
                override fun onNegativeClick() {
                    Log.d(TAG, "onNegativeClick: ")
                    dialog.dismiss()
                }
            })
        }*/
//        dialog.show()
        logoutUser()
    }

    fun logoutUser(){
        Log.d(TAG, "Logout User.......")
        viewModel.logoutUser().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            AppPreferences.clearPrefs()
                            Log.d(TAG, "logoutUser: ${response.message}")

                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Log.d(TAG, "logoutUser: $it1")}
                    }
                    Status.LOADING -> {
                        Log.d(TAG, "Loading.......")
                    }
                }

            }
        })
    }

    private fun hasLocationForegroundPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationBackgroundPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasFineLocationPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun requestPermission() {
        var permissionToRequest = mutableListOf<String>()
        if (!hasLocationForegroundPermission())
            permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (!hasLocationBackgroundPermission())
            permissionToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if (!hasFineLocationPermission())
            permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionToRequest.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    viewModel.locationGranted = true
                    Log.d("MainActivity", "onRequestPermissionsResult: ${permissions[i]}")
                }
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment,
            fragment.javaClass.getSimpleName()
        ).commit()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment,
            fragment.javaClass.getSimpleName()
        ).addToBackStack(fragment.javaClass.getSimpleName()).commit()
    }
}