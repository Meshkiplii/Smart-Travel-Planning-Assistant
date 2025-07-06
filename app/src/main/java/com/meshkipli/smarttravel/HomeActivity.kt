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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.meshkipli.smarttravel.screens.AssistantScreen
import com.meshkipli.smarttravel.screens.HomeScreen
import com.meshkipli.smarttravel.screens.ItineraryFormScreen
import com.meshkipli.smarttravel.ui.wallet.WalletScreen
import com.meshkipli.smarttravel.ui.BottomNavItem
import com.meshkipli.smarttravel.ui.theme.SmartTravelTheme

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen() {
    val navController = androidx.navigation.compose.rememberNavController()
    val orangeColor = Color(0xFFF9882B)

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
    ) { innerPadding -> // Content of the Scaffold
       NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen()
            }
            composable(NavRoutes.ITINERARY) {
                ItineraryFormScreen()
            }
           composable(NavRoutes.WALLET) {
               WalletScreen()
           }
           composable(NavRoutes.ASSISTANT) {
               AssistantScreen()
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
