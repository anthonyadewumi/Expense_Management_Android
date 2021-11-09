package com.bonhams.expensemanagement.ui.rmExpence

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
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
import com.bonhams.expensemanagement.utils.Status
import com.bonhams.expensemanagement.utils.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.gson.JsonObject
import java.util.*

class ExpenceToBeAccepted : BaseActivity() {
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
        expencesListAdapter = ExpencesListAdapter(toBeAcceptedDataList,this)
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



}