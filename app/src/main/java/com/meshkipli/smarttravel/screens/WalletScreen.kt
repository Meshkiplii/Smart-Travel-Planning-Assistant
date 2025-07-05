package com.meshkipli.smarttravel.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meshkipli.smarttravel.data.local.db.entities.Expense // Import your Expense entity
import com.meshkipli.smarttravel.ui.wallet.WalletUiState
import com.meshkipli.smarttravel.ui.wallet.WalletViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Recommended for collecting flows

// Data class for representing categories in the UI, mapping to your Expense entity's category string
data class DisplayExpenseCategory(
    val name: String, // Matches Expense.category
    val icon: ImageVector,
    val color: Color
)

fun getDisplayCategory(categoryName: String, allDisplayCategories: List<DisplayExpenseCategory>): DisplayExpenseCategory {
    return allDisplayCategories.find { it.name.equals(categoryName, ignoreCase = true) }
        ?: DisplayExpenseCategory("Others", Icons.Default.Category, Color.Gray) // Fallback
}


@Composable
fun WalletHeader(total: String, dailyAverage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TOTAL", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(total, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("DAILY AVERAGE", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(dailyAverage, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
        }
    }
}

val availableCategories = listOf( // Define your categories here
    DisplayExpenseCategory("Food", Icons.Default.LunchDining, Color(0xFF4CAF50)),
    DisplayExpenseCategory("Shopping", Icons.Default.ShoppingCart, Color(0xFF2196F3)),
    DisplayExpenseCategory("Transport", Icons.Default.DirectionsCar, Color(0xFFFFC107)),
    DisplayExpenseCategory("Housing", Icons.Default.Home, Color(0xFFE91E63)),
    DisplayExpenseCategory("Entertainment", Icons.Default.Movie, Color(0xFF9C27B0)), // Changed icon
    DisplayExpenseCategory("Health", Icons.Default.LocalHospital, Color(0xFF673AB7)),
    DisplayExpenseCategory("Education", Icons.Default.School, Color(0xFF3F51B5)),
    DisplayExpenseCategory("Miscellaneous", Icons.Default.Apps, Color(0xFF795548)),
    DisplayExpenseCategory("Bills", Icons.Default.Receipt, Color(0xFF009688)),
    DisplayExpenseCategory("Gifts", Icons.Default.CardGiftcard, Color(0xFF9E9E9E)),
    DisplayExpenseCategory("Others", Icons.Default.Category, Color.DarkGray)
)


@Composable
fun ExpenseRowItem(
    expense: Expense,
    displayCategory: DisplayExpenseCategory,
    onExpenseClick: (Expense) -> Unit // For editing or deleting
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpenseClick(expense) }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(displayCategory.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(displayCategory.icon, contentDescription = displayCategory.name, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
            Text(dateFormat.format(expense.date), color = Color.Gray, fontSize = 12.sp)
        }
        Text(
            text = currencyFormat.format(expense.amount),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (expense.amount >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error // Example color
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(walletViewModel: WalletViewModel = viewModel()) {
    val orangeColor = Color(0xFFF9882B)
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) } // For editing

    val uiState by walletViewModel.uiState.collectAsStateWithLifecycle()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

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
        if (showAddExpenseDialog || expenseToEdit != null) {
            AddExpenseDialog(
                categories = availableCategories.map { it.name },
                existingExpense = expenseToEdit,
                onDismiss = {
                    showAddExpenseDialog = false
                    expenseToEdit = null
                },
                onSave = { title, category, amountStr, existingId ->
                    if (existingId != null && expenseToEdit != null) {
                        // Update existing expense
                        val updatedExpense = expenseToEdit!!.copy(
                            title = title,
                            category = category,
                            amount = amountStr.toDoubleOrNull() ?: expenseToEdit!!.amount // Keep old if invalid
                            // date will remain the same unless you want to update it
                        )
                        walletViewModel.updateExpense(updatedExpense)
                    } else {
                        // Add new expense
                        walletViewModel.addExpense(title, amountStr, category)
                    }
                    showAddExpenseDialog = false
                    expenseToEdit = null
                }
            )
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
                        modifier = Modifier.padding(top = 8.dp) // Add some top padding
                    )

                }
                if (uiState.expenses.isEmpty()) {
                    item {
                        Text(
                            "No expenses yet. Tap '+' to add your first one!",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    item {
                        Text(
                            "Recent Expenses", // Changed from "Expense Categories"
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
                                // Option 1: Show dialog to edit/delete
                                expenseToEdit = selectedExpense
                                // showAddExpenseDialog = true // This will open the dialog in edit mode
                                // Or, option 2: navigate to a detailed screen, or show a context menu
                            }
                        )
                        // Add functionality to delete by swiping or long press if desired
                    }
                }
            }
        }
    }
}

// --- Add/Edit Expense Dialog ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    categories: List<String>,
    existingExpense: Expense? = null, // Pass existing expense for editing
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, amount: String, existingId: Int?) -> Unit
) {
    var title by remember { mutableStateOf(existingExpense?.title ?: "") }
    var amount by remember { mutableStateOf(existingExpense?.amount?.toString() ?: "") }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(existingExpense?.category ?: categories.firstOrNull() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingExpense == null) "Add New Expense" else "Edit Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    leadingIcon = { Text("$", color = Color.Gray) }, // Adjust currency symbol if needed
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = isCategoryExpanded,
                    onExpandedChange = { isCategoryExpanded = !isCategoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryExpanded,
                        onDismissRequest = { isCategoryExpanded = false },
                        modifier = Modifier.requiredSizeIn(maxHeight = 200.dp) // Limit dropdown height
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank() && selectedCategory.isNotBlank()) {
                        onSave(title, selectedCategory, amount, existingExpense?.id)
                    }
                    // else: handle validation error, e.g. show a Toast or highlight fields
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9882B))
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}




@Preview(showBackground = true, name = "Wallet Screen - Empty", widthDp = 360, heightDp = 800)
@Composable
fun WalletScreenEmptyPreview() {
    MaterialTheme {
        // Provide a mock ViewModel with empty state for preview
        WalletScreen(walletViewModel = viewModel())
    }
}

@Preview(showBackground = true, name = "Wallet Screen - With Data", widthDp = 360, heightDp = 800)
@Composable
fun WalletScreenWithDataPreview() {
    MaterialTheme {
        // To preview with data, you'd ideally have a way to populate the ViewModel's state
        // with mock data or have a pre-populated database for debug builds.
        val mockViewModel = WalletViewModel(LocalContext.current.applicationContext as Application)
        // For a true preview with data, you might need to:
        // 1. Extend WalletViewModel for previews and set a mock _uiState.value.
        // 2. Or, ensure your debug database has some sample entries.
        WalletScreen(walletViewModel = mockViewModel)
    }
}
