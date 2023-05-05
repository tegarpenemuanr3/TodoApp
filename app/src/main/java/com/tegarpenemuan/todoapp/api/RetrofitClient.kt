package com.tegarpenemuan.todoapp.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "http://192.168.195.140/todo-app/public/api/"
//    private const val BASE_URL = "http://todo-app.infinityfreeapp.com/api/"
//    private const val BASE_URL = "http://10.0.2.2/todo-app/public/api/"

    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(Api::class.java)
    }
}