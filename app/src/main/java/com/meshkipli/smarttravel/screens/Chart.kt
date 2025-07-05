package com.meshkipli.smarttravel.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkipli.smarttravel.ui.wallet.CategoryExpenseSummary // Import your new data class
import java.text.NumberFormat
import kotlin.math.ceil

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

    // Determine the maximum amount to scale the bars correctly.
    // Could also be the overall total if you want percentages relative to that.
    val maxAmount = summaries.maxOfOrNull { it.totalAmount } ?: 1.0 // Avoid division by zero

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
                verticalAlignment = Alignment.Bottom // Align bars to the bottom
            ) {
                summaries.take(7).forEach { summary -> // Take top N categories or implement scrolling
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f) // Distribute space evenly
                    ) {
                        // Bar
                        Box(
                            modifier = Modifier
                                .width(barWidth)
                                .fillMaxHeight(
                                    fraction = (summary.totalAmount / maxAmount).toFloat()
                                        .coerceIn(0.05f, 1f)
                                ) // Min height for visibility
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(summary.displayCategory.color)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Category Icon (Optional)
                        Icon(
                            imageVector = summary.displayCategory.icon,
                            contentDescription = summary.displayCategory.name,
                            tint = summary.displayCategory.color, // Or MaterialTheme.colorScheme.onSurfaceVariant
                            modifier = Modifier.size(18.dp)
                        )
                        // Category Name (Optional, could get crowded)
                        // Text(
                        //    text = summary.displayCategory.name.take(3), // Abbreviate if needed
                        //    fontSize = 9.sp,
                        //    maxLines = 1,
                        //    overflow = TextOverflow.Ellipsis
                        // )
                    }
                }
            }
            // Optional: Add a legend or more details below the chart
        }
    }
}

// More Advanced Chart with Canvas (Optional, for more control)
@Composable
fun CategoryExpenseCanvasChart(
    summaries: List<CategoryExpenseSummary>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 200.dp
) {
    if (summaries.isEmpty()) {
        // ... same empty state as above
        return
    }
    val density = LocalDensity.current
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = density.run { 10.sp.toPx() }
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val currencyFormat = NumberFormat.getCurrencyInstance()

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(chartHeight)
        .padding(16.dp)) {
        val maxAmount = summaries.maxOfOrNull { it.totalAmount } ?: 1.0
        val barCount = summaries.size
        val totalBarSpacing = size.width * 0.1f // 10% of width for spacing between bars
        val barWidth = (size.width - totalBarSpacing) / barCount.coerceAtLeast(1)
        val spaceBetweenBars = if (barCount > 1) totalBarSpacing / (barCount - 1) else 0f

        summaries.forEachIndexed { index, summary ->
            val barHeight = (summary.totalAmount / maxAmount * size.height).toFloat().coerceAtLeast(10f) // min height
            val xOffset = index * (barWidth + spaceBetweenBars)

            // Draw Bar
            drawRect(
                color = summary.displayCategory.color,
                topLeft = Offset(xOffset, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )

            // Draw Category Name below bar (example)
            drawContext.canvas.nativeCanvas.drawText(
                summary.displayCategory.name.take(3), // Abbreviate
                xOffset + barWidth / 2,
                size.height + density.run { 12.sp.toPx() }, // Below the bar
                textPaint
            )
            // Draw Amount on top of bar (example)
            drawContext.canvas.nativeCanvas.drawText(
                currencyFormat.format(summary.totalAmount).take(4), // Abbreviate amount
                xOffset + barWidth / 2,
                size.height - barHeight - density.run { 4.sp.toPx() }, // Above the bar
                textPaint.apply { color = android.graphics.Color.DKGRAY } // Different color for amount
            )
        }
    }
}
