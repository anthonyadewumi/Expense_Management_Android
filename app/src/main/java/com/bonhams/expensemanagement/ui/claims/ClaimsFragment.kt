package com.bonhams.expensemanagement.ui.claims

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
import com.bonhams.expensemanagement.adapters.ClaimsAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimDetail
import com.bonhams.expensemanagement.data.services.responses.ClaimsResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailFragment
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.home.HomeViewModel
import com.bonhams.expensemanagement.ui.home.HomeViewModelFactory
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

private const val TAG = "NotificationFragment"

class ClaimsFragment() : Fragment(), ClaimsAdapter.OnClaimClickListener {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var adapter: ClaimsAdapter? = null
    private var tilSearchClaim: TextInputLayout? = null
    private var edtSearchClaim: TextInputEditText? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mNoResult: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var viewModel: ClaimsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_claims, container, false)
        contextActivity = activity as? BaseActivity
        tilSearchClaim = view.findViewById(R.id.tilSearchClaim)
        edtSearchClaim = view.findViewById(R.id.edtSearchClaim)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        recyclerView = view.findViewById(R.id.recyclerView)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        mNoResult = view.findViewById(R.id.mNoResult)

        setupViewModel()
        setViews()

        return view
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ClaimsViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ClaimsViewModel::class.java)

        homeViewModel = ViewModelProvider(requireActivity(),
            HomeViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)

        homeViewModel.datePicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: datePicker: $it")
        })

        homeViewModel.statusPicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: statusPicker: $it")
        })

        viewModel.responseClaimsList?.observe(requireActivity(), Observer {
            setupRecyclerView()
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

        contextActivity?.let {
            if(NoInternetUtils.isConnectedToInternet(it))
                getMileageListObserver(viewModel.getClaimsRequest(1, "test"))
            else
                Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setViews() {
        viewModel.responseClaimsList?.value?.claimsList?.let {

        } ?: run {

        }
    }


    private fun setupRecyclerView() {

        swipeRefresh?.setOnRefreshListener {
            swipeRefresh?.isRefreshing = false
        }

        viewModel.responseClaimsList?.value?.claimsList?.let {
            recyclerView?.visibility = View.VISIBLE
            if (adapter == null) {
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                recyclerView?.layoutManager = linearLayoutManager
                adapter = ClaimsAdapter(viewModel.responseClaimsList?.value?.claimsList, this)
                recyclerView?.adapter = adapter
            }else{
                adapter?.listClaims = viewModel.responseClaimsList?.value?.claimsList
                adapter?.notifyDataSetChanged()
            }
        } ?: kotlin.run {
            mNoResult?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
    }

    private fun getMileageListObserver(claimsRequest: ClaimsRequest){
        viewModel.getClaimsList(claimsRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                setClaimsResponse(response)
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

    private fun setClaimsResponse(claimsResponse: ClaimsResponse) {
        viewModel.responseClaimsList?.value = claimsResponse
        mProgressBar?.visibility = View.GONE
    }

    override fun onClaimItemClicked(claim: ClaimDetail, position: Int) {
        Log.d(TAG, "onClaimItemClicked: $position")
        val fragment = ClaimDetailFragment()
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }

    override fun onClaimCreateCopyClicked(claim: ClaimDetail, position: Int) {
        Log.d(TAG, "onClaimCreateCopyClicked: $position")
        val fragment = NewClaimFragment()
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }
}