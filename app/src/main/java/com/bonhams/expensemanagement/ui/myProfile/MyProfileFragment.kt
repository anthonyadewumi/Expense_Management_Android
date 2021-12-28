package com.bonhams.expensemanagement.ui.myProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.MyProfileResponse
import com.bonhams.expensemanagement.databinding.FragmentMyProfileBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment
import com.bonhams.expensemanagement.ui.myProfile.editProfile.EditProfileFragment
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class MyProfileFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentMyProfileBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setClickListeners()
        setupView()
        setMyProfileObserver()

        return view
    }

    private fun setClickListeners(){
        binding.layoutChangePassword.setOnClickListener(View.OnClickListener {
            val fragment = ChangePasswordFragment()
            (contextActivity as? MainActivity)?.addFragment(fragment)
        })
    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(requireActivity(),
            MyProfileViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MyProfileViewModel::class.java)

        mainViewModel.appbarEditClick?.observe(viewLifecycleOwner, {
            Log.d(TAG, "setupViewModel: Clicked!!")
            val fragment = EditProfileFragment()
            (contextActivity as? MainActivity)?.addFragment(fragment)
        })
        mainViewModel.appbarbackClick?.observe(viewLifecycleOwner, {
            setupView()

        })
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }
    private fun setupView(){
        Glide.with(this)
            .load(AppPreferences.profilePic)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_default_user_theme)
                    .error(R.drawable.ic_default_user_theme)
            )
            .placeholder(R.drawable.ic_default_user_theme)
            .error(R.drawable.ic_default_user_theme)
            .circleCrop()
            .into(binding.ivProfilePic)

        binding.tvEmpId.isEnabled=false
        binding.tvFirstName.isEnabled=false
        binding.tvLastName.isEnabled=false
        binding.tvEmail.isEnabled=false
        binding.tvPhoneNumber.isEnabled=false
        binding.tvLedgerID.isEnabled=false


        binding.tvEmpId.text = AppPreferences.employeeId
        binding.tvFirstName.setText(AppPreferences.firstName)
        binding.tvCompanyName.setText(AppPreferences.company)
        binding.tvCarType.setText(AppPreferences.carType)
        binding.tvLastName.setText(AppPreferences.lastName)
        binding.tvEmail.setText(AppPreferences.email)
        binding.tvLedgerID.setText(AppPreferences.ledgerId)



    }

    private fun setMyProfileObserver() {
        viewModel.profileDetail().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setResponse(response: MyProfileResponse) {
        viewModel.setResponse(response)

        binding.tvEmpId.text = response.profileDetail?.employID

        binding.tvFirstName.setText(response.profileDetail?.fname)
        binding.tvLastName.setText(response.profileDetail?.lname)
        binding.tvEmail.setText(response.profileDetail?.email)
        binding.tvPhoneNumber.setText(response.profileDetail?.contactNo)

        binding.tvCompanyName.text = response.profileDetail?.companyName
        binding.tvDepartmentName.text = response.profileDetail?.departmentName
        binding.tvCarType.text = response.profileDetail?.carType
        binding.tvCountry.text = response.profileDetail?.countryCode
        binding.tvMileageType.text = response.profileDetail?.mileageType
        binding.tvDefaultApprover.text = response.profileDetail?.approver
    }
}