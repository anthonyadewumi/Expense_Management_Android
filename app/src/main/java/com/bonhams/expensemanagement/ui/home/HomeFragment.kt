package com.bonhams.expensemanagement.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.HomeViewPagerAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class HomeFragment : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: HomeViewModel

    private var adapter: HomeViewPagerAdapter? = null
    private var ivCalendar: ImageView? = null
    private var ivFilter: ImageView? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val tabsArray = arrayOf(
        "Claims",
        "Mileage Expenses",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        contextActivity = activity as? BaseActivity
        ivCalendar = view.findViewById(R.id.ivCalendar)
        ivFilter = view.findViewById(R.id.ivFilter)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.viewPager)


        setupViewModel()
        setupViewPager()
        setClickListeners()

        return view
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity(),
            HomeViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)

        viewModel.datePicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: message: $it")
        })
    }

    private fun setupViewPager() {
        adapter = HomeViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()
    }

    private fun setClickListeners(){
        ivCalendar?.setOnClickListener(View.OnClickListener {
            showDateRangePicker()
        })

        ivFilter?.setOnClickListener(View.OnClickListener {
            showStatusFilterBottomSheet()
        })
    }

    private fun showDateRangePicker(){
        Log.d(TAG, "showDateRangePicker: called")

        val calendar = Calendar.getInstance()
        val calendarStart: Calendar = Calendar.getInstance()
//        val calendarEnd: Calendar = Calendar.getInstance()

        calendarStart.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH] - 1, calendar[Calendar.DAY_OF_MONTH])
//        calendarEnd.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(calendarStart.timeInMillis)
                .setEnd(calendar.timeInMillis)
                .setValidator(DateValidatorPointBackward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder
                .dateRangePicker()
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setInputMode(INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener {
            val startDate: Long = it.first
            val endDate: Long = it.second
            Log.d(TAG, "showDateRangePicker: start Date: ${Utils.getDateInDisplayFormat(startDate)}   end Date: ${Utils.getDateInDisplayFormat(endDate)}")
            viewModel.datePicker.value = Pair(Utils.getDateInDisplayFormat(startDate), Utils.getDateInDisplayFormat(endDate))
        })

        dateRangePicker.addOnCancelListener {
            // Respond to cancel button click.
        }

        dateRangePicker.addOnDismissListener {
            // Respond to dismiss events.
        }

        dateRangePicker.show(childFragmentManager, "dateRangePicker");
    }

    private fun showStatusFilterBottomSheet(){
        contextActivity?.let {
            val dialog = BottomSheetDialog(contextActivity!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.item_bottom_sheet_status_filter, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionPending = view.findViewById<TextView>(R.id.bottomOptionPending)
            val bottomOptionApproved = view.findViewById<TextView>(R.id.bottomOptionApproved)
            val bottomOptionRejected = view.findViewById<TextView>(R.id.bottomOptionRejected)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionPending.setOnClickListener {
                dialog.dismiss()
                viewModel.statusPicker.value = Constants.STATUS_PENDING
            }
            bottomOptionApproved.setOnClickListener {
                dialog.dismiss()
                viewModel.statusPicker.value = Constants.STATUS_APPROVED
            }
            bottomOptionRejected.setOnClickListener {
                dialog.dismiss()
                viewModel.statusPicker.value = Constants.STATUS_REJECTED
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}