package com.bonhams.expensemanagement.utils

class Constants {

    companion object {
        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$"
//        const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
        const val DD_MM_YYYY_FORMAT = "dd/MM/yyyy"
        const val DD_MMM_YYYY_FORMAT = "dd MMM yyyy"
    }
}