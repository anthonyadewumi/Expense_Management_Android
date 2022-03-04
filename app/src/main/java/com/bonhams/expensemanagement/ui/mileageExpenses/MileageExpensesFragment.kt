package com.bonhams.expensemanagement.ui.mileageExpenses

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.adapters.MileageAdapter
import com.bonhams.expensemanagement.adapters.MileageExpensesLoadStateAdapter
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentClaimsBinding
import com.bonhams.expensemanagement.databinding.FragmentMilegeBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.home.HomeViewModel
import com.bonhams.expensemanagement.ui.home.HomeViewModelFactory
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail.MileageDetailFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.utils.Status
import com.bonhams.expensemanagement.utils.Utils.Companion.showKeyboard
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class MileageExpensesFragment() : Fragment(), MileageAdapter.OnMileageExpenseClickListener {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var adapter: MileageAdapter
    private lateinit var viewModel: MileageExpensesViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentMilegeBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_milege, container, false)
        val view = binding.root
        binding.lifecycleOwner = this

        contextActivity = activity as? BaseActivity

        setupViewModel()
        initSwipeToRefresh()
        initSearch()
        getClaimTotal()
        return view
    }

    override fun onResume() {
        super.onResume()
        initAdapter()

    }
    private fun getClaimTotal() {
        val jsonObject = JsonObject().also {
            it.addProperty("data_of", "M")
        }
        viewModel.getClaimedTotal(jsonObject).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                viewModel.totalClaimedList=response.data
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                setupSpinners()
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
    private fun setupSpinners(){
        // Tax Adapter

        if(viewModel.totalClaimedList.isNotEmpty()){
            binding.lnMileageClaimed.visibility=View.VISIBLE
        }else{
            binding.lnMileageClaimed.visibility=View.GONE

        }

        var mutableList1 = mutableListOf("")
        mutableList1.clear()
        viewModel.totalClaimedList.forEach {
            it.mileage_type?.let { it1 -> mutableList1.add(it1) }
        }
        val currencyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            mutableList1 as List<String>
        )
        binding.spnCurrency.adapter = currencyAdapter
        binding.spnCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.txtTotalClaimed.text = String.format("%.2f",viewModel.totalClaimedList[position].miles_claimed.toString().toDouble())

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


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


        mainViewModel.isMileageListRefresh?.observe(viewLifecycleOwner, {
            if(it) {
                mainViewModel.isMileageListRefresh?.value=false
                adapter.refresh()
            }

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
        binding.txtTitle.text = "Mileage Claimed"
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
    private fun showImagePopup(imageUrl:String) {
        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_popup_dialog)

        val image = dialog.findViewById(R.id.itemImage) as ImageView
        Glide.with(this)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.mountains)
                    .error(R.drawable.mountains)
            )
            .placeholder(R.drawable.mountains)
            .error(R.drawable.mountains)
            .into(image)


        dialog.show()
        val noBtn = dialog.findViewById(R.id.lnClose) as LinearLayout
        noBtn.setOnClickListener {
            dialog.dismiss()
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

    override fun onMileageImageClicked(imageUrl: String?, position: Int) {
        if (imageUrl != null) {
            showImagePopup(imageUrl)
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