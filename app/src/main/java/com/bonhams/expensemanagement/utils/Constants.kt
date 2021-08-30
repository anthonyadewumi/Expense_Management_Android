package com.bonhams.expensemanagement.utils

class Constants {

    companion object {
        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$"
//        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
        const val DD_MM_YYYY_FORMAT = "dd/MM/yyyy"
        const val DD_MM_YYYY_SERVER_FORMAT = "yyyy/MM/dd"
        const val DD_MMM_YYYY_FORMAT = "dd MMM yyyy"

        const val NETWORK_PAGE_SIZE = 10

        const val STATUS_APPROVED = "Approved"
        const val STATUS_PENDING = "Pending"
        const val STATUS_REJECTED = "Rejected"
    }
}