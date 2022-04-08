package com.bonhams.expensemanagement.ui.main

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.BuildConfig
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.NavDrawerAdapter
import com.bonhams.expensemanagement.data.model.NavDrawerItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.MIleageDetailsResponse
import com.bonhams.expensemanagement.databinding.ActivityMainBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.BlankFragment
import com.bonhams.expensemanagement.ui.OCR.CapturedImageFragment
import com.bonhams.expensemanagement.ui.batch.BatchFragment
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailFragment
import com.bonhams.expensemanagement.ui.claims.claimDetail.claimedit.EditClaimFragment
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.EditSplitClaimDetailsFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimDetailsFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.gpsTracking.GPSTrackingFragment
import com.bonhams.expensemanagement.ui.home.HomeFragment
import com.bonhams.expensemanagement.ui.login.LoginActivity
import com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail.MileageDetailFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.EditMileageClaimFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.ui.myProfile.MyProfileFragment
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment
import com.bonhams.expensemanagement.ui.myProfile.editProfile.EditProfileFragment
import com.bonhams.expensemanagement.ui.notification.NotificationFragment
import com.bonhams.expensemanagement.ui.rmExpence.EditClaimRMFragment
import com.bonhams.expensemanagement.ui.rmExpence.EditMileageClaimRMFragment
import com.bonhams.expensemanagement.ui.rmExpence.ExpenceToBeAccepted
import com.bonhams.expensemanagement.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var navDrawerAdapter: NavDrawerAdapter
    private val TAG = javaClass.simpleName
    var isEdit=false
    var isclaim_mileage=false

   private var navDrawerItems = arrayListOf(
        NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1),
        NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1),
     //   NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2),
        NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1),
        NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3),
        NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4),
        NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1),
        NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5),
        NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6),
    )
    private lateinit var binding: ActivityMainBinding

    companion object {
        var isMilageClaim = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        println("user type :"+AppPreferences.userType)
       // printHashKey(this)

      /*  val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))*/

        when (AppPreferences.userType) {
            "Reporting Manager" -> {

                navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1))
                //    navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Expense to be approved", 7))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))


                //  navDrawerItems.add(3, NavDrawerItem(R.drawable.ic_nav_scan, "Expense to be approved ", 7))
            }
            "Finance Department" -> {

                navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1))
                //    navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Expense to be approved", 7))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))

                /*  navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))*/
            }
            "Admin" -> {

                navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1))
                //    navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Expense to be approved", 7))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))

                /*  navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))*/
            }
            "Final Approver" -> {

                navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1))
                //    navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Expense to be approved", 7))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))

                /*  navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))*/
            }
            else -> {
                navDrawerItems.clear()
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Expense", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Manually Create", 1))
                // navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Mileage", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 3))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 4))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_plus_circle, "Others", -1))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 5))
                navDrawerItems.add( NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 6))
            }
        }
      //  binding.bottomNavigationView.menu.removeItem(R.id.bottom_nav_camera)

        if(AppPreferences.userType == "Finance Department"){
            try {
              //  binding.bottomNavigationView.menu.removeItem(R.id.bottom_nav_camera)
              //  binding.bottomNavigationView.menu.removeItem(R.id.bottom_nav_expense_plus)
             //   navDrawerItems.removeAt(0)
              //  navDrawerItems.removeAt(1)
             //   navDrawerItems.removeAt(2)
             //   navDrawerItems.removeAt(3)
              //  navDrawerItems.removeAt(4)
              //  navDrawerItems.removeAt(5)
              //  navDrawerItems.removeAt(6)
            } catch (e: Exception) {
            }
        }
        setupNavDrawer()
        setupViewModel()
        setupClickListeners()
        setupAppbar()
        setNoInternetSnackbar()
        fragmentBackstackListener()
        if (savedInstanceState == null) {
           // val fragment = HomeFragment()

            val extras = intent.extras
            if(extras!=null) {
                val request_type = extras.get("request_type") as String

                if (request_type == "claim") {
                    val details =
                        intent.getSerializableExtra("details_edit") as? ClaimDetailsResponse
                    if (details != null) {
                        showBottomNavbar(false)
                        setAppbarTitle(getString(R.string.edit_claims))
                        showAppbarBackButton(true)
                        val fragment = EditClaimRMFragment()
                        fragment.setClaimDetails(details)
                        changeFragment(fragment)
                        isEdit=true
                    }

                }
                if (request_type == "mileage") {
                    val details = intent.getSerializableExtra("details_edit") as? MIleageDetailsResponse
                    if (details != null) {
                        showBottomNavbar(false)
                        setAppbarTitle(getString(R.string.edit_mileage))
                        showAppbarBackButton(true)
                        val fragment = EditMileageClaimRMFragment()
                        fragment.setMileageDetails(details)
                        changeFragment(fragment)
                        isEdit=true
                    }

                }
            }else{
                val fragment = BatchFragment()
                addFragment(fragment)
            }



            /*if(AppPreferences.userType.equals("Finance Department")){
                val fragment = FinanceHomeFragment()
                changeFragment(fragment)
            }else{
                val fragment = HomeFragment()
                changeFragment(fragment)
            }*/


        }
        addFcmKey()
    }

    override fun onResume() {
        super.onResume()
        binding.navDrawerTitle.text = AppPreferences.fullName

        println("profile img ${AppPreferences.profilePic}")
        Glide.with(this)
            .load(AppPreferences.profilePic)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
            )
            .placeholder(R.drawable.ic_default_user)
            .error(R.drawable.ic_default_user)
            .circleCrop()
            .into(binding.navDrawerProfilePic)
        myProfile()

    }
    override fun onBackPressed() {
        binding.navDrawerTitle.text = AppPreferences.fullName

        if (binding.navDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.navDrawer.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                backButtonPressed()
            } else {
                // Exit the app
                super.onBackPressed()
            }

          /*  val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment != null) {
                val fragName = fragment.javaClass.simpleName
                if (fragName == "CapturedImageFragment") {
                    setupAppbar()
                    showBottomNavbar(true)
                    removeAnyOtherFragVisible()
                    // val fragment = HomeFragment()
                    val fragment = BatchFragment()
                    changeFragment(fragment)
                } else {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        backButtonPressed()
                    } else {
                        // Exit the app
                        super.onBackPressed()
                    }
                }
            }else{
                super.onBackPressed()
            }*/
        }
    }

     fun backButtonPressed(){
         binding.navDrawerTitle.text = AppPreferences.fullName

         // Go to the previous fragment
        supportFragmentManager.popBackStack()
         println("chk this"+supportFragmentManager.backStackEntryCount)

         // Reset app bar
        if (supportFragmentManager.backStackEntryCount == 1) {
            setupAppbar()
            showBottomNavbar(true)
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            myProfile()
            binding.bottomNavigationView.menu.setGroupCheckable(0, true, true)
            when (menuItem.itemId) {
                R.id.bottom_nav_home -> {
                    if(AppPreferences.userType == "Finance Department"){
                        setupAppbar()
                        showBottomNavbar(true)
                        removeAnyOtherFragVisible()
                       // val fragment = HomeFragment()
                        val fragment = BatchFragment()
                        changeFragment(fragment)
                        return@OnNavigationItemSelectedListener true
                    }else{
                        setupAppbar()
                        showBottomNavbar(true)
                        removeAnyOtherFragVisible()
                       // val fragment = HomeFragment()
                        val fragment = BatchFragment()
                        addFragment(fragment)
                        return@OnNavigationItemSelectedListener true
                    }

                }
                R.id.bottom_nav_camera -> {
                    setupAppbar()
                    setAppbarTitle(getString(R.string.scan_receipt))
                    showBottomNavbar(true)
                    val fragment = CapturedImageFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_expense_plus -> {
                    if(isMilageClaim)
                    {
                        setAppbarTitle(getString(R.string.create_mileage_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewMileageClaimFragment()
                        addFragment(fragment)
                    }else {
                        setAppbarTitle(getString(R.string.create_new_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewClaimFragment()
                        addFragment(fragment)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_notifications -> {
                    setAppbarTitle(getString(R.string.notifications))
                    showAppbarBackButton(false)
                    showAppbarMore(true)
                    showBottomNavbar(true)
                    val fragment = NotificationFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_my_profile -> {
                    setAppbarTitle(getString(R.string.profile))
                    hideAppbarBackAndMenu(true)
                    showAppbarEdit(true)
                    showBottomNavbar(true)

                    val fragment = MyProfileFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true

                }
            }
            false
        }

    private fun setupClickListeners(){
        binding.appBar.layoutBack.setOnClickListener(View.OnClickListener {
            println("clicked on back ")
            println("clicked on back isEdit"+isEdit)
            println("clicked on back isclaim_mileage"+isclaim_mileage)
            println("supportFragmentManager.backStackEntryCount : "+supportFragmentManager.backStackEntryCount)
            if(isEdit)
            {
                finish()

            }else if(isclaim_mileage){
                isclaim_mileage=false
                setupAppbar()
                showBottomNavbar(true)
                removeAnyOtherFragVisible()
                val fragment = BatchFragment()
                changeFragment(fragment)
            }
            if (supportFragmentManager.backStackEntryCount > 0) {
                viewModel.appbarbackClick?.value = it
                backButtonPressed()
            }else{
                setupAppbar()
                showBottomNavbar(true)
                removeAnyOtherFragVisible()
                val fragment = BatchFragment()
                changeFragment(fragment)
            }
        })

        binding.appBar.ivMenu.setOnClickListener(View.OnClickListener {
            if (!binding.navDrawer.isDrawerOpen(GravityCompat.START)) {
                binding.navDrawer.openDrawer(GravityCompat.START)
            }
        })

        binding.appBar.ivSearch.setOnClickListener(View.OnClickListener {
            viewModel.appbarSearchClick?.value = viewModel.appbarSearchClick?.value?.not()
        })

        binding.appBar.appbarEdit.setOnClickListener(View.OnClickListener {
            //binding.appBar.appbarSave.visibility = View.VISIBLE

            viewModel.appbarEditClick?.value = it
        })
        binding.appBar.appbarSave.setOnClickListener(View.OnClickListener {
            //binding.appBar.appbarSave.visibility = View.VISIBLE

            viewModel.appbarSaveClick?.value = it
            viewModel.isEdit=true
        })
        binding.appBar.ivMore.setOnClickListener(View.OnClickListener {
            viewModel.appbarMenuClick?.value = it
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
        binding.appBar.appbarTitle.visibility = View.GONE
        binding.appBar.layoutAppBarMenu.visibility = View.VISIBLE
        binding.appBar.layoutGreeting.visibility = View.VISIBLE
        binding.appBar.appbarGreeting.visibility = View.VISIBLE
        binding.appBar.ivMenu.visibility = View.VISIBLE
        binding.appBar.layoutBack.visibility = View.GONE
        binding.appBar.layoutAppBarSearch.visibility = View.VISIBLE
        binding.appBar.ivSearch.visibility = View.VISIBLE
        binding.appBar.ivMore.visibility = View.GONE
        binding.appBar.appbarEdit.visibility = View.GONE

        binding.appBar.appbarGreeting.text = "Hello ${AppPreferences.firstName}!"
    }

    private fun setupNavDrawer(){
        binding.navDrawerTitle.text = AppPreferences.fullName
        binding.navDrawerDescription.text = AppPreferences.email
        binding.tvNavDrawerAppVersion.text = "Version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
      /*println("profile img ${AppPreferences.profilePic}")
        Glide.with(this)
            .load(AppPreferences.profilePic)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
            )
            .placeholder(R.drawable.ic_default_user)
            .error(R.drawable.ic_default_user)
            .circleCrop()
            .into(binding.navDrawerProfilePic)*/

        // Setup Recyclerview's Layout
        binding.navDrawerRv.layoutManager = LinearLayoutManager(this)
        binding.navDrawerRv.setHasFixedSize(true)

        // Add Item Touch Listener
        binding.navDrawerRv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
            /*    Glide.with(this@MainActivity)
                    .load(AppPreferences.profilePic)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_default_user)
                            .error(R.drawable.ic_default_user)
                    )
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
                    .circleCrop()
                    .into(binding.navDrawerProfilePic)*/
                /*when (navDrawerItems[position].title) {
                    "Expense"->{

                    }
                    "Manually Create"->{
                        setAppbarTitle(getString(R.string.create_new_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewClaimFragment()
                        addFragment(fragment)
                    }
                    "Scan Receipt"->{
                        setAppbarTitle(getString(R.string.scan_receipt))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = BlankFragment()
                        addFragment(fragment)
                    }
                    "Expense to be approved"->{
                        val fp = Intent(applicationContext, ExpenceToBeAccepted::class.java)
                        startActivity(fp)
                        binding.navDrawer.closeDrawer(GravityCompat.START)
                    }
                    "Mileage"->{

                    }
                    "Manually Create"->{
                        setAppbarTitle(getString(R.string.create_mileage_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewMileageClaimFragment()
                        addFragment(fragment)
                    }
                    "Start GPS"->{
                        setAppbarTitle(getString(R.string.start_gps))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = GPSTrackingFragment()
                        addFragment(fragment)
                    }
                    "Others"->{

                    }
                    "My Account"->{
                        setAppbarTitle(getString(R.string.my_profile))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = MyProfileFragment()
                        addFragment(fragment)

                    }
                    "Logout"->{
                        showLogoutAlert()
                    }

                }
*/

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
                        val fragment = BlankFragment()
                        addFragment(fragment)
                    }
                    3 -> { // Manually Create
                        setAppbarTitle(getString(R.string.create_mileage_claim))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)
                        val fragment = NewMileageClaimFragment()
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
                        setAppbarTitle(getString(R.string.my_profile))
                        showBottomNavbar(false)
                        showAppbarBackButton(true)

                        val fragment = MyProfileFragment()
                        addFragment(fragment)

                        /*setAppbarTitle(getString(R.string.profile))
                        setupAppbar()
                        binding.bottomNavigationView.selectedItemId = R.id.bottom_nav_my_profile*/
                    }
                    6 -> { // Logout
                        showLogoutAlert()
                    }
                    7 -> {
                        val fp = Intent(applicationContext, ExpenceToBeAccepted::class.java)
                        startActivity(fp)
                        binding.navDrawer.closeDrawer(GravityCompat.START)

                    }
                    else -> {
                        setupAppbar()
                        showBottomNavbar(true)
                        //val fragment = HomeFragment()
                        val fragment = BatchFragment()
                        changeFragment(fragment)
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.navDrawer.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

//        updateAdapter(0)
        navDrawerAdapter = NavDrawerAdapter(navDrawerItems, -1)
        binding.navDrawerRv.adapter = navDrawerAdapter
        navDrawerAdapter.notifyDataSetChanged()
    }

    private fun showLogoutAlert() {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_alert_dialog)
        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val body = dialog.findViewById(R.id.txtDescription) as TextView
        val input = dialog.findViewById(R.id.edtDescription) as EditText
        val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
        val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

        input.visibility = View.GONE
        title.text = resources.getString(R.string.logout)
        body.text = resources.getString(R.string.are_you_sure_you_want_to_logout)
        yesBtn.text = resources.getString(R.string.logout)
        noBtn.text = resources.getString(R.string.cancel)

        yesBtn.setOnClickListener {
            dialog.dismiss()
            logoutUser()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun logoutUser(){
        viewModel.logoutUser().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            Log.d(TAG, "logoutUser: ${response.message}")
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

        AppPreferences.clearPrefs()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun addFcmKey(){
        val deviceId = Settings.Secure.getString(
            this.getContentResolver(), Settings.Secure.ANDROID_ID
        )
            println("device_id :"+deviceId)
            println("device_token :"+AppPreferences.fireBaseToken)
        val jsonObject = JsonObject().also {
            it.addProperty("device_id", deviceId)
            it.addProperty("device_token", AppPreferences.fireBaseToken)
        }

        viewModel.addFcmKey(jsonObject).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            Log.d(TAG, "add facm key: ${response.message}")
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Log.d(TAG, "add facm key: $it1")}
                    }
                    Status.LOADING -> {
                        Log.d(TAG, "Loading.......")
                    }
                }
            }
        })


    }



    private fun myProfile(){

        println("profile img ${AppPreferences.profilePic}")
        Glide.with(this)
            .load(AppPreferences.profilePic)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
            )
            .placeholder(R.drawable.ic_default_user)
            .error(R.drawable.ic_default_user)
            .circleCrop()
            .into(binding.navDrawerProfilePic)
        viewModel.myprofile().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            if(response.message.equals("invalid signature")||response.message.equals("Invalid token")){
                                showforceLogoutAlert()

                            }else{
                                println("total claimed miles :"+response.profileDetail?.distance_covered)
                                AppPreferences.claimedMils = response.profileDetail?.distance_covered ?: "0"
                            }
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
    private fun showforceLogoutAlert() {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_force_logout_alert_dialog)
        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val body = dialog.findViewById(R.id.txtDescription) as TextView
        val input = dialog.findViewById(R.id.edtDescription) as EditText
        //val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
        val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

        input.visibility = View.GONE
        title.text = resources.getString(R.string.logout)
        body.text = resources.getString(R.string.force_logout)
        //yesBtn.text = resources.getString(R.string.logout)
        noBtn.text = resources.getString(R.string.ok)


        noBtn.setOnClickListener {
            dialog.dismiss()
            AppPreferences.clearPrefs()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()

        }
        dialog.show()
    }

    /*
    * Location functions
    * */
    private fun hasLocationForegroundPermission() = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationBackgroundPermission() = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasFineLocationPermission() = ActivityCompat.checkSelfPermission(
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    viewModel.locationGranted = true
                }
            }
        }
    }

    /*
    * App bar functions
    * */
    fun setAppbarTitle(title: String){
        binding.appBar.appbarTitle.visibility = View.VISIBLE
        binding.appBar.appbarTitle.text = title
        binding.appBar.layoutGreeting.visibility = View.GONE
        binding.appBar.ivMenu.visibility = View.VISIBLE
        binding.appBar.layoutBack.visibility = View.GONE
        binding.appBar.layoutAppBarSearch.visibility = View.VISIBLE
    }


    fun showAppbarBackButton(show: Boolean){
        binding.appBar.appbarTitle.visibility = View.VISIBLE//if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.layoutGreeting.visibility = View.GONE
        binding.appBar.layoutAppBarMenu.visibility = View.VISIBLE
        binding.appBar.ivMenu.visibility = View.GONE
        binding.appBar.layoutBack.visibility = if(show) View.VISIBLE else View.INVISIBLE
        showAppbarSearch(false)
    }


    fun hideAppbarBackAndMenu(hide: Boolean){
        binding.appBar.layoutAppBarMenu.visibility = if(!hide) View.VISIBLE else View.INVISIBLE
    }

    fun showAppbarSearch(show: Boolean){
        binding.appBar.layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.ivSearch.visibility = if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.ivMore.visibility = View.GONE
        binding.appBar.appbarEdit.visibility = View.GONE
    }

    fun showAppbarEdit(show: Boolean){
        binding.appBar.layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.appbarEdit.visibility = if(show) View.VISIBLE else View.GONE
        binding.appBar.appbarSave.visibility = View.GONE
        binding.appBar.ivSearch.visibility = View.GONE
        binding.appBar.ivMore.visibility = View.GONE
    }
    fun showAppbarEditSave(show: Boolean){
        binding.appBar.layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.appbarSave.visibility = if(show) View.VISIBLE else View.GONE
        binding.appBar.appbarEdit.visibility = View.GONE
        binding.appBar.ivSearch.visibility = View.GONE
        binding.appBar.ivMore.visibility = View.GONE
    }


    fun showAppbarMore(show: Boolean){
        binding.appBar.layoutAppBarSearch.visibility = if(show) View.VISIBLE else View.INVISIBLE
        binding.appBar.ivMore.visibility = if(show) View.VISIBLE else View.GONE
        binding.appBar.ivSearch.visibility = View.GONE
        binding.appBar.appbarEdit.visibility = View.GONE
    }

    fun showBottomNavbar(show: Boolean){
        binding.bottomNavigationView.visibility = if(show) View.VISIBLE else View.GONE
    }

    /*
    * Fragment backstack functions
    * */
    fun removeAnyOtherFragVisible(){
        supportFragmentManager.fragments.forEach {
            if (it !is BatchFragment) {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
        }
    }

    fun changeFragment(fragment: Fragment) {
        isclaim_mileage=false
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        ).commit()
    }
    fun changeFragmentHome(fragment: Fragment) {
        setAppbarTitle("Batch No: "+Constants.batch_allotted)
        showBottomNavbar(true)
        showAppbarBackButton(true)
       isclaim_mileage=true
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        ).commit()
    }

    fun addFragmentHome(fragment: Fragment) {
        setAppbarTitle("Batch: "+Constants.batch_allotted)
        supportFragmentManager.beginTransaction().add(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        ).addToBackStack(fragment.javaClass.simpleName).commit()
    }
    fun addFragment(fragment: Fragment) {
        isclaim_mileage=false

        supportFragmentManager.beginTransaction().add(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        ).addToBackStack(fragment.javaClass.simpleName).commit()
    }

    fun clearFragmentBackstack() {
        println("call clearFragmentBackstack")

        removeAnyOtherFragVisible()
        supportFragmentManager.popBackStack(HomeFragment::class.java.simpleName, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        setupAppbar()
        showBottomNavbar(true)
    }

    private fun fragmentBackstackListener(){
        supportFragmentManager.addOnBackStackChangedListener {

            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment != null) {
                val fragName = fragment.javaClass.simpleName

                if(fragName.equals("HomeFragment")){
                    println("call fragmentBackstackListener"+fragName)
                    isclaim_mileage=true
                    showAppbarBackButton(true)
                    showBottomNavbar(true)
                    setAppbarTitle("Batch No: "+Constants.batch_allotted)
                   // setupAppbar()
                    //showBottomNavbar(false)

                } else if(fragName.equals(NewClaimFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.create_new_claim))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(EditClaimFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_claims))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(EditClaimRMFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_claims))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(EditMileageClaimFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_mileage))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(EditMileageClaimRMFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_mileage))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }else if(fragName.equals(SplitClaimFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.split_expenses))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(EditSplitClaimDetailsFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_split_expenses))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(SplitClaimDetailsFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.split_expenses_details))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(ClaimDetailFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.claim_details))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                    showAppbarMore(true)
                }
                else if(fragName.equals(BlankFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.bonhams))
                    setupAppbar()
                    showBottomNavbar(true)
                }
                else if(fragName.equals(NewMileageClaimFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.create_mileage_claim))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }else if(fragName.equals(CapturedImageFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.scan_receipt))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(MileageDetailFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.mileage_details))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                    showAppbarMore(true)
                }
                else if(fragName.equals(GPSTrackingFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.start_gps))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
                else if(fragName.equals(NotificationFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.notifications))
                    showAppbarBackButton(false)
                    showBottomNavbar(true)
                    showAppbarMore(true)
                }
                else if(fragName.equals(MyProfileFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.profile))
                    hideAppbarBackAndMenu(true)
                    showAppbarEdit(true)
                    showBottomNavbar(true)

                }
                else if(fragName.equals(EditProfileFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.edit_profile))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                    showAppbarEditSave(true)
                }
                else if(fragName.equals(ChangePasswordFragment::class.java.simpleName, true)){
                    setAppbarTitle(getString(R.string.change_password))
                    showAppbarBackButton(true)
                    showBottomNavbar(false)
                }
            }
        }
    }

    /*
    * No internet connection check
    * */
    private fun setNoInternetSnackbar(){
        // No Internet Snackbar: Fire
        NoInternetSnackbarFire.Builder(
            binding.navDrawer,
            lifecycle
        ).apply {
            snackbarProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {

                    }
                }

                duration = Snackbar.LENGTH_INDEFINITE // Optional
                noInternetConnectionMessage = "No active Internet connection!" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode!" // Optional
                snackbarActionText = "Settings" // Optional
                showActionToDismiss = false // Optional
                snackbarDismissActionText = "OK" // Optional
            }
        }.build()
    }
    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }
}
