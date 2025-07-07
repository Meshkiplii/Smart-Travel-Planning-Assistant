package com.meshkipli.smarttravel.ui.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Changed from items(uiState.messages.reversed())
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ErrorOutline // For error messages
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Data models and Author enum can be moved to ViewModel file or a separate data file if preferred
// For simplicity, keeping them here for now if they are only used by this screen.
// If AssistantViewModel is in a different package, ensure Author and ChatMessage are accessible.

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = when (message.author) {
        Author.USER -> Color(0xFFF9882B) // Your original user color
        Author.ASSISTANT -> Color(0xFFF0F0F0) // Your original assistant color
        Author.ERROR -> Color(0xFFD32F2F) // Error color
    }
    val textColor = when (message.author) {
        Author.USER -> Color.White
        Author.ASSISTANT -> Color.Black
        Author.ERROR -> Color.White
    }
    val alignment = if (message.author == Author.USER) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (message.author == Author.USER) {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = if (message.author == Author.USER) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (message.author == Author.ASSISTANT || message.author == Author.ERROR) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (message.author == Author.ERROR) Color.DarkGray.copy(alpha = 0.4f)
                        else Color.LightGray.copy(alpha = 0.4f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (message.author == Author.ERROR) Icons.Default.ErrorOutline else Icons.Default.SmartToy,
                    contentDescription = if (message.author == Author.ERROR) "Error Icon" else "AI Assistant Avatar",
                    tint = if (message.author == Author.ERROR) Color(0xFFD32F2F) else Color(0xFFF9882B),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f, fill = false) // Important for wrapping content
        ) {
            Surface(
                shape = bubbleShape,
                color = bubbleColor,
                modifier = Modifier.align(alignment)
            ) {
                if (message.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        color = textColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = message.content,
                        color = textColor,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 22.sp
                    )
                }
            }
        }
        if (message.author == Author.USER) { // Add spacer for user messages if needed for balance
            Spacer(modifier = Modifier.width(48.dp)) // Corresponds to avatar + spacer width
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isEnabled: Boolean // To disable input while waiting for response
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (isEnabled) "Ask me anything..." else "Assistant is thinking...") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA),
                ),
                enabled = isEnabled
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank() && isEnabled,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank() && isEnabled) Color(0xFFF9882B) else Color.LightGray)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    uiState: AssistantUiState, // Pass state directly
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    // Add any other necessary event lambdas
) {
//    val uiState by viewModel.uiState.collectAsState()
//    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to the bottom when a new message arrives or keyboard opens
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assistant", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                state = listState,
                // reverseLayout = true, // Set to false for standard top-to-bottom chat
                // items are added to the end of the list now
            ) {
                items(
                    items = uiState.messages,
                    key = { message -> message.id } // Use unique ID as key
                ) { message ->
                    ChatBubble(message = message)
                }
            }
            MessageInput(
                value = inputText,
                onValueChange = onInputTextChanged, // Use hoisted state and event
                onSendClick = onSendMessage,      // Use hoisted event
                isEnabled = !uiState.AwaitingResponse
            )
        }
    }
}


// Then in your Previews:
@Preview(showBackground = true, name = "AI Assistant Screen - Empty", widthDp = 360, heightDp = 800)
@Composable
fun AssistantScreenPreviewEmpty() {
    MaterialTheme {
        AssistantScreen(
            uiState = AssistantUiState(), // Provide a default empty state
            inputText = "",
            onInputTextChanged = {},
            onSendMessage = {}
        )
    }
}

@Composable
fun AssistantRoute(
    viewModel: AssistantViewModel = viewModel() // Gets the ViewModel instance
) {
    val uiState by viewModel.uiState.collectAsState()
    // Hoist inputText state here, making AssistantRoute the source of truth for it at this level
    var inputText by rememberSaveable { mutableStateOf("") }

    AssistantScreen(
        uiState = uiState,
        inputText = inputText,
        onInputTextChanged = { newText -> inputText = newText },
        onSendMessage = {
            if (inputText.isNotBlank()) {
                viewModel.sendMessage(inputText)
                inputText = "" // Clear input after sending, or ViewModel can handle this via state update
            }
        }
    )
}

@Preview(showBackground = true, name = "AI Assistant Screen - With Messages", widthDp = 360, heightDp = 800)
@Composable
fun AssistantScreenPreviewWithMessages() {
    MaterialTheme {
        AssistantScreen(
            uiState = AssistantUiState(
                messages = listOf(
                    ChatMessage(author = Author.ASSISTANT, content = "Hello! How can I help?"),
                    ChatMessage(author = Author.USER, content = "What's the weather like?"),
                    ChatMessage(author = Author.ASSISTANT, content = "", isLoading = true)
                ),
                AwaitingResponse = true
            ),
            inputText = "What's the weather like?",
            onInputTextChanged = {},
            onSendMessage = {}
        )
    }
}
