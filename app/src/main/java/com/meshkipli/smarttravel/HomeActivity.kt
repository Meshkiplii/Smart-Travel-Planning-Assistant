package com.meshkipli.smarttravel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.meshkipli.smarttravel.screens.HomeScreen // Assuming HomeScreen is in this package
import com.meshkipli.smarttravel.screens.ItineraryFormScreen// Assuming ItineraryScreen is here
import com.meshkipli.smarttravel.ui.BottomNavItem // Your BottomNavItem class
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme // Your app theme

class HomeActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Add if Scaffold complains
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme { // Or your specific app theme
                MainNavigationScreen()
            }
        }
    }
}

// Define navigation routes
object NavRoutes {
    const val HOME = "home"
    const val ITINERARY = "itinerary"
    // Add other routes here if needed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen() {
    val navController = androidx.navigation.compose.rememberNavController()
    val orangeColor = Color(0xFFF9882B) // Define your accent color

    val navItems = listOf(
        BottomNavItem(
            label = "Home",
            route = NavRoutes.HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            label = "Itinerary",
            route = NavRoutes.ITINERARY,
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = orangeColor,
                            selectedTextColor = orangeColor,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White // Or a very light orange or transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding -> // Content of the Scaffold
       NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME, // Default screen
            modifier = Modifier.padding(innerPadding) // Apply padding from Scaffold
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen() // Your existing HomeScreen composable
            }
            composable(NavRoutes.ITINERARY) {
                ItineraryFormScreen() // Your new ItineraryScreen composable
            }
            // Add other destinations here
        }
    }
}

// Preview for the MainNavigationScreen (optional but helpful)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartTravelTheme {
        MainNavigationScreen()
    }
}

// Make sure your HomeScreen composable definition exists
// (You likely have it already from previous steps)
// Example:
// @Composable
// fun HomeScreen() { /* ... your home screen content ... */ }