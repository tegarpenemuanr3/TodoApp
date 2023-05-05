package com.tegarpenemuan.todoapp.model

data class MessageResponse(
    val error: Any,
    val message: Message,
    val status: Int
) {
    data class Message(
        val success: String
    )
}