package com.meshkipli.smarttravel.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meshkipli.smarttravel.ui.wallet.components.AddExpenseDialog
import com.meshkipli.smarttravel.ui.wallet.components.CategoryExpenseChart
import com.meshkipli.smarttravel.ui.wallet.components.ExpenseRowItem
import com.meshkipli.smarttravel.ui.wallet.components.WalletHeader
import com.meshkipli.smarttravel.data.local.db.entities.Expense // Keep if needed directly
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Recommended
import com.meshkipli.smarttravel.ui.common.DisplayExpenseCategory
import com.meshkipli.smarttravel.ui.common.availableCategories
import java.text.NumberFormat
import java.util.Locale


fun getDisplayCategory(categoryName: String, categories: List<DisplayExpenseCategory>): DisplayExpenseCategory {
    return categories.find { it.name.equals(categoryName, ignoreCase = true) }
        ?: DisplayExpenseCategory("Other", Icons.Default.MoreVert, Color.Gray) // Fallback
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    walletViewModel: WalletViewModel = viewModel(),
    // Potentially pass availableCategories if it's defined higher up and needed by components
    // availableCategories: List<DisplayExpenseCategory> = LocalAvailableCategories.current
) {
    val orangeColor = Color(0xFFF9882B);
    val uiState by walletViewModel.uiState.collectAsStateWithLifecycle()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    expenseToEdit = null // Ensure it's for adding new
                    showAddExpenseDialog = true
                },
                containerColor = orangeColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (showAddExpenseDialog) {
            AddExpenseDialog(
                expenseToEdit = expenseToEdit,
                onDismiss = { showAddExpenseDialog = false },
                onSave = { title, category, amountStr, existingId ->
                    if (existingId != null && expenseToEdit != null) {
                        // Create an updated expense object
                        val updatedExpense = expenseToEdit!!.copy(
                            title = title,
                            category = category,
                            amount = amountStr.toDoubleOrNull() ?: expenseToEdit!!.amount // Keep old if invalid
                        )
                        walletViewModel.updateExpense(updatedExpense)
                    } else {
                        walletViewModel.addExpense(title, amountStr, category)
                    }
                    showAddExpenseDialog = false
                }
            )
        }

        if (uiState.isLoading && uiState.expenses.isEmpty() && uiState.categorySummaries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    WalletHeader(
                        total = currencyFormat.format(uiState.totalAmount),
                        dailyAverage = currencyFormat.format(uiState.dailyAverage)
                    )
                }
                item {
                    CategoryExpenseChart(
                        summaries = uiState.categorySummaries,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (uiState.expenses.isEmpty() && uiState.categorySummaries.isEmpty()) {
                    item {
                        Text(
                            "No expenses yet. Tap '+' to add your first one!",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else if (uiState.expenses.isNotEmpty()) {
                    item {
                        Text(
                            "Recent Expenses",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }
                    items(uiState.expenses, key = { it.id }) { expense ->
                        val displayCategory = getDisplayCategory(expense.category, availableCategories)
                        ExpenseRowItem(
                            expense = expense,
                            displayCategory = displayCategory,
                            onExpenseClick = { selectedExpense ->
                                expenseToEdit = selectedExpense
                                showAddExpenseDialog = true // Open dialog to edit
                            }
                        )
                    }
                }
            }
        }
    }
}
