package com.meshkipli.smarttravel.ui.wallet // Or your UI package

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.meshkipli.smarttravel.data.local.db.AppDatabase
import com.meshkipli.smarttravel.data.local.db.entities.Expense
import com.meshkipli.smarttravel.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

data class WalletUiState(
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val dailyAverage: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository: ExpenseRepository by lazy {
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        ExpenseRepository(expenseDao)
    }

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        // Now, the primary job of init is to start loading data.
        // The repository will be initialized when loadWalletData() first accesses it.
        loadWalletData()
    }
    private fun loadWalletData() {
        viewModelScope.launch {
            try {
                // Accessing expenseRepository here will trigger its lazy initialization if not already done.
                combine(
                    expenseRepository.allExpenses,
                    expenseRepository.totalExpenses,
                    expenseRepository.firstExpenseDate
                ) { expenses, total, firstDate ->
                    val currentTotal = total ?: 0.0
                    val dailyAvg = calculateDailyAverage(currentTotal, firstDate)
                    WalletUiState(
                        expenses = expenses,
                        totalAmount = currentTotal,
                        dailyAverage = dailyAvg,
                        isLoading = false
                    )
                }.catch { throwable ->
                    android.util.Log.e("WalletViewModel", "Error loading wallet data", throwable)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load data: ${throwable.message}"
                    )
                }.collect { combinedState ->
                    _uiState.value = combinedState
                }
            } catch (e: Exception) {
                // This catch block handles exceptions during the lazy initialization of expenseRepository
                android.util.Log.e("WalletViewModel", "Error initializing repository or loading data", e)
                _uiState.value = WalletUiState(
                    isLoading = false,
                    expenses = emptyList(),
                    error = "Failed to initialize wallet: ${e.message}"
                )
            }
        }
    }
    private fun calculateDailyAverage(totalAmount: Double, firstExpenseDate: Date?): Double {
        if (totalAmount == 0.0 || firstExpenseDate == null) {
            return 0.0
        }
        val today = Calendar.getInstance().time
        if (firstExpenseDate.after(today)) return totalAmount // First expense cannot be in the future

        val diffInMillis = today.time - firstExpenseDate.time
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return if (days >= 1) { // Count at least one day if there's any expense
            totalAmount / days
        } else {
            // If it's the same day, or less than 24 hours but spanning across midnight for 'days=0',
            // count it as one day for average purposes.
            totalAmount
        }
    }


    fun addExpense(title: String, amountString: String, category: String) {
        val amount = amountString.toDoubleOrNull()
        if (title.isBlank() || amount == null || category.isBlank()) {
            android.util.Log.w("WalletViewModel", "Invalid input for addExpense: Title='$title', Amount='$amountString', Category='$category'")
            _uiState.value = _uiState.value.copy(error = "Invalid input. Please fill all fields.")
            return
        }
        viewModelScope.launch {
            try {
                val newExpense = Expense(
                    title = title,
                    amount = amount,
                    category = category,
                    date = Date() // Current date and time
                )
                expenseRepository.insert(newExpense) // Repository access will trigger lazy init if needed
                _uiState.value = _uiState.value.copy(error = null) // Clear previous error
            } catch (e: Exception) {
                android.util.Log.e("WalletViewModel", "Error adding expense", e)
                _uiState.value = _uiState.value.copy(error = "Failed to add expense: ${e.message}")
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.update(expense)
                _uiState.value = _uiState.value.copy(error = null)
            } catch (e: Exception) {
                android.util.Log.e("WalletViewModel", "Error updating expense", e)
                _uiState.value = _uiState.value.copy(error = "Failed to update expense: ${e.message}")
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.delete(expense)
                _uiState.value = _uiState.value.copy(error = null)
            } catch (e: Exception) {
                android.util.Log.e("WalletViewModel", "Error deleting expense", e)
                _uiState.value = _uiState.value.copy(error = "Failed to delete expense: ${e.message}")
            }
        }
    }
}
