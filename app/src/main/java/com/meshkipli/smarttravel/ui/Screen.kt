package com.meshkipli.smarttravel.ui    // NavRoutes.kt (or in a suitable navigation package)
    sealed class Screen(val route: String) {
        object TripDetails : Screen("trip_details/{tripId}") { // If you pass tripId
             fun createRoute(tripId: String) = "trip_details/$tripId"
        }
        object TripPlanner : Screen("trip_planner/{tripTitle}?description={tripDescription}&coverImageUrl={coverImageUrl}") {
            fun createRoute(
                tripTitle: String,
                tripDescription: String? = null,
                coverImageUrl: String? = null // Add coverImageUrl
            ): String {
                val encodedTitle = java.net.URLEncoder.encode(tripTitle, "UTF-8")
                val descPart = tripDescription?.let { "&description=${java.net.URLEncoder.encode(it, "UTF-8")}" } ?: ""
                val coverPart = coverImageUrl?.let { "&coverImageUrl=${java.net.URLEncoder.encode(it, "UTF-8")}" } ?: ""
                return "trip_planner/$encodedTitle?$descPart$coverPart".replace("?&", "?") // Clean up if first optional param is null
            }
        }
        // ... other screens
    }