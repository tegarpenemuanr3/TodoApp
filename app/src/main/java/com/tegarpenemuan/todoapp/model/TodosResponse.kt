package com.tegarpenemuan.todoapp.model

data class TodosResponse(
    val completed: String,
    val created_at: String,
    val description: String,
    val id: String,
    val title: String,
    val updated_at: String
)
