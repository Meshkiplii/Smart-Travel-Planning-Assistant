package com.meshkipli.smarttravel.ui.common

    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.* // Import all default icons for convenience
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.vector.ImageVector

    // Define the DisplayExpenseCategory data class
    data class DisplayExpenseCategory(
        val name: String,
        val icon: ImageVector,
        val color: Color
    )

    // Define the list of available categories
    val availableCategories: List<DisplayExpenseCategory> = listOf(
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

    // Utility function to get a display category, can also live here
    fun getDisplayCategory(categoryName: String, categories: List<DisplayExpenseCategory> = availableCategories): DisplayExpenseCategory {
        return categories.find { it.name.equals(categoryName, ignoreCase = true) }
            ?: categories.find { it.name.equals("Other", ignoreCase = true) } // Fallback to "Other"
            ?: DisplayExpenseCategory("Unknown", Icons.Default.HelpOutline, Color.DarkGray) // Absolute fallback
    }
    