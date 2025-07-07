package com.meshkipli.smarttravel.ui.assistant // Or your ViewModel package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.meshkipli.smarttravel.BuildConfig
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

        When the conversation starts, provide a friendly greeting and ask how you can help plan their next adventure.
    """.trimIndent() // Modified the last line to be more direct for an initial message

    private val systemInstructionContent: Content = content(role = "system") { text(systemPromptText) }

    init {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty()) {
            _uiState.update {
                it.copy(
                    messages = it.messages + ChatMessage(
                        author = Author.ERROR,
                        content = "API Key is missing. Please check your configuration."
                    ),
                    AwaitingResponse = false // Not awaiting if there's a config error
                )
            }
        } else {
            try {
                generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash-latest",
                    apiKey = apiKey,
                    systemInstruction = systemInstructionContent
                )
                // Trigger the initial greeting from the AI
                fetchInitialGreeting()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            author = Author.ERROR,
                            content = "Error initializing AI model: ${e.localizedMessage}."
                        ),
                        AwaitingResponse = false
                    )
                }
            }
        }
    }

    private fun fetchInitialGreeting() {
        if (!::generativeModel.isInitialized) return

        // Add a temporary loading message for the initial greeting
        val loadingMessageId = UUID.randomUUID().toString()
        val loadingGreetingMessage = ChatMessage(
            id = loadingMessageId,
            author = Author.ASSISTANT,
            content = "", // Empty content while loading
            isLoading = true
        )
        _uiState.update {
            it.copy(
                messages = listOf(loadingGreetingMessage), // Start with only the loading message
                AwaitingResponse = true
            )
        }

        viewModelScope.launch {
            try {
                // For a truly fresh start based *only* on the system prompt, send empty history
                // or a minimal user prompt to kickstart the conversation.
                // The systemInstruction is already part of the model's configuration.
                val chat = generativeModel.startChat(history = emptyList())
                // Sending a very simple, almost placeholder message from the "user"
                // can encourage the model to respond according to its system instructions.
                // Or, for some models, generateContent might be better if you don't want a "user" turn.
                val response: GenerateContentResponse = chat.sendMessage("Hello") // or even an empty string if allowed ""

                // Remove loading message and add actual greeting
                _uiState.update { currentState ->
                    val greetingMessage = response.text?.let { greeting ->
                        ChatMessage(author = Author.ASSISTANT, content = greeting)
                    } ?: ChatMessage(author = Author.ERROR, content = "Assistant did not provide an initial greeting.")

                    currentState.copy(
                        messages = listOf(greetingMessage), // Replace loading with the actual greeting
                        AwaitingResponse = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    val errorMessage = ChatMessage(author = Author.ERROR, content = "Error fetching initial greeting: ${e.localizedMessage}")
                    currentState.copy(
                        messages = listOf(errorMessage), // Replace loading with error
                        AwaitingResponse = false
                    )
                }
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

        val userMessage = ChatMessage(author = Author.USER, content = userInput)
        // Ensure initial greeting (if it was loading) is replaced or history is clean before adding new user message
        val messagesWithoutInitialLoading = _uiState.value.messages.filter { !it.isLoading }

        _uiState.update {
            it.copy(
                messages = messagesWithoutInitialLoading + userMessage,
                AwaitingResponse = true
            )
        }

        val loadingMessageId = UUID.randomUUID().toString()
        val loadingAssistantMessage = ChatMessage(
            id = loadingMessageId,
            author = Author.ASSISTANT,
            content = "",
            isLoading = true
        )
        _uiState.update {
            it.copy(messages = it.messages + loadingAssistantMessage)
        }

        viewModelScope.launch {
            try {
                val chatHistory = _uiState.value.messages
                    .filter { !it.isLoading && it.author != Author.ERROR } // Exclude current loading & errors
                    .dropLast(1) // Drop the current user message being sent, it's the `userInput`
                    .map { message ->
                        content(role = if (message.author == Author.USER) "user" else "model") { text(message.content) }
                    }

                val chat = generativeModel.startChat(history = chatHistory)
                val response = chat.sendMessage(userInput)

                _uiState.update { currentState ->
                    val updatedMessages = currentState.messages.mapNotNull { msg ->
                        if (msg.id == loadingMessageId) null // Remove assistant's loading message
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