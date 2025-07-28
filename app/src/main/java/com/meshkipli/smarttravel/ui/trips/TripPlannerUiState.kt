package com.meshkipli.smarttravel.ui.trips // Or your ViewModel package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.meshkipli.smarttravel.BuildConfig // For API Key
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TripPlannerUiState(
    val startDate: String = "", // New
    val endDate: String = "",   // New
    val numberOfPeople: String = "1",
    val budget: String = "",
    val aiSuggestion: String? = null,
    val isLoadingAiSuggestion: Boolean = false,
    val aiError: String? = null
)

class TripPlannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TripPlannerUiState())
    val uiState: StateFlow<TripPlannerUiState> = _uiState.asStateFlow()

    private var generativeModel: GenerativeModel? = null

    init {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY // Ensure you have this in your BuildConfig
            if (apiKey.isNullOrEmpty()) {
                _uiState.update { it.copy(aiError = "API Key is missing.") }
            } else {
                generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash-latest", // Or your preferred model
                    apiKey = apiKey
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(aiError = "Error initializing AI model: ${e.localizedMessage}") }
        }
    }

    fun updateStartDate(date: String) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: String) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun updateNumberOfPeople(people: String) {
        if (people.all { char -> char.isDigit() }) {
            _uiState.update { it.copy(numberOfPeople = people) }
        }
    }

    fun updateBudget(newBudget: String) {
        if (newBudget.all { char -> char.isDigit() || char == '.' }) {
            _uiState.update { it.copy(budget = newBudget) }
        }
    }

    fun generateAiTripPlan(tripTitle: String, tripDescription: String?) {
        if (generativeModel == null) {
            _uiState.update { it.copy(aiError = "AI Model not initialized.", isLoadingAiSuggestion = false) }
            return
        }
        // Check if both start and end dates are selected
        if (_uiState.value.startDate.isEmpty() || _uiState.value.endDate.isEmpty()) {
            _uiState.update { it.copy(aiError = "Please select both a start and end date.", isLoadingAiSuggestion = false) }
            return
        }

        _uiState.update { it.copy(isLoadingAiSuggestion = true, aiError = null, aiSuggestion = null) }

        val peopleInt = _uiState.value.numberOfPeople.toIntOrNull() ?: 1
        val budgetDouble = _uiState.value.budget.toDoubleOrNull()

        val prompt = buildPrompt(
            tripTitle,
            tripDescription,
            _uiState.value.startDate,
            _uiState.value.endDate, // Pass end date to prompt
            peopleInt,
            budgetDouble
        )

        viewModelScope.launch {
            try {
                val response: GenerateContentResponse = generativeModel!!.generateContent(prompt)
                _uiState.update {
                    it.copy(
                        aiSuggestion = response.text,
                        isLoadingAiSuggestion = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        aiError = "Error generating AI plan: ${e.localizedMessage}",
                        isLoadingAiSuggestion = false
                    )
                }
            }
        }
    }
    private fun buildPrompt(
        title: String,
        description: String?,
        startDate: String,
        endDate: String, // Add endDate parameter
        people: Int,
        budget: Double?
    ): String {
        val descPart = if (!description.isNullOrBlank()) "It's described as: \"$description\"." else ""
        val budgetPart = if (budget != null && budget > 0) "with an approximate budget of $${String.format("%.2f", budget)}" else "with no specific budget."
        val datePart = if (startDate == endDate) "on $startDate" else "from $startDate to $endDate"

        return """
            You are a helpful travel planning assistant.
            I need a travel plan suggestion for a trip to "$title".
            $descPart
            The trip is planned $datePart, for $people person(s), $budgetPart.

            Please provide a concise and actionable travel plan suggestion.
            If the duration is multiple days, suggest a high-level itinerary or key activities for the period.
            If it's a single day, focus on that.
            Make it sound exciting and helpful!
        """.trimIndent()
    }

    fun clearAiError() {
        _uiState.update { it.copy(aiError = null) }
    }

    fun clearAiSuggestion() {
         _uiState.update { it.copy(aiSuggestion = null) }
    }
}
