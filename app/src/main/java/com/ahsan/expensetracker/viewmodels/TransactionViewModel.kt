package com.ahsan.expensetracker.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahsan.expensetracker.data.local.datastore.UIModeImpl
import com.ahsan.expensetracker.models.Transaction
import com.ahsan.expensetracker.repositories.TransactionRepo
import com.ahsan.expensetracker.utils.viewState.DetailState
import com.ahsan.expensetracker.utils.viewState.SummaryState
import com.ahsan.expensetracker.utils.viewState.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepo: TransactionRepo,
    private val uiModeDataStore: UIModeImpl
) : ViewModel() {

    private val _transactionFilter = MutableStateFlow("Overall")
    val transactionFilter: StateFlow<String> = _transactionFilter

    private val _uiState = MutableStateFlow<ViewState>(ViewState.Loading)
    private val _detailState = MutableStateFlow<DetailState>(DetailState.Loading)

    private val _summaryState = MutableStateFlow<SummaryState>(SummaryState.Loading)
    val summaryState: StateFlow<SummaryState> = _summaryState

    // UI collect from this stateFlow to get the state updates
    val uiState: StateFlow<ViewState> = _uiState
    val detailState: StateFlow<DetailState> = _detailState

    // get ui mode
    val getUIMode = uiModeDataStore.uiMode

    // save ui mode
    fun setDarkMode(isNightMode: Boolean) {
        viewModelScope.launch(IO) {
            uiModeDataStore.saveToDataStore(isNightMode)
        }
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                val income = transactionRepo.getTotalIncome()
                val expense = transactionRepo.getTotalExpense()
                _summaryState.value = SummaryState.Success(income, expense)
            } catch (e: Exception) {
                _summaryState.value = SummaryState.Error("Failed to load summary")
            }
        }
    }

    // insert transaction
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepo.insert(transaction)
    }

    // update transaction
    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepo.update(transaction)
    }

    // delete transaction
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepo.delete(transaction)
    }

    // get all transaction
    fun getAllTransaction(type: String) = viewModelScope.launch {
        transactionRepo.getAllSingleTransaction(type).collect { result ->
            if (result.isNullOrEmpty()) {
                _uiState.value = ViewState.Empty
            } else {
                _uiState.value = ViewState.Success(result)
                Log.i("Filter", "Transaction filter is ${transactionFilter.value}")
            }
        }
    }

    // get transaction by id
    fun getByID(id: Int) = viewModelScope.launch {
        _detailState.value = DetailState.Loading
        transactionRepo.getByID(id).collect { result: Transaction? ->
            if (result != null) {
                _detailState.value = DetailState.Success(result)
            }
        }
    }

    // delete transaction
    fun deleteByID(id: Int) = viewModelScope.launch {
        transactionRepo.deleteByID(id)
    }

    fun allIncome() {
        _transactionFilter.value = "Income"
    }

    fun allExpense() {
        _transactionFilter.value = "Expense"
    }

    fun overall() {
        _transactionFilter.value = "Overall"
    }
}
