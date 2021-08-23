package com.bonhams.expensemanagement.ui.mileageExpenses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.MileageAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.responses.MileageListResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.home.HomeViewModel
import com.bonhams.expensemanagement.ui.home.HomeViewModelFactory
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class MileageExpensesFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var tilSearchClaim: TextInputLayout? = null
    private var edtSearchClaim: TextInputEditText? = null
    private var adapter: MileageAdapter? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var mNotifyRecycler: RecyclerView? = null
    private var mNoResult: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var viewModel: MileageExpensesViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        contextActivity = activity as? BaseActivity
        tilSearchClaim = view.findViewById(R.id.tilSearchClaim)
        edtSearchClaim = view.findViewById(R.id.edtSearchClaim)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        mNotifyRecycler = view.findViewById(R.id.mNotifyRecycler)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        mNoResult = view.findViewById(R.id.mNoResult)

        setupViewModel()
        setViews()

        return view
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            MileageExpensesViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MileageExpensesViewModel::class.java)

        homeViewModel = ViewModelProvider(requireActivity(),
            HomeViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)

        homeViewModel.datePicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: datePicker: $it")
        })

        homeViewModel.statusPicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: statusPicker: $it")
        })

        mainViewModel.appbarSearchClick?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: appbarSearchClick: $it")
            if(it){
                tilSearchClaim?.visibility = View.VISIBLE
            }
            else{
                tilSearchClaim?.visibility = View.GONE
            }
        })

        viewModel.responseMileageList?.observe(requireActivity(), Observer {
            Log.d(TAG, "setupViewModel: ${viewModel.responseMileageList?.value?.mileageList?.size}")
            setupRecyclerView()
        })

        contextActivity?.let {
            if(NoInternetUtils.isConnectedToInternet(it))
                getMileageListObserver(viewModel.getMileageListRequest(1, ""))
            else
                Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setViews() {
        viewModel.responseMileageList?.value?.mileageList?.let {

        } ?: run {

        }
    }


    private fun setupRecyclerView() {
        swipeRefresh?.setOnRefreshListener {
            swipeRefresh?.isRefreshing = false
        }

        viewModel.responseMileageList?.value?.mileageList?.let {
            mNotifyRecycler?.visibility = View.VISIBLE
            if (adapter == null) {
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                mNotifyRecycler?.layoutManager = linearLayoutManager
                adapter =
                    MileageAdapter(viewModel.responseMileageList?.value?.mileageList)
                mNotifyRecycler?.adapter = adapter
            }else{
                adapter?.listMileage = viewModel.responseMileageList?.value?.mileageList
                adapter?.notifyDataSetChanged()
            }
        } ?: kotlin.run {
            mNoResult?.visibility = View.VISIBLE
            mNotifyRecycler?.visibility = View.GONE
        }
    }

    private fun getMileageListObserver(mileageExpenseRequest: MileageExpenseRequest){
        viewModel.getMileageExpensesList(mileageExpenseRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                setMileageResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                        mProgressBar?.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        mProgressBar?.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setMileageResponse(mileageListResponse: MileageListResponse) {
        viewModel.responseMileageList?.value = mileageListResponse
        mProgressBar?.visibility = View.GONE
    }
}