package com.bonhams.expensemanagement.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.NavDrawerAdapter
import com.bonhams.expensemanagement.data.model.NavDrawerItem
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.home.HomeFragment
import com.bonhams.expensemanagement.ui.myProfile.MyProfileFragment
import com.bonhams.expensemanagement.ui.notification.NotificationFragment
import com.bonhams.expensemanagement.utils.ClickListener
import com.bonhams.expensemanagement.utils.RecyclerTouchListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*


class MainActivity : BaseActivity() {

    private lateinit var navDrawerAdapter: NavDrawerAdapter

    private var navDrawerItems = arrayListOf(
        NavDrawerItem(R.drawable.ic_home, "Expense", -1),
        NavDrawerItem(R.drawable.ic_nav_expense_plus, "Manually Create", 1),
        NavDrawerItem(R.drawable.ic_nav_scan, "Scan Receipt", 2),
        NavDrawerItem(R.drawable.ic_home, "Mileage", -1),
        NavDrawerItem(R.drawable.ic_nav_car, "Manually Create", 4),
        NavDrawerItem(R.drawable.ic_nav_gps, "Start GPS", 5),
        NavDrawerItem(R.drawable.ic_profile, "Others", -1),
        NavDrawerItem(R.drawable.ic_nav_my_profile, "My Account", 7),
        NavDrawerItem(R.drawable.ic_nav_logout, "Logout", 8),
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setupNavDrawer()
        setupClickListeners()

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
                    val fragment = HomeFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_camera -> {
                    val fragment = MyProfileFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_notifications -> {
                    val fragment = NotificationFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_my_profile -> {
                    val fragment = MyProfileFragment()
                    changeFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun setupClickListeners(){
        ivMenu.setOnClickListener(View.OnClickListener {
            if (!navDrawer.isDrawerOpen(GravityCompat.START)) {
                navDrawer.openDrawer(GravityCompat.START)
            }
        })
    }

    private fun setupNavDrawer(){
        // Setup Recyclerview's Layout
        navDrawerRv.layoutManager = LinearLayoutManager(this)
        navDrawerRv.setHasFixedSize(true)

        // Add Item Touch Listener
        navDrawerRv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        val fragment = HomeFragment()
                        changeFragment(fragment)
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4) {
                    updateAdapter(position)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    navDrawer.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        // Close the soft keyboard when you open or close the Drawer
        /*val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        navDrawer.addDrawerListener(toggle)

        toggle.syncState()*/

        updateAdapter(0)
    }

    private fun updateAdapter(highlightItemPos: Int) {
        navDrawerAdapter = NavDrawerAdapter(navDrawerItems, highlightItemPos)
        navDrawerRv.adapter = navDrawerAdapter
        navDrawerAdapter.notifyDataSetChanged()
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
}