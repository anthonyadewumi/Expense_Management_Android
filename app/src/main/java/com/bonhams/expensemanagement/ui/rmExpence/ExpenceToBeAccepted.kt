package com.bonhams.expensemanagement.ui.rmExpence

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ExpencesListAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.ActivityExpenceAcceptedBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.home.HomeFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.util.*

class ExpenceToBeAccepted : BaseActivity(), RecylerCallback {
    private lateinit var binding: ActivityExpenceAcceptedBinding
    private lateinit var viewModel: ExpenceViewModel
    public lateinit var toBeAcceptedDataList: List<ToBeAcceptedData>
    private var expenseCode: String = ""
    private var taxcodeId: String = ""
    private lateinit var expencesListAdapter: ExpencesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expence_accepted)
        binding.lifecycleOwner = this
        setupViewModel()
        setClickListeners()
      //  setDropdownDataObserver(" ")
        setupView()
        initSearch()

    }

    override fun onResume() {
        super.onResume()
        setDropdownDataObserver("","","")

    }
    private fun setupView(){
    }

    private fun initSearch() {
        binding.edtSearchClaim.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
                setDropdownDataObserver(binding.edtSearchClaim.text!!.trim().toString()," "," ")
                true
            } else {
                false
            }
        }
    }
    private fun showDateRangePicker(){

        val calendar = Calendar.getInstance()
        val calendarStart: Calendar = Calendar.getInstance()
        calendarStart.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH] - 1, calendar[Calendar.DAY_OF_MONTH])

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(calendarStart.timeInMillis)
                .setEnd(calendar.timeInMillis)
                .setValidator(DateValidatorPointBackward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder
                .dateRangePicker()
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(viewModel.dateRange.value as androidx.core.util.Pair<Long, Long>?)
                .build()

        dateRangePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener {
            val startDate: Long = it.first
            val endDate: Long = it.second
            Log.d("Expense to accepted : ", "showDateRangePicker: start Date: ${Utils.getDateInDisplayFormat(startDate)}   end Date: ${Utils.getDateInDisplayFormat(endDate)}")
            setDropdownDataObserver("",Utils.getDateInDisplayFormat(startDate),Utils.getDateInDisplayFormat(endDate))

              viewModel.dateRange.value = it
           viewModel.datePicker.value = Pair(Utils.getDateInServerRequestFormat(startDate), Utils.getDateInServerRequestFormat(endDate))
        })

        dateRangePicker.addOnCancelListener {
            // Respond to cancel button click.
        }

        dateRangePicker.addOnDismissListener {
            // Respond to dismiss events.
        }

        dateRangePicker.show(this.supportFragmentManager, "dateRangePicker");
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ExpenceViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ExpenceViewModel::class.java)
    }
    private fun setClickListeners(){
      //  binding.appbarGreeting.text="rtio"
        binding.appbarGreeting.text = "Hello ${AppPreferences.firstName}!"

        binding.layoutAppBarSearch.setOnClickListener {
            if(binding.tilSearchClaim.isVisible){
                binding.tilSearchClaim.visibility=View.GONE
                binding.edtSearchClaim.setText("")
                setDropdownDataObserver("","","")

            }else{
                binding.tilSearchClaim.visibility=View.VISIBLE

            }
        }
        binding.ivCalendar.setOnClickListener(View.OnClickListener {
           // val fp = Intent(this, RequestClaimDetails::class.java)
           // startActivity(fp)
            showDateRangePicker()
        })
        binding.layoutBack.setOnClickListener {
                finish()
        }

    }
    private fun setupExpenceRecyclerView(toBeAcceptedDataList: List<ToBeAcceptedData>){

        val linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rmExpenceList.layoutManager = linearLayoutManager
        expencesListAdapter = ExpencesListAdapter(toBeAcceptedDataList,this,this)
        binding.rmExpenceList.adapter = expencesListAdapter
    }
    private fun setDropdownDataObserver(serchkey:String,fromdate:String,todate:String) {
       val data= JsonObject()
        data.addProperty("numberOfItems",20)
        data.addProperty("page",1)
       // data.addProperty("from",fromdate)
       // data.addProperty("to",todate)
        data.addProperty("searchKey",serchkey)
        viewModel.getRequestExpences(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                setupExpenceRecyclerView(response.tobeaccepteddata)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun initializeSpinnerData(dropdownResponse: DropdownResponse){



    }

    override fun callback(action: String, data: Any, postion: Int) {
       if(action == "batch"){
         val mData=data as ToBeAcceptedData
           println("batch_allotted :${mData.batch_allotted}")
           showBatchBottomSheet(mData.batch_allotted,mData.employeeId.toString())
        }
    }
    private fun showBatchBottomSheet(batch_number :Int,requestId:String){
        this?.let {
            val dialog = BottomSheetDialog(this!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.request_batch_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionPending = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val bottomOptionApproved = view.findViewById<TextView>(R.id.bottomOptionThree)
            val bottomOptionRejected = view.findViewById<TextView>(R.id.bottomOptionfour)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionPending.setOnClickListener {
                showDeleteBatchAlert(batch_number)
                dialog.dismiss()
                //  viewModel.statusPicker.value = Constants.STATUS_PENDING
            }
            bottomOptionApproved.setOnClickListener {
                acceptRequest(requestId,batch_number)
                dialog.dismiss()
                // viewModel.statusPicker.value = Constants.STATUS_APPROVED
            }
            bottomOptionRejected.setOnClickListener {
                showRejectAlert(requestId,batch_number)
                dialog.dismiss()
                //viewModel.statusPicker.value = Constants.STATUS_REJECTED
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    private fun showRejectAlert(requestId:String,batch_number :Int) {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reject_alert_dialog)
        val input = dialog.findViewById(R.id.edtDescription) as EditText
        val yesBtn = dialog.findViewById(R.id.btnSubmit) as Button
        val noBtn = dialog.findViewById(R.id.btnCancel) as TextView

        yesBtn.setOnClickListener {

            if (input.text.isNullOrEmpty()) {
                input.error = "Please enter the reason"
            } else {
                input.error = null
                dialog.dismiss()
                println("reason :" + input.text)
                acceptRejectObserver(2, input.text.toString(),requestId,batch_number.toString())
            }

            //   logoutUser()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
           // finish()
        }
        dialog.show()
    }

    private fun acceptRequest(requestId:String,batch_number: Int) {
        this?.let { activity ->
            val dialog = Dialog(activity)
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
            title.text = resources.getString(R.string.accept_claim)
            body.text = resources.getString(R.string.are_you_sure_you_want_to_accept_claim)
            yesBtn.text = "Yes"
            noBtn.text = "No"

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    acceptRejectObserver(1, "",requestId,batch_number.toString())

                else
                    Toast.makeText(
                        activity,
                        getString(R.string.check_internet_msg),
                        Toast.LENGTH_SHORT
                    ).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
    private fun showDeleteBatchAlert(batch_number :Int){
        this?.let { activity ->
            val dialog = Dialog(activity)
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
            title.text = resources.getString(R.string.delete_batch)
            body.text = resources.getString(R.string.are_you_sure_you_want_to_delete_batch)
            yesBtn.text = resources.getString(R.string.delete)
            noBtn.text = resources.getString(R.string.cancel)

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    deleteBatchObserver(batch_number)
                else
                    Toast.makeText(activity, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
    private fun deleteBatchObserver(batch_number :Int) {
        val data= JsonObject()
        data.addProperty("batch_number",batch_number)
        viewModel.deleteBatch(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                               // getBatchDataObserver()
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
    private fun submitBatchObserver(batch_number :Int) {
        val data= JsonObject()
        data.addProperty("batch_number",batch_number)
        viewModel.submitBatch(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                               // getBatchDataObserver()
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
    private fun acceptRejectObserver(accept_reject: Int, reson: String,requestId:String,batch_allotted:String) {
        val data = JsonObject()
        //val jsonIdArray = JsonArray()
       // jsonIdArray.add(requestId.toInt())
       // data.add("claim_ids", jsonIdArray)
        data.addProperty("action", accept_reject)
        data.addProperty("user_id",requestId)
        data.addProperty("batch_number",batch_allotted)
        if (accept_reject == 2) {
            data.addProperty("reason", reson)

        }
        data.addProperty("role", AppPreferences.userType)//////////////////////////
        viewModel.acceptRejectBatch(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 ->
                            Toast.makeText(this, it1, Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

}