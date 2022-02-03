package com.bonhams.expensemanagement.data.services.responses

import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.google.gson.annotations.SerializedName

class ClaimDetailsResponse {
    @SerializedName("success")
    var success: Boolean = false
    @SerializedName("message")
    var message: String? = ""
    @SerializedName("main_claim")
    var main_claim: MainClaim?=null
    @SerializedName("splitted_claim")
    var splitedClaim: List<SplitedClaim>? = null
}
class MainClaim (
    val id: String = "",
    @SerializedName("claim_type")
    val claimType: String = "",
    @SerializedName("userId")
    val userID: String = "",
    val createdBy: String = "",
    @SerializedName("userTypeId")
    val userTypeID: String = "",
    val userType: String = "",
    @SerializedName("employ_id")
    val employID: String = "",
    val title: String = "",
    val description: String = "",
    val totalAmount: String = "",
    @SerializedName("created_on")
    val createdOn: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("expense_group_id")
    val expenseGroupID: String = "",
    @SerializedName("expense_group_name")
    val expenseGroupName: String = "",
    @SerializedName("expense_type_id")
    val expenseTypeID: String = "",
    @SerializedName("expense_type_name")
    val expenseTypeName: String = "",
    val companyName: String = "",
    val department: String = "",
    @SerializedName("currency_type_id")
    val currencyTypeID: String = "",
    @SerializedName("currency_type_name")
    val currencyTypeName: String = "",
    @SerializedName("currency_symbol")
    val currencySymbol: String = "",
    @SerializedName("auction")
    val auction: Int = 0,
    @SerializedName("expenseCode")
    val expenseCode: String = "",
    val tax: String = "",
    @SerializedName("netAmount")
    val netAmount: String = "",
    val merchant: String = "",
    val attachments: String = "",
    @SerializedName("tax_code")
    val tax_code: String = "",
    @SerializedName("rm_updation_date")
    val rm_updation_date: String = "",
    @SerializedName("fm_updation_date")
    val fm_updation_date: String = "" ,
    @SerializedName("split_id")
    val split_id: String = ""
)
class SplitedClaim (
    val id: String = "",
    @SerializedName("claim_type")
    val claimType: String = "",
    @SerializedName("userId")
    val userID: String = "",
    val createdBy: String = "",
    @SerializedName("userTypeId")
    val userTypeID: String = "",
    val userType: String = "",
    @SerializedName("employ_id")
    val employID: String = "",
    val title: String = "",
    val description: String = "",
    var totalAmount: String = "",
    @SerializedName("created_on")
    val createdOn: String = "",
    val reportingMStatus: String = "",
    val financeMStatus: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("expense_group_id")
    val expenseGroupID: String = "",
    @SerializedName("expense_group_name")
    val expenseGroupName: String = "",
    @SerializedName("expense_type_id")
    val expenseTypeID: String = "",
    @SerializedName("expense_type_name")
    val expenseTypeName: String = "",
    val companyName: String = "",
    val department: String = "",
    @SerializedName("currency_type_id")
    val currencyTypeID: String = "",
    @SerializedName("currency_type_name")
    val currencyTypeName: String = "",
    @SerializedName("currency_symbol")
    val currencySymbol: String = "",
    @SerializedName("auction")
    val auction: Int = 0,
    @SerializedName("expenseCode")
    val expenseCode: String = "",
    @SerializedName("companyNumber")
    val companyNumber: String = "",
    val tax: String = "",
    val netAmount: String = "",
    val merchant: String = "",
    val attachments: String = "",
    @SerializedName("tax_code")
    val tax_code: String = "",
    @SerializedName("rm_updation_date")
    val rm_updation_date: String = "",
    @SerializedName("fm_updation_date")
    val fm_updation_date: String = "" ,
    @SerializedName("split_id")
    val split_id: String = ""
)