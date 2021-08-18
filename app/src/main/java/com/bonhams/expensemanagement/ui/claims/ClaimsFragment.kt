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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ClaimsAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimsResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

private const val TAG = "NotificationFragment"

class ClaimsFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var adapter: ClaimsAdapter? = null
    private var mNotifyRecycler: RecyclerView? = null
    private var mNoResult: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var viewModel: ClaimsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        contextActivity = activity as? BaseActivity
        mNotifyRecycler = view.findViewById(R.id.mNotifyRecycler)
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

        viewModel.responseClaimsList?.observe(requireActivity(), Observer {
            Log.d(TAG, "setupViewModel: ${viewModel.responseClaimsList?.value?.claimsList?.size}")
            setupRecyclerView()
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
        viewModel.responseClaimsList?.value?.claimsList?.let {
            mNotifyRecycler?.visibility = View.VISIBLE
            if (adapter == null) {
                val linearLayoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                mNotifyRecycler?.layoutManager = linearLayoutManager
                adapter =
                    ClaimsAdapter(viewModel.responseClaimsList?.value?.claimsList)
                mNotifyRecycler?.adapter = adapter
            }else{
                adapter?.listClaims = viewModel.responseClaimsList?.value?.claimsList
                adapter?.notifyDataSetChanged()
            }
        } ?: kotlin.run {
            mNoResult?.visibility = View.VISIBLE
            mNotifyRecycler?.visibility = View.GONE
        }
    }

    private fun getMileageListObserver(claimsRequest: ClaimsRequest){
        viewModel.getClaimsList(claimsRequest).observe(this, Observer {
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
}