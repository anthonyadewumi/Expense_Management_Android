package com.bonhams.expensemanagement.ui.claims

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ClaimsAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimDetail
import com.bonhams.expensemanagement.data.services.responses.ClaimsResponse
import com.bonhams.expensemanagement.databinding.FragmentClaimsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailFragment
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.home.HomeViewModel
import com.bonhams.expensemanagement.ui.home.HomeViewModelFactory
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Status
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class ClaimsFragment : Fragment(), ClaimsAdapter.OnClaimClickListener {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var adapter: ClaimsAdapter? = null
    /*private var tilSearchClaim: TextInputLayout? = null
    private var edtSearchClaim: TextInputEditText? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mNoResult: TextView? = null
    private var mProgressBar: ProgressBar? = null*/
    private lateinit var viewModel: ClaimsViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentClaimsBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claims, container, false)
        val view = binding.root
        binding.lifecycleOwner = this

        contextActivity = activity as? BaseActivity

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

        homeViewModel.datePicker.observe(viewLifecycleOwner, {
            Log.d(TAG, "setupViewModel: datePicker: $it")
        })

        homeViewModel.statusPicker.observe(viewLifecycleOwner, {
            Log.d(TAG, "setupViewModel: statusPicker: $it")
        })

        viewModel.responseClaimsList?.observe(requireActivity(), {
            setupRecyclerView()
        })

        mainViewModel.appbarSearchClick?.observe(viewLifecycleOwner, {
            Log.d(TAG, "setupViewModel: appbarSearchClick: $it")
            if(it){
                binding.tilSearchClaim.visibility = View.VISIBLE
            }
            else{
                binding.tilSearchClaim.visibility = View.GONE
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

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.responseClaimsList?.value?.claimsList?.let {
            binding.recyclerView.visibility = View.VISIBLE
            if (adapter == null) {
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding.recyclerView.layoutManager = linearLayoutManager
                adapter = ClaimsAdapter(viewModel.responseClaimsList?.value?.claimsList, this)
//                adapter?.setResponse(viewModel.responseClaimsList?.value?.claimsList)
                binding.recyclerView.adapter = adapter
            }else{
                adapter?.setResponse(viewModel.responseClaimsList?.value?.claimsList)
            }
        } ?: kotlin.run {
            binding.mNoResult.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    private fun getMileageListObserver(claimsRequest: ClaimsRequest){
        viewModel.getClaimsList(claimsRequest).observe(viewLifecycleOwner, {
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
                        binding.mProgressBars.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setClaimsResponse(claimsResponse: ClaimsResponse) {
        viewModel.responseClaimsList?.value = claimsResponse
        binding.mProgressBars.visibility = View.GONE
    }

    override fun onClaimItemClicked(claim: ClaimDetail?, position: Int) {
        Log.d(TAG, "onClaimItemClicked: $position")
        val fragment = ClaimDetailFragment()
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }

    override fun onClaimCreateCopyClicked(claim: ClaimDetail?, position: Int) {
        Log.d(TAG, "onClaimCreateCopyClicked: $position")
        val fragment = NewClaimFragment()
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }
}