package com.bonhams.expensemanagement.ui.myProfile.editProfile

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
import com.bonhams.expensemanagement.data.services.responses.EditProfileResponse
import com.bonhams.expensemanagement.data.services.responses.MyProfileResponse
import com.bonhams.expensemanagement.databinding.FragmentEditProfileBinding
import com.bonhams.expensemanagement.databinding.FragmentMyProfileBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModelFactory
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class EditProfileFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentEditProfileBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
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

        mainViewModel.appbarSaveClick?.observe(viewLifecycleOwner, {
            if(validateEditDetails()){
                editMyProfileObserver(binding.tvFirstName.text.toString(),binding.tvLastName.text.toString(),binding.tvEmail.text.toString())
            }
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
        binding.tvFirstName.setText(AppPreferences.firstName)
        binding.tvLastName.setText(AppPreferences.lastName)
        binding.tvEmail.setText(AppPreferences.email)
        binding.tvPhoneNumber.setText(AppPreferences.phoneNumber)



        /*binding.tvCompanyName.text = AppPreferences.userId
        binding.tvDepartmentName.text = AppPreferences.userId
        binding.tvCarType.text = AppPreferences.userId
        binding.tvCountry.text = AppPreferences.userId
        binding.tvMileageType.text = AppPreferences.userId
        binding.tvDefaultApprover.text = AppPreferences.userId*/
    }

    private fun validateEditDetails(): Boolean {
        if(binding.tvFirstName.text.isNullOrEmpty()){
            binding.tvFirstName.error = "Please enter first name"
            return false
        }else if(binding.tvLastName.text.isNullOrEmpty()){
            binding.tvLastName.error = "Please enter last name"

            return false

        }else if(binding.tvEmail.text.isNullOrEmpty()){
            binding.tvEmail.error = "Please enter email"

            return false

        }
        return true

    }
    private fun editMyProfileObserver(fname:String, lname:String, email:String) {
        val data = JsonObject()
        data.addProperty("fname", fname)
        data.addProperty("lname", lname)
        data.addProperty("email", email)

        viewModel.editProfile(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()

                                setEditResponse(response)
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
    private fun setEditResponse(response: EditProfileResponse) {
        viewModel.setEditResponse(response)

        binding.tvEmpId.text = response.profileDetail[0]?.employID

        binding.tvFirstName.setText(response.profileDetail[0]?.fname)
        binding.tvLastName.setText(response.profileDetail[0]?.lname)
        binding.tvEmail.setText(response.profileDetail[0]?.email)
        binding.tvPhoneNumber.setText(response.profileDetail[0]?.contactNo)

        binding.tvCompanyName.text = response.profileDetail[0]?.companyName
        binding.tvDepartmentName.text = response.profileDetail[0]?.departmentName
        binding.tvCarType.text = response.profileDetail[0]?.carType
        binding.tvCountry.text = response.profileDetail[0]?.countryCode
        binding.tvMileageType.text = response.profileDetail[0]?.mileageType
        binding.tvDefaultApprover.text = response.profileDetail[0]?.approver
    }


}