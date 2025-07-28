package com.meshkipli.smarttravel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.meshkipli.smarttravel.ui.home.HomeScreen
import com.meshkipli.smarttravel.ui.itinerary.ItineraryFormScreen
import com.meshkipli.smarttravel.ui.wallet.WalletScreen
import com.meshkipli.smarttravel.ui.BottomNavItem
import com.meshkipli.smarttravel.ui.Screen
import com.meshkipli.smarttravel.ui.assistant.AssistantRoute
import com.meshkipli.smarttravel.ui.itinerary.AddItineraryScreen
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme
import com.meshkipli.smarttravel.ui.tourdetails.TourDetailsScreen
import com.meshkipli.smarttravel.ui.trips.TripDetailsScreen
import com.meshkipli.smarttravel.ui.trips.TripPlannerScreen
import com.meshkipli.smarttravel.ui.trips.TripsScreen

class HomeActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Add if Scaffold complains
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTravelTheme {
                MainNavigationScreen()
            }
        }
    }
}

// Define navigation routes
object NavRoutes {
    const val HOME = "home"
    const val ITINERARY = "itinerary"
    const val WALLET = "wallet"
    const val ASSISTANT = "assistant"
    const val ADD_ITINERARY_ROUTE = "addItinerary"
    const val TOUR_DETAILS_ROUTE = "tourDetail"
    const val TRIPS_ROUTE = "trips"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen() {
    val navController = androidx.navigation.compose.rememberNavController()
    val orangeColor = Color(0xFFF9882B)

    val bottomBarVisibleRoutes = setOf(
        NavRoutes.HOME,
        NavRoutes.ITINERARY,
        NavRoutes.WALLET,
        NavRoutes.ASSISTANT
    )

    // Observe the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if the bottom bar should be shown
    val shouldShowBottomBar = currentRoute in bottomBarVisibleRoutes


    val navItems = listOf(
        BottomNavItem(
            label = "Home",
            route = NavRoutes.HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            label = "Wallet",
            route = NavRoutes.WALLET,
            selectedIcon = Icons.Filled.AccountBalanceWallet,
            unselectedIcon = Icons.Outlined.AccountBalanceWallet
        ),
        BottomNavItem(
            label = "Guide",
            route = NavRoutes.ITINERARY,
            selectedIcon = Icons.Filled.Explore,
            unselectedIcon = Icons.Outlined.Explore
        ),
        BottomNavItem(
            label = "Assistant",
            route = NavRoutes.ASSISTANT,
            selectedIcon = Icons.Filled.SupportAgent,
            unselectedIcon = Icons.Outlined.SupportAgent
        )

    )

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) { // Conditionally render the NavigationBar
                NavigationBar(
                    containerColor = Color.White.copy(alpha = 0.95f), // Consider using MaterialTheme.colorScheme.surface
                    tonalElevation = 8.dp
                ) {
                    val currentDestination = navBackStackEntry?.destination // Already have this from currentRoute logic

                    navItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
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
        }
    ) { innerPadding -> // Content of the Scaffold
       NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen(
                    onNavigateToTrips= {
                        navController.navigate(NavRoutes.TRIPS_ROUTE)

                    },
                     onNavigateToTourDetails = { tourId ->
                        navController.navigate("${NavRoutes.TOUR_DETAILS_ROUTE}/$tourId")
                    }
                )
            }
//
           composable(NavRoutes.WALLET) {
               WalletScreen()
           }
           composable(NavRoutes.ASSISTANT) {
               AssistantRoute()
           }
           composable(NavRoutes.ITINERARY) {
               ItineraryFormScreen(
                   onNavigateToAddItinerary = {
                       navController.navigate(NavRoutes.ADD_ITINERARY_ROUTE)
                   }
                   // Pass your ItineraryViewModel if ItineraryFormScreen needs it directly
                   // or if it's scoped differently
               )
           }
           composable(NavRoutes.ADD_ITINERARY_ROUTE) {
               // Assuming AddItineraryScreen takes the ViewModel and a navigate back lambda
               AddItineraryScreen(
                   onNavigateBack = { navController.popBackStack() }
                   // ViewModel for AddItineraryScreen will be created within it
                   // using its own factory, or you can pass it if it's shared and scoped appropriately
               )
           }
           composable("trips") {
               TripsScreen(
                   onNavigateBack = { navController.popBackStack()},
                   onNavigateToTourDetails = { tourId ->
                       navController.navigate("${NavRoutes.TOUR_DETAILS_ROUTE}/$tourId")
                   }
               )
           }
           composable(
               route = "tourDetail/{tourId}",
               arguments = listOf(navArgument("tourId") { type = NavType.StringType })
           ) { backStackEntry ->
               val tourId = backStackEntry.arguments?.getString("tourId")
               if (tourId != null) {
                   TourDetailsScreen(
                       tourId = tourId,
                       onNavigateBack = { navController.popBackStack() },
                       onPlanTripClicked = { id, title, description, coverUrl ->

                           navController.navigate(
                               Screen.TripPlanner.createRoute(
                                   tripTitle = title,
                                   tripDescription = description,
                                   coverImageUrl = coverUrl // pass it differently. For now, assuming title/desc are sufficient.
                               )
                           )
                       }
                   )
               } else {
                   // Handle error: tourId not found (should not happen if navigation is correct)
                   Text("Error: Tour ID missing.")
               }
           }
           composable(
               route = Screen.TripPlanner.route,
               arguments = listOf(
                   navArgument("tripTitle") { type = NavType.StringType },
                   navArgument("tripDescription") {
                       type = NavType.StringType
                       nullable = true
                       defaultValue = null
                   },
                   navArgument("coverImageUrl") { // Add argument for cover image
                       type = NavType.StringType
                       nullable = true
                       defaultValue = null
                   }
               )
           ) { backStackEntry ->
               val encodedTripTitle = backStackEntry.arguments?.getString("tripTitle")
               val tripTitle = encodedTripTitle?.let { java.net.URLDecoder.decode(it, "UTF-8") }

               val encodedTripDescription = backStackEntry.arguments?.getString("tripDescription")
               val tripDescription = encodedTripDescription?.let { java.net.URLDecoder.decode(it, "UTF-8") }

               val encodedCoverImageUrl = backStackEntry.arguments?.getString("coverImageUrl")
               val coverImageUrl = encodedCoverImageUrl?.let { java.net.URLDecoder.decode(it, "UTF-8") }

               TripPlannerScreen(
                   tripTitle = tripTitle,
                   tripDescription = tripDescription,
                   coverImageUrl = coverImageUrl, // Pass it here
                   onNavigateBack = { navController.popBackStack() },
                   viewModel = viewModel() // Ensure ViewModel is correctly provided if needed
               )
           }
        }
    }
}

// Preview for the MainNavigationScreen
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartTravelTheme {
        MainNavigationScreen()
    }
}
