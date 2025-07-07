package com.meshkipli.smarttravel.ui.assistant.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.meshkipli.smarttravel.ui.assistant.Author
import com.meshkipli.smarttravel.ui.assistant.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = when (message.author) {
        Author.USER -> Color(0xFFF9882B)
        Author.ASSISTANT -> Color(0xFFF0F0F0) 
        Author.ERROR -> Color(0xFFD32F2F) 
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
                .weight(1f, fill = false) 
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
                    CompositionLocalProvider(
                        LocalContentColor provides textColor,
                        // You might need to adjust LocalTextStyle if the library doesn't pick it up
                        // LocalTextStyle provides MaterialTheme.typography.bodyLarge.copy(color = textColor)
                    ) {
                        RichText(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // The Markdown composable parses and prepares the content
                            Markdown(
                                content = message.content,
                                // You can provide custom renderers here if needed
                                // commonmarkFeatures = CommonmarkAstNodeParser.DEFAULT_FEATURES,
                                // onLinkClicked = { /* handle link clicks */ }
                            )
                        }
                    }
                }
            }
        }
        if (message.author == Author.USER) { 
            Spacer(modifier = Modifier.width(48.dp)) 
        }
    }
}

