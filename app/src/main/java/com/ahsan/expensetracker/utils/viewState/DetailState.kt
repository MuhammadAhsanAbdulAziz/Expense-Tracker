package com.ahsan.expensetracker.utils.viewState

import com.ahsan.expensetracker.models.Transaction


sealed class DetailState {
    object Loading : DetailState()
    object Empty : DetailState()
    data class Success(val transaction: Transaction) : DetailState()
    data class Error(val exception: Throwable) : DetailState()
}

