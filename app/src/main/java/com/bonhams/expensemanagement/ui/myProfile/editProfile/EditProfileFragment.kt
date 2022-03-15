package com.bonhams.expensemanagement.ui.myProfile.editProfile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.model.Currency
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.EditProfileResponse
import com.bonhams.expensemanagement.data.services.responses.MyProfileResponse
import com.bonhams.expensemanagement.databinding.FragmentEditProfileBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModel
import com.bonhams.expensemanagement.ui.myProfile.MyProfileViewModelFactory
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.MutableList
import kotlin.collections.set


class EditProfileFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentEditProfileBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: MyProfileViewModel
    private lateinit var progDialog: ProgressDialog
    private var compnyId: Int = 0
    private var carId: Int = 0

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
        binding.ivProfilePic.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })
    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(requireActivity(),
            MyProfileViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MyProfileViewModel::class.java)
        progDialog= ProgressDialog(requireContext())
        progDialog.setTitle("Uploading ...")
        mainViewModel.appbarSaveClick?.observe(viewLifecycleOwner, {
            if(validateEditDetails()){
                if(mainViewModel.isEdit){
                    mainViewModel.isEdit=false
                editMyProfileObserver(binding.tvFirstName.text.toString(),binding.tvLastName.text.toString(),binding.tvEmail.text.toString(),binding.tvPhoneNumber.text.toString())
            }
                }
        })
        setMyProfileObserver()
    }


    private fun setupView(){
        setDropdownDataObserver()
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



    private fun editMyProfileObserver(fname:String, lname:String, email:String,phone:String) {
        val data = JsonObject()
        data.addProperty("fname", fname)
        data.addProperty("lname", lname)
       // data.addProperty("email", email)
        data.addProperty("contact_no", phone)
        data.addProperty("company_id", compnyId)
        data.addProperty("car_type_id", carId)

        viewModel.editProfile(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                if(response.message?.contains("Cannot fetch") != true)
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

    private fun setDropdownDataObserver() {
        viewModel.getDropDownData().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                initializeSpinnerData(response)
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

    private fun initializeSpinnerData(dropdownResponse: DropdownResponse){



        // viewModel.expenseTypeList = dropdownResponse.expenseType
        viewModel.currencyList  = dropdownResponse.currencyType as MutableList<Currency>
        viewModel.carTypeList  = dropdownResponse.carType
        viewModel.companyList  = dropdownResponse.companyList

        setupSpinners()
    }

    private fun setupSpinners(){
        // Company List Adapter
        val companyAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_spinner, viewModel.companyList)
        binding.spnCompanyNumber.adapter = companyAdapter
        var compnypostion=0
        viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.companyId == element.id){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompanyNumber.setSelection(compnypostion)

        binding.spnCompanyNumber.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                compnyId=viewModel.companyList[position].id.toInt()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
        // Car List Adapter
        val carAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_spinner, viewModel.carTypeList)
        binding.spnCarType.adapter = carAdapter
        var carpostion=0
        viewModel.carTypeList.forEachIndexed { index, element ->

            if(AppPreferences.carId == element.id){
                carpostion=index
                return@forEachIndexed
            }
        }
        binding.spnCarType.setSelection(carpostion)

        binding.spnCarType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                carId=viewModel.carTypeList[position].id.toInt()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

    }

    private fun setEditResponse(response: EditProfileResponse) {
        viewModel.setEditResponse(response)
        viewModel.isProfileRefresh?.value=true

        binding.tvEmpId.text = response.profileDetail[0]?.employID

        binding.tvFirstName.setText(response.profileDetail[0]?.fname)
        binding.tvLastName.setText(response.profileDetail[0]?.lname)
        binding.tvEmail.setText(response.profileDetail[0]?.email)
        binding.tvPhoneNumber.setText(response.profileDetail[0]?.contactNo)

        binding.tvCompanyName.text = response.profileDetail[0]?.companyName
        binding.tvDepartmentName.text = response.profileDetail[0]?.departmentName
        binding.tvCarType.text = response.profileDetail[0]?.carType
        AppPreferences.carType= response.profileDetail[0]?.carType.toString()
        AppPreferences.company= response.profileDetail[0]?.companyName.toString()
        binding.tvCountry.text = response.profileDetail[0]?.countryCode
        binding.tvMileageType.text = response.profileDetail[0]?.mileageType
        binding.tvDefaultApprover.text = response.profileDetail[0]?.approver
        AppPreferences.companyId= compnyId.toString()
        AppPreferences.carId= carId.toString()
        try {
            (contextActivity as? MainActivity)?.backButtonPressed()
        } catch (e: Exception) {
        }
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
    private fun showBottomSheet(){
        contextActivity?.let {
            val dialog = BottomSheetDialog(contextActivity!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.item_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionOne = view.findViewById<TextView>(R.id.bottomOptionOne)
//            val dividerOne = view.findViewById<View>(R.id.dividerOne)
            val bottomOptionTwo = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val dividerTwo = view.findViewById<View>(R.id.dividerTwo)
            val bottomOptionThree = view.findViewById<TextView>(R.id.bottomOptionThree)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionOne.text = resources.getString(R.string.upload_file)
            bottomOptionTwo.text = resources.getString(R.string.take_photo)
            dividerTwo.visibility = View.GONE
            bottomOptionThree.visibility = View.GONE

            bottomOptionOne.setOnClickListener {
                dialog.dismiss()
                choosePhotoFromGallery()
            }
            bottomOptionTwo.setOnClickListener {
                dialog.dismiss()
                takePhotoFromCamera()
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    private fun choosePhotoFromGallery() {
        contextActivity?. let {
            val intent = Lassi(contextActivity!!)
                .with(LassiOption.GALLERY) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
                .setCompressionRation(20) // compress image for single item selection (can be 0 to 100)
                .setMinFileSize(100) // Restrict by minimum file size
                .setMaxFileSize(1024) //  Restrict by maximum file size
                .disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
                .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 100)
        }
    }

    private fun takePhotoFromCamera(){
        contextActivity?. let {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ImagePicker.with(this)
                    .crop()
                    .cameraOnly()//Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }else {


                val intent = Lassi(contextActivity!!)
                .with(LassiOption.CAMERA) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
                .setCompressionRation(20) // compress image for single item selection (can be 0 to 100)
                .setMinFileSize(100) // Restrict by minimum file size
                .setMaxFileSize(1024) //  Restrict by maximum file size
                .disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
               .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 101)
            }
        }
    }
    private fun uploadProfileImage(){
          val requestBody: RequestBody
          val body: MultipartBody.Part
          val mapRequestBody = LinkedHashMap<String, RequestBody>()
          val arrBody: MutableList<MultipartBody.Part> = ArrayList()
          val file=File( viewModel.attachmentsList.get(0))
          requestBody = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file)
          mapRequestBody["file\"; filename=\"$file"] = requestBody
          mapRequestBody["test"] =
              RequestBody.create(okhttp3.MediaType.parse("text/plain"), "gogogogogogogog")
          body = MultipartBody.Part.createFormData("image", file.name, requestBody)
          arrBody.add(body)
        progDialog.show()

        viewModel.imageProfile(arrBody).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progDialog.dismiss()

                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                                AppPreferences.profilePic = response.image_url ?: ""

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
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        progDialog.dismiss()

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                100 -> {
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d(TAG, "onActivityResult: ${selectedMedia[0].path}")
                    if(selectedMedia.size > 0) {
                        viewModel.attachmentsList.add(selectedMedia[0].path!!)
                       // refreshAttachments()
                        Log.d(TAG, "onActivityResult:  attachmentsList: ${viewModel.attachmentsList.size}")
                    }
                    uploadProfileImage()

                }
                101 -> {
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d(TAG, "onActivityResult: ${selectedMedia[0].path}")
                 val file=    File(selectedMedia[0].path)
                    Log.d(TAG, "onActivityResult file : $file")
                    if(selectedMedia.size > 0) {
                        viewModel.attachmentsList.add(selectedMedia[0].path!!)
                       // refreshAttachments()
                        Log.d(TAG, "onActivityResult:  attachmentsList: ${viewModel.attachmentsList.size}")
                    }
                    uploadProfileImage()
                }else->{
                if (resultCode == Activity.RESULT_OK){
                    val uri: Uri = data?.data!!
                    viewModel.attachmentsList.add(uri.path)
                    uploadProfileImage()
                }

                }

            }
        }else{

        }
    }
}