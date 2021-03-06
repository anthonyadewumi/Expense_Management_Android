package com.bonhams.expensemanagement.ui.FinanceManger

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import com.bonhams.expensemanagement.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class FinaanceMangerViewModel(private val claimsByPageRepository: RequestByPageRepository,
                              private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val datePicker = MutableLiveData<Any?>()
    private val statusPicker = MutableLiveData<Any?>()

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
    val claims = flowOf(
        clearListCh.receiveAsFlow().map { PagingData.empty<ToBeAcceptedData>() },
        savedStateHandle.getLiveData<String>(KEY_CLAIMS)
            .asFlow()
            .flatMapLatest { claimsByPageRepository.getClaimsList(getClaimsRequest(it)) }
            // cachedIn() shares the paging state across multiple consumers of posts,
            // e.g. different generations of UI across rotation config change
            .cachedIn(viewModelScope)
    ).flattenMerge(2)

    fun shouldShowClaimList(
        search: String
    ) = savedStateHandle.get<String>(KEY_CLAIMS) != search

    
    fun showClaimsList(search: String) {
//        if (!shouldShowClaimList(search)) return

        clearListCh.offer(Unit)
        savedStateHandle.set(KEY_CLAIMS, search)
    }

    fun showClaimsList(search: String, status: String?, date: Any?) {

        if(status != null)
            statusPicker.value = status
        if(date != null)
            datePicker.value = date

        clearListCh.offer(Unit)
        savedStateHandle.set(KEY_CLAIMS, search)
    }

    fun resetFilters() {

        statusPicker.value = null
        datePicker.value = null

        clearListCh.offer(Unit)
        savedStateHandle.set(KEY_CLAIMS, "")
    }

    private fun getClaimsRequest(search: String): ClaimsRequest {
        val claimListRequest = ClaimsRequest()
        claimListRequest.searchKey = search
        claimListRequest.page = 1
        claimListRequest.numberOfItems = Constants.NETWORK_PAGE_SIZE
        claimListRequest.status = statusPicker.value as String?
        claimListRequest.fromDate = (datePicker.value as Pair<*, *>?)?.first as String?
        claimListRequest.toDate  = (datePicker.value as Pair<*, *>?)?.second as String?
        return claimListRequest
    }
}