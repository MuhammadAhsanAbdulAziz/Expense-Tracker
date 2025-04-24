package com.ahsan.expensetracker.utils.viewState


sealed class SummaryState {
    object Loading : SummaryState()
    data class Success(val income: Double, val expense: Double) : SummaryState() {
        val savings = income - expense
    }
    data class Error(val message: String) : SummaryState()
}