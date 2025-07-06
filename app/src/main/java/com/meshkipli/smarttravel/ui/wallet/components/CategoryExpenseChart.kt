package com.meshkipli.smarttravel.ui.wallet.components

// ... (imports for Card, Box, Icon, Text, DisplayExpenseCategory, CategoryExpenseSummary etc.)
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//... other necessary imports
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meshkipli.smarttravel.ui.wallet.CategoryExpenseSummary // Correct import

@Composable
fun CategoryExpenseChart(
    summaries: List<CategoryExpenseSummary>,
    modifier: Modifier = Modifier,
    barWidth: Dp = 28.dp,
    chartHeight: Dp = 200.dp
) {
    if (summaries.isEmpty()) {
        Text(
            "No category spending to display yet.",
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        return
    }
    val maxAmount = summaries.maxOfOrNull { it.totalAmount } ?: 1.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight + 60.dp) // Extra space for labels
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                summaries.take(7).forEach { summary ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(barWidth)
                                .fillMaxHeight(fraction = (summary.totalAmount / maxAmount).toFloat().coerceIn(0.05f, 1f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(summary.displayCategory.color)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = summary.displayCategory.icon,
                            contentDescription = summary.displayCategory.name,
                            tint = summary.displayCategory.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
