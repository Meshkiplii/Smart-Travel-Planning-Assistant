package com.meshkipli.smarttravel.ui.wallet.components

// --- Imports needed for the new Donut Chart ---
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meshkipli.smarttravel.ui.common.DisplayExpenseCategory
import com.meshkipli.smarttravel.ui.wallet.CategoryExpenseSummary
import kotlin.math.cos
import kotlin.math.sin

//// --- Your existing data classes (assuming their structure) ---
//data class CategoryExpenseSummary(
//    val displayCategory: DisplayExpenseCategory,
//    val totalAmount: Double
//)

//data class DisplayExpenseCategory(
//    val name: String,
//    val icon: ImageVector, // We'll use name for the label, but keep this for data structure
//    val color: Color
//)
// -----------------------------------------------------------------


/**
 * A composable that displays category spending in a donut chart.
 * It positions labels around the chart corresponding to each colored segment.
 *
 * @param summaries A list of [CategoryExpenseSummary] to be displayed.
 * @param totalTimeString A formatted string representing the total, displayed in the center.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun CategoryExpenseChart(
    summaries: List<CategoryExpenseSummary>,
    totalTimeString: String,
    modifier: Modifier = Modifier
) {
    // Handle the case where there is no data to display
    if (summaries.isEmpty()) {
        Text(
            "No category spending to display yet.",
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        return
    }

    val totalAmount = summaries.sumOf { it.totalAmount }.toFloat()

    // Main container for the chart and its labels
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // The central text showing total time
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TODAY",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = totalTimeString,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Custom Layout to position the chart and its labels circularly
        Layout(
            content = {
                // 1. The donut chart is drawn on a Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        // Add padding to create space for the labels outside the chart
                        .padding(48.dp)
                ) {
                    val strokeWidth = 35.dp.toPx()
                    var startAngle = -90f // Start from the top

                    // Draw an arc for each summary
                    summaries.forEach { summary ->
                        val sweepAngle = (summary.totalAmount.toFloat() / totalAmount) * 360f
                        // Subtract a small gap angle to create the spacing between arcs
                        val gapAngle = 1f
                        drawArc(
                            color = summary.displayCategory.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - gapAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                            size = Size(size.width, size.height)
                        )
                        startAngle += sweepAngle
                    }
                }

                // 2. The Text labels for each segment
                summaries.forEach { summary ->
                    Text(
                        text = summary.displayCategory.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        ) { measurables, constraints ->
            // The first measurable is the Canvas, the rest are the Text labels
            val canvasPlaceable = measurables.first().measure(constraints)
            val labelPlaceables = measurables.drop(1).map { it.measure(constraints) }

            layout(canvasPlaceable.width, canvasPlaceable.height) {
                // Place the Canvas in the center
                canvasPlaceable.placeRelative(0, 0)

                // Calculate positions for the labels
                val chartRadius = canvasPlaceable.width / 2f
                val labelRadius = chartRadius * 1.35f // Position labels outside the chart ring
                val center = Offset(chartRadius, chartRadius)

                var currentAngle = -90.0
                labelPlaceables.forEachIndexed { index, placeable ->
                    val summary = summaries[index]
                    val sweepAngle = (summary.totalAmount / totalAmount) * 360.0
                    // Calculate the angle for the middle of the arc
                    val midAngle = currentAngle + sweepAngle / 2.0
                    val midAngleRad = Math.toRadians(midAngle)

                    // Calculate the (x, y) coordinates for the label
                    val x = center.x + labelRadius * cos(midAngleRad).toFloat()
                    val y = center.y + labelRadius * sin(midAngleRad).toFloat()

                    // Adjust position to center the text on the (x, y) coordinate
                    val textX = x - (placeable.width / 2)
                    val textY = y - (placeable.height / 2)

                    placeable.placeRelative(textX.toInt(), textY.toInt())

                    currentAngle += sweepAngle
                }
            }
        }
    }
}


/**
 * Preview function to display the CategoryExpenseChart in Android Studio.
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
private fun CategoryExpenseChartPreview() {
    // This mock data is estimated from the image to match the proportions.
     val total = 87.0
    val sampleSummaries = listOf(
        // Starting from the top ("Family Link") and going clockwise
        CategoryExpenseSummary(DisplayExpenseCategory("Family Link", Icons.Default.Warning, Color(0xFF5C86E4)), 35.0,(35.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Target", Icons.Default.Warning, Color(0xFFD65F58)), 12.0,(12.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Camera", Icons.Default.Warning, Color(0xFFF9BF4B)), 3.0,(3.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Twitter", Icons.Default.Warning, Color(0xFF43A047)), 7.0,(7.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Slack", Icons.Default.Warning, Color(0xFF8E24AA)), 4.0,(4.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Photos", Icons.Default.Warning, Color(0xFF00ACC1)), 2.0,(2.0 / total).toFloat()),
        CategoryExpenseSummary(DisplayExpenseCategory("Other", Icons.Default.Warning, Color(0xFFF48462)), 24.0,(24.0 / total).toFloat())
    )

    // Using a Surface with a white background to match the image
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Use plain white background as in the target image
    ) {
        CategoryExpenseChart(
            summaries = sampleSummaries,
            totalTimeString = "1 hr, 27 min"
        )
    }
}