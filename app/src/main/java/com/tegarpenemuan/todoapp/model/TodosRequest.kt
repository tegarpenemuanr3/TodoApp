package com.tegarpenemuan.todoapp.model

data class TodosRequest(
    val completed: Int,
    val description: String,
    val title: String
)