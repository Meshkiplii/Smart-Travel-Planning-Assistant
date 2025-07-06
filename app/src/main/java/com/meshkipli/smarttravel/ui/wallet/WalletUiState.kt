package com.meshkipli.smarttravel.ui.wallet // Or your specific UI package for wallet

import com.meshkipli.smarttravel.data.local.db.entities.Expense
import com.meshkipli.smarttravel.ui.common.DisplayExpenseCategory


data class CategoryExpenseSummary(
    val displayCategory: DisplayExpenseCategory,
    val totalAmount: Double,
    val percentage: Float
)


data class WalletUiState(
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val dailyAverage: Double = 0.0,
    val categorySummaries: List<CategoryExpenseSummary> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
