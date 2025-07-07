package com.meshkipli.smarttravel.ui.assistant // Or your ViewModel package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.meshkipli.smarttravel.BuildConfig // Import your BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// Data models (can remain in AssistantScreen.kt or be moved here/to a separate file)
enum class Author {
    USER, ASSISTANT, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // Add unique ID for stable LazyColumn
    val author: Author,
    val content: String,
    val isLoading: Boolean = false // To show a loading indicator for assistant messages
)

data class AssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val AwaitingResponse: Boolean = false
)

class AssistantViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    private lateinit var generativeModel: GenerativeModel
    // Define the system prompt
    private val systemPromptText = """
        You are "SmartTravel AI", a highly knowledgeable and enthusiastic virtual tour agent.
        Your primary goal is to assist users in planning their perfect travel experiences.
        You are an expert in finding the best tour destinations, hotels, flights, activities, and local attractions worldwide.

        Your persona:
        - Professional and Courteous: Always greet users politely and maintain a professional demeanor.
        - Enthusiastic and Passionate: Show genuine excitement about travel and discovering new places.
        - Helpful and Proactive: Offer suggestions, ask clarifying questions to better understand user needs, and provide comprehensive information.
        - Resourceful: You can suggest real-world places, types of accommodations, and activities.
        - Safety-conscious: Briefly mention considering safety or travel advisories for destinations if appropriate.

        What you should do:
        - Understand user queries related to travel planning (e.g., "best beaches in Thailand," "cheap hotels in Paris," "family-friendly resorts in Italy," "adventure tours in Costa Rica").
        - Provide detailed and relevant information based on the user's request.
        - Offer multiple options when appropriate (e.g., different types of hotels, various destinations fitting criteria).
        - Ask follow-up questions to narrow down preferences (e.g., "What's your approximate budget?", "Are you looking for a relaxing trip or something more adventurous?", "When are you planning to travel?").
        - If you don't know something specific, politely state that you can't find that exact detail but can help with other aspects or provide general advice.
        - Maintain a conversational flow.

        What you should NOT do:
        - Do not make actual bookings or reservations (state this clearly if a user asks). You are an assistant for planning and information.
        - Do not provide real-time availability or exact pricing, as this data changes rapidly. Instead, suggest where users can find this information (e.g., "You can check popular booking websites for current prices and availability.").
        - Do not go off-topic from travel planning unless the user explicitly steers the conversation.
        - Do not use offensive language or engage in harmful discussions.

        Start by greeting the user and asking how you can help them plan their next adventure.
    """.trimIndent()

    private val systemInstructionContent: Content = content(role = "system") { text(systemPromptText) }

    init {
        try {
            // For text-only input, use the gemini-pro model
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash-latest", // Or another suitable model
                apiKey = BuildConfig.GEMINI_API_KEY, // Use the key from BuildConfig
                systemInstruction = systemInstructionContent
                // You can add safetySettings and generationConfig here if needed
            )
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage(
                        author = Author.ERROR,
                        content = "Error initializing AI model: ${e.localizedMessage}"
                    )
                )
            }
        }
    }

    fun sendMessage(userInput: String) {
        if (!::generativeModel.isInitialized) {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage(
                        author = Author.ERROR,
                        content = "AI Model not initialized. Please check API key and configuration."
                    )
                )
            }
            return
        }

        // Add user message to UI
        val userMessage = ChatMessage(author = Author.USER, content = userInput)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                AwaitingResponse = true
            )
        }

        // Add a temporary loading message for the assistant
        val loadingMessageId = UUID.randomUUID().toString()
        val loadingAssistantMessage = ChatMessage(
            id = loadingMessageId,
            author = Author.ASSISTANT,
            content = "", // Empty content while loading
            isLoading = true
        )
        _uiState.update {
            it.copy(messages = it.messages + loadingAssistantMessage)
        }

        viewModelScope.launch {
            try {
                // History should now implicitly include the systemInstruction if set during model initialization
                // Or, explicitly pass it if systemInstruction wasn't used in GenerativeModel constructor:
                val currentMessages = _uiState.value.messages
                    .filter { !it.isLoading && it.author != Author.ERROR }
                    .map {
                        content(role = if (it.author == Author.USER) "user" else "model") { text(it.content) }
                    }
                    .dropLast(1) // Drop the current user message being sent

                // Option A: If systemInstruction was set in GenerativeModel constructor, this is usually enough.
                val chat = generativeModel.startChat(history = currentMessages)

                // Option B: If systemInstruction was NOT set in GenerativeModel constructor,
                // and you need to ensure it's the first message in every new chat session from this VM.
                // val chatHistory = mutableListOf(systemInstructionContent) // Or systemMessageForHistory
                // chatHistory.addAll(currentMessages)
                // val chat = generativeModel.startChat(history = chatHistory)


                val response = chat.sendMessage(userInput)

                _uiState.update { currentState ->
                    val updatedMessages = currentState.messages.mapNotNull { msg ->
                        if (msg.id == loadingMessageId) null
                        else msg
                    }.toMutableList()

                    response.text?.let { assistantResponse ->
                        updatedMessages.add(ChatMessage(author = Author.ASSISTANT, content = assistantResponse))
                    } ?: run {
                        updatedMessages.add(ChatMessage(author = Author.ERROR, content = "Assistant response was empty."))
                    }
                    currentState.copy(messages = updatedMessages, AwaitingResponse = false)
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    val updatedMessages = currentState.messages.mapNotNull { msg ->
                        if (msg.id == loadingMessageId) null
                        else msg
                    }.toMutableList()
                    updatedMessages.add(ChatMessage(author = Author.ERROR, content = "Error: ${e.localizedMessage}"))
                    currentState.copy(messages = updatedMessages, AwaitingResponse = false)
                }
            }
        }
    }
}
