package com.bonhams.expensemanagement.ui.mileageExpenses

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.MileageAdapter
import com.bonhams.expensemanagement.adapters.MileageExpensesLoadStateAdapter
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentClaimsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.home.HomeViewModel
import com.bonhams.expensemanagement.ui.home.HomeViewModelFactory
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail.MileageDetailFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.utils.Utils.Companion.showKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class MileageExpensesFragment() : Fragment(), MileageAdapter.OnMileageExpenseClickListener {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var adapter: MileageAdapter
    private lateinit var viewModel: MileageExpensesViewModel
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
        initAdapter()
        initSwipeToRefresh()
        initSearch()
        
        return view
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            MileageExpensesViewModelFactory(this, ApiHelper(RetrofitBuilder.apiService))
        ).get(MileageExpensesViewModel::class.java)

        homeViewModel = ViewModelProvider(requireActivity(),
            HomeViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)

        homeViewModel.datePicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: datePicker: $it")
            updatedClaimsFromStatus(null, it)
        })

        homeViewModel.statusPicker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "setupViewModel: statusPicker: $it")
            updatedClaimsFromStatus(it as String?, null)
        })

        mainViewModel.appbarSearchClick?.observe(viewLifecycleOwner, {
            Log.d(TAG, "setupViewModel: appbarSearchClick: $it")
            if(it){
                binding.tilSearchClaim.visibility = View.VISIBLE
                binding.edtSearchClaim.showKeyboard(contextActivity, true)
            }
            else{
                binding.tilSearchClaim.visibility = View.GONE
                binding.edtSearchClaim.setText("")
                binding.edtSearchClaim.showKeyboard(contextActivity, false)
            }
        })
    }

    private fun initAdapter() {
        adapter = MileageAdapter()
        adapter.setupClaimListener(this)
        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MileageExpensesLoadStateAdapter(adapter),
            footer = MileageExpensesLoadStateAdapter(adapter)
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(contextActivity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading
                && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                binding.mNoResult.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.mNoResult.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefresh.isRefreshing = loadStates.mediator?.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.mileageExpenses.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                // Use a state-machine to track LoadStates such that we only transition to
                // NotLoading from a RemoteMediator load if it was also presented to UI.
//                .asMergedLoadStates()
                // Only emit when REFRESH changes, as we only want to react on loads replacing the
                // list.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
            // Scroll to top is synchronous with UI updates, even if remote load was triggered.
//                .collect { binding.recyclerView.scrollToPosition(0) }
        }
    }
    private fun initSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.resetFilters()
            homeViewModel.resetFilters()
//            adapter.refresh()
        }
    }
    private fun initSearch() {
        binding.edtSearchClaim.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
                updatedClaimsFromInput()
                true
            } else {
                false
            }
        }
        binding.edtSearchClaim.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedClaimsFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedClaimsFromInput() {
        binding.edtSearchClaim.text!!.trim().toString().let {
            if (/*it.isNotBlank() &&*/ viewModel.shouldShowExpensesList(it)) {
                viewModel.showExpensesList(it)
            }
        }
    }

    private fun updatedClaimsFromStatus(status: String?, date: Any?) {
        binding.edtSearchClaim.text!!.trim().toString().let {
            viewModel.showExpensesList(it, status, date)
        }
    }

    override fun onMileageExpenseItemClicked(claim: MileageDetail?, position: Int) {
        Log.d(TAG, "onMileageExpenseItemClicked: $position")
        val fragment = MileageDetailFragment()
        fragment.setMileageDetails(claim)
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }

    override fun onMileageExpenseCreateCopyClicked(claim: MileageDetail?, position: Int) {
        Log.d(TAG, "onMileageExpenseCreateCopyClicked: $position")
        val fragment = NewMileageClaimFragment()
        fragment.setMileageDetails(claim)
        (contextActivity as? MainActivity)?.addFragment(fragment)
    }

}