package com.bonhams.expensemanagement.data.model

data class SpinnerItem(var id: String, var title: String){
    override fun toString(): String {
        return title
    }
}