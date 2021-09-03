package com.bonhams.expensemanagement.data.services.requests

import com.google.gson.annotations.SerializedName

class DeleteClaimRequest {
    @SerializedName("claim_id")
    var claimId: String = ""
}