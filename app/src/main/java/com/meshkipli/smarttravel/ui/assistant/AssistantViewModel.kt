package com.meshkipli.smarttravel.ui.assistant // Or your ViewModel package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
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

    init {
        try {
            // For text-only input, use the gemini-pro model
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash-latest", // Or another suitable model
                apiKey = BuildConfig.GEMINI_API_KEY // Use the key from BuildConfig
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
                // Construct the chat history for the model
                // The Gemini API works best with alternating user/model roles.
                // We'll simplify here by just sending the current user input,
                // but for a conversational experience, you'd send more history.
                val chat = generativeModel.startChat(
                    history = _uiState.value.messages
                        .filter { !it.isLoading && it.author != Author.ERROR } // Exclude loading/error messages
                        .map {
                            content(role = if (it.author == Author.USER) "user" else "model") {
                                text(it.content)
                            }
                        }
                        .dropLast(1) // Drop the current user message as it will be sent via send()
                )

                val response = chat.sendMessage(userInput)

                // Remove loading message and add actual response
                _uiState.update { currentState ->
                    val updatedMessages = currentState.messages.mapNotNull { msg ->
                        if (msg.id == loadingMessageId) null // Remove loading
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
                // Remove loading message and add error message
                _uiState.update { currentState ->
                    val updatedMessages = currentState.messages.mapNotNull { msg ->
                        if (msg.id == loadingMessageId) null // Remove loading
                        else msg
                    }.toMutableList()
                    updatedMessages.add(ChatMessage(author = Author.ERROR, content = "Error: ${e.localizedMessage}"))
                    currentState.copy(messages = updatedMessages, AwaitingResponse = false)
                }
            }
        }
    }
}
