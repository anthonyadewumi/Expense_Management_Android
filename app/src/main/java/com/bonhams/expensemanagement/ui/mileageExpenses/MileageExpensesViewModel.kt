package com.bonhams.expensemanagement.ui.mileageExpenses

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.requests.MileageExpenseRequest
import com.bonhams.expensemanagement.data.services.responses.TotalClaimedData
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MileageExpensesViewModel(private val mileageExpensesByPageRepository: MileageExpensesByPageRepository,
                               private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val datePicker = MutableLiveData<Any?>()
    private val statusPicker = MutableLiveData<Any?>()
    lateinit var totalClaimedList: List<TotalClaimedData>

    companion object {
        const val KEY_CLAIMS = "expenses_list"
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

    fun showExpensesList(search: String, status: String?, date: Any?) {

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

    fun getClaimedTotal(requestObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mileageExpensesByPageRepository.getClaimedTotal(requestObject)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    fun getMileageListRequest(search: String): MileageExpenseRequest {
        val expenseRequest = MileageExpenseRequest()
        expenseRequest.searchKey = search
        expenseRequest.page = 1
        expenseRequest.batch_allotted = Constants.batch_allotted
        expenseRequest.numberOfItems = Constants.NETWORK_PAGE_SIZE
        expenseRequest.status = statusPicker.value as String?
        expenseRequest.fromDate = (datePicker.value as Pair<*, *>?)?.first as String?
        expenseRequest.toDate  = (datePicker.value as Pair<*, *>?)?.second as String?
        return expenseRequest
    }
}