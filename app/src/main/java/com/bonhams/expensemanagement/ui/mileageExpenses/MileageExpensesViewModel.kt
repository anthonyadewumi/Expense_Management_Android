package com.bonhams.expensemanagement.ui.mileageExpenses

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.responses.MileageListResponse
import com.bonhams.expensemanagement.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MileageExpensesViewModel(private val mileageExpensesByPageRepository: MileageExpensesByPageRepository,
                               private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var responseMileageList: MutableLiveData<MileageListResponse>? = null

    companion object {
        const val KEY_CLAIMS = "claims_list"
        const val DEFAULT_CLAIMS = ""
    }

    init {
        if (!savedStateHandle.contains(KEY_CLAIMS)) {
            savedStateHandle.set(KEY_CLAIMS, DEFAULT_CLAIMS)
        }
    }

    private val clearListCh = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val mileageExpenses = flowOf(
        clearListCh.receiveAsFlow().map { PagingData.empty<MileageDetail>() },
        savedStateHandle.getLiveData<String>(KEY_CLAIMS)
            .asFlow()
            .flatMapLatest { mileageExpensesByPageRepository.getMileageExpensesList(getMileageListRequest(it)) }
            // cachedIn() shares the paging state across multiple consumers of posts,
            // e.g. different generations of UI across rotation config change
            .cachedIn(viewModelScope)
    ).flattenMerge(2)

    fun shouldShowExpensesList(
        search: String
    ) = savedStateHandle.get<String>(KEY_CLAIMS) != search


    fun showExpensesList(search: String) {
//        if (!shouldShowClaimList(search)) return
        clearListCh.offer(Unit)
        savedStateHandle.set(KEY_CLAIMS, search)
    }

    fun getMileageListRequest(search: String): MileageExpenseRequest {
        val expenseRequest = MileageExpenseRequest()
        expenseRequest.searchKey = search
        expenseRequest.page = 1
        expenseRequest.numberOfItems = Constants.NETWORK_PAGE_SIZE
        return expenseRequest
    }
}