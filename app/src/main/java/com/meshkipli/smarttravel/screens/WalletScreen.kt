package com.meshkipli.smarttravel.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class ExpenseCategory(
    val name: String,
    val icon: ImageVector,
    val percentage: Float,
    val color: Color
)



// --- Wallet Screen Components ---

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

@Composable
fun ExpenseChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // --- This is a placeholder for a real chart library ---
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..7).forEach { _ ->
                val barHeight = Random.nextFloat()
                Box(
                    modifier = Modifier
                        .width(25.dp)
                        .fillMaxHeight(barHeight)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun CategoryRowItem(category: ExpenseCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(category.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, contentDescription = category.name, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Expense", color = Color.Gray, fontSize = 12.sp)
        }
        Text(
            text = "${(category.percentage * 100).toInt()}%",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}


// --- Main Wallet Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen() {
    val orangeColor = Color(0xFFF9882B)
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    // Dummy data
    val expenseCategories = listOf(
        ExpenseCategory("Food", Icons.Default.LunchDining, 0.45f, Color(0xFF4CAF50)),
        ExpenseCategory("Shopping", Icons.Default.ShoppingCart, 0.25f, Color(0xFF2196F3)),
        ExpenseCategory("Transport", Icons.Default.DirectionsCar, 0.15f, Color(0xFFFFC107)),
        ExpenseCategory("Housing", Icons.Default.Home, 0.15f, Color(0xFFE91E63))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true },
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
                categories = expenseCategories.map { it.name },
                onDismiss = { showAddExpenseDialog = false },
                onSave = { title, category, amount ->
                    // Handle save logic here
                    showAddExpenseDialog = false
                }
            )
        }

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item { WalletHeader(total = "$1,280.50", dailyAverage = "$42.68") }
            item { ExpenseChart() }
            item {
                Text(
                    "Expense Categories",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                )
            }
            items(expenseCategories) { category ->
                CategoryRowItem(category = category)
            }
        }
    }
}

// --- Add Expense Dialog ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    leadingIcon = { Text("$", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                // Category Dropdown
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
                        onDismissRequest = { isCategoryExpanded = false }
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
                onClick = { onSave(title, selectedCategory, amount) },
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


// --- Preview ---

@Preview(showBackground = true, name = "Wallet Screen", widthDp = 360, heightDp = 800)
@Composable
fun WalletScreenPreview() {
    MaterialTheme {
        WalletScreen()
    }
}