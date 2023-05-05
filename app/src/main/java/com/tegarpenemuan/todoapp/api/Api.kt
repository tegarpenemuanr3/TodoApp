package com.tegarpenemuan.todoapp.api

import com.tegarpenemuan.todoapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("todos")
    fun getTodos(): Call<ArrayList<TodosResponse>>

    @GET("todos/complete")
    fun getTodosComplete(): Call<ArrayList<TodosResponse>>

    @FormUrlEncoded
    @POST("todos")
    fun createTodos(
        @Field("title") title: String,
        @Field("description") description: String
    ): Call<MessageResponse>

    @PUT("todos/{id}")
    fun updateTodos(
        @Path("id") id: Int,
        @Body request: TodosRequest
    ): Call<MessageResponse>

    @PATCH("complete/{id}")
    fun todoComplete(
        @Path("id") id: Int,
        @Body request: TodoCompleteRequest
    ): Call<TodoCompleteResponse>

    @DELETE("todos/{id}")
    fun deleteTodos(
        @Path("id") id: Int
    ): Call<MessageResponse>
}