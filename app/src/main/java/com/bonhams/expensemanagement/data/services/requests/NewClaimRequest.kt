package com.bonhams.expensemanagement.data.services.requests

class NewClaimRequest {
    var merchantName: String? = ""
    var expenseGroup: String? = ""
    var expenseType: String? = ""
    var companyNumber: String? = ""
    var department: String? = ""
    var dateSubmitted: String? = ""
    var currency:  String? = ""
    var totalAmount: String? = ""
    var tax: String? = ""
    var netAmount: String? = ""
    var description: String? = ""
    var attachments: String? = ""
    var split: String? = ""
}