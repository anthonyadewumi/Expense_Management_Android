package com.bonhams.expensemanagement.ui.myProfile.editProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentMyProfileBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModelFactory
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class EditProfileFragment() : Fragment() {

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
        })
    }

    private fun setupView(){
        Glide.with(this)
            .load(AppPreferences.profilePic)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_default_user_theme)
                    .error(R.drawable.ic_default_user_theme)
            )
            .placeholder(R.drawable.ic_default_user_theme)
            .error(R.drawable.ic_default_user_theme)
            .circleCrop()
            .into(binding.ivProfilePic)

        binding.tvEmpId.text = AppPreferences.employeeId
        binding.tvFirstName.text = AppPreferences.firstName
        binding.tvLastName.text = AppPreferences.lastName
        binding.tvEmail.text = AppPreferences.email
        binding.tvPhoneNumber.text = AppPreferences.phoneNumber

        /*binding.tvCompanyName.text = AppPreferences.userId
        binding.tvDepartmentName.text = AppPreferences.userId
        binding.tvCarType.text = AppPreferences.userId
        binding.tvCountry.text = AppPreferences.userId
        binding.tvMileageType.text = AppPreferences.userId
        binding.tvDefaultApprover.text = AppPreferences.userId*/
    }
}