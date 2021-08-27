/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bonhams.expensemanagement.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.requests.ClaimsRequest
import retrofit2.HttpException
import java.io.IOException

/**
 * A [PagingSource] that uses the before/after keys returned in page requests.
 *
 * @see ItemKeyedSubredditPagingSource
 */
open class ClaimsPagingSource(
    private val apiHelper: ApiHelper,
    private val claimsRequest: ClaimsRequest
) : PagingSource<Int, ClaimDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClaimDetail> {
        val pageNumber = params.key ?: 1
        return try {
            claimsRequest.page = pageNumber
            val data = apiHelper.claimsList(claimsRequest)
            val nextKey = if(data.success) pageNumber.plus(1) else pageNumber
//            val prevKey = if (pageNumber == 1) null else pageNumber - 1

            if(data.success) {
                Page(
                    data = data.claimsList!!,
                    prevKey = null, // Only paging forward. prevKey,
                    nextKey = nextKey //pageNumber.plus(1)
                )
            }
            else{
                Page(
                    data = emptyList<ClaimDetail>(),
                    prevKey = null, // Only paging forward. prevKey,
                    nextKey = null, //pageNumber.plus(1)
                )
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ClaimDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
//            state.closestPageToPosition(anchorPosition)?.prevKey
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
