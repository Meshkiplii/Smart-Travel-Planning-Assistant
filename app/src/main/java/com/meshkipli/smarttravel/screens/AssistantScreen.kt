package com.meshkipli.smarttravel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- Data Models ---

enum class Author {
    USER, ASSISTANT
}

data class ChatMessage(
    val author: Author,
    val content: AnnotatedString
)

// --- Mock Data ---

fun getMockChatHistory(): List<ChatMessage> {
    return listOf(
        ChatMessage(
            author = Author.ASSISTANT,
            content = buildAnnotatedString {
                append("Hello! I'm your travel assistant. How can I help you plan your next trip today?")
            }
        ),
        ChatMessage(
            author = Author.USER,
            content = buildAnnotatedString {
                append("What are some must-visit places in Sydney, Australia?")
            }
        ),
        ChatMessage(
            author = Author.ASSISTANT,
            content = buildAnnotatedString {
                append("Of course! Sydney is a fantastic city. For a first-time visitor, I'd recommend:\n\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("• Sydney Opera House & Harbour Bridge:")
                }
                append(" Classic icons, you can walk around or take a guided tour.\n\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("• Bondi Beach:")
                }
                append(" Famous for its surf, sand, and the beautiful coastal walk to Coogee.\n\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("• The Rocks:")
                }
                append(" A historic area with cobblestone streets, pubs, and markets.")
            }
        ),
        ChatMessage(
            author = Author.USER,
            content = buildAnnotatedString {
                append("That sounds great! How about food recommendations near The Rocks?")
            }
        ),
    )
}


// --- Chat Screen Components ---

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.author == Author.USER) Color(0xFFF9882B) else Color(0xFFF0F0F0)
    val textColor = if (message.author == Author.USER) Color.White else Color.Black
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
        if (message.author == Author.ASSISTANT) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = "AI Assistant Avatar",
                    tint = Color(0xFFF9882B),
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
                Text(
                    text = message.content,
                    color = textColor,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
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
                placeholder = { Text("Ask me anything...") },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA),
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank()) Color(0xFFF9882B) else Color.LightGray)
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

// --- Main Assistant Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen() {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(getMockChatHistory()) } }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val handleSend = {
        if (inputText.isNotBlank()) {
            val userMessage = ChatMessage(
                author = Author.USER,
                content = buildAnnotatedString { append(inputText) }
            )
            messages.add(userMessage)
            inputText = ""
            // Scroll to the newest message
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assistant", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp) // Removes extra padding
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
                modifier = Modifier.weight(1f),
                state = listState,
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(message = message)
                }
            }
            MessageInput(
                value = inputText,
                onValueChange = { inputText = it },
                onSendClick = handleSend
            )
        }
    }
}


// --- Preview ---

@Preview(showBackground = true, name = "AI Assistant Screen", widthDp = 360, heightDp = 800)
@Composable
fun AssistantScreenPreview() {
    MaterialTheme {
        AssistantScreen()
    }
}