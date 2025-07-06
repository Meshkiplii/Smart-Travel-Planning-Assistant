package com.meshkipli.smarttravel.ui.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.data.local.db.entities.Expense
import com.meshkipli.smarttravel.ui.common.DisplayExpenseCategory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
