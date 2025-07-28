package com.meshkipli.smarttravel.ui // Or your preferred package

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun rememberShimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f, // Adjust for speed/width of shimmer
    shimmerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
): Brush {
    if (!showShimmer) {
        return SolidColor(Color.Transparent)
    }

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate_animation"
    )

    return Brush.linearGradient(
        colors = listOf(
            backgroundColor,
            shimmerColor,
            backgroundColor,
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnimation, y = translateAnimation)
    )
}

fun Modifier.shimmerBackground(shape: RoundedCornerShape = RoundedCornerShape(8.dp)): Modifier = composed {
    this.background(
        brush = rememberShimmerBrush(),
        shape = shape
    )
}

// --- Shimmer Placeholders for HomeScreen ---

@Composable
fun ShimmerLocationCardPlaceholder(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .width(180.dp) // Same as LocationCard
                .height(240.dp) // Same as LocationCard
                .shimmerBackground(shape = RoundedCornerShape(16.dp)) // Same shape
        )
    }
}

@Composable
fun ShimmerLocationCategoryRowPlaceholder(
    titlePlaceholderWidthFraction: Float = 0.4f,
    itemCount: Int = 3
) {
    val shimmerBrush = rememberShimmerBrush()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) { // Consistent padding
        // Shimmer for Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp), // Match LocationCategoryRow padding
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(titlePlaceholderWidthFraction)
                    .height(20.dp) // Approx height of title Text
                    .shimmerBackground()
            )
            // Optionally, shimmer for "View More" if it's always there
            // Spacer(
            //     modifier = Modifier
            //         .width(70.dp) // Approx width of "View More"
            //         .height(14.dp)
            //         .shimmerBackground()
            // )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Match LocationCategoryRow spacing

        // Shimmer for LazyRow items
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Match LocationCategoryRow spacing
        ) {
            items(itemCount) {
                ShimmerLocationCardPlaceholder()
            }
        }
    }
}
