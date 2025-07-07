package com.meshkipli.smarttravel.ui.assistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meshkipli.smarttravel.ui.assistant.components.ChatBubble
import com.meshkipli.smarttravel.ui.assistant.components.MessageInput


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    uiState: AssistantUiState, 
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    
) {


    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    
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
                
                
            ) {
                items(
                    items = uiState.messages,
                    key = { message -> message.id } 
                ) { message ->
                    ChatBubble(message = message)
                }
            }
            MessageInput(
                value = inputText,
                onValueChange = onInputTextChanged, 
                onSendClick = onSendMessage,      
                isEnabled = !uiState.AwaitingResponse
            )
        }
    }
}



@Preview(showBackground = true, name = "AI Assistant Screen - Empty", widthDp = 360, heightDp = 800)
@Composable
fun AssistantScreenPreviewEmpty() {
    MaterialTheme {
        AssistantScreen(
            uiState = AssistantUiState(), 
            inputText = "",
            onInputTextChanged = {},
            onSendMessage = {}
        )
    }
}

@Composable
fun AssistantRoute(
    viewModel: AssistantViewModel = viewModel() 
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var inputText by rememberSaveable { mutableStateOf("") }

    AssistantScreen(
        uiState = uiState,
        inputText = inputText,
        onInputTextChanged = { newText -> inputText = newText },
        onSendMessage = {
            if (inputText.isNotBlank()) {
                viewModel.sendMessage(inputText)
                inputText = "" 
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
