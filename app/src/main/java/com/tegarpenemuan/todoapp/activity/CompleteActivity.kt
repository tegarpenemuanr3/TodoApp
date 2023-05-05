package com.tegarpenemuan.todoapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tegarpenemuan.todoapp.R
import com.tegarpenemuan.todoapp.adapter.Adapter
import com.tegarpenemuan.todoapp.api.RetrofitClient
import com.tegarpenemuan.todoapp.model.TodoCompleteRequest
import com.tegarpenemuan.todoapp.model.TodoCompleteResponse
import com.tegarpenemuan.todoapp.model.TodosResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompleteActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var imageNoData: ImageView
    private lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        supportActionBar?.hide()

        ivBack = findViewById(R.id.ivback)
        ivBack.setOnClickListener {
            finish()
        }
        imageNoData = findViewById(R.id.iv_no_data)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener(this)
        setupRecylerView()
        getTodosComplete()
    }

    private fun getTodosComplete() {
        RetrofitClient.instance.getTodosComplete()
            .enqueue(object : Callback<ArrayList<TodosResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<TodosResponse>>,
                    response: Response<ArrayList<TodosResponse>>
                ) {
                    val responseBody = response.body()
                    Log.d("MainActivity", responseBody!!.toString())
                    if (responseBody.isEmpty()) {
                        imageNoData.visibility = View.VISIBLE
                    } else {
                        imageNoData.visibility = View.GONE
                    }
                    recyclerView.adapter =
                        Adapter(
                            this@CompleteActivity,
                            responseBody,
                            object : Adapter.EventListener {
                                override fun onLongClick(data: TodosResponse) {}
                                override fun onClick(data: TodosResponse) {}
                                override fun onItemClick(
                                    data: TodosResponse,
                                    position: Int,
                                    isChecked: Boolean
                                ) {
                                    if (!isChecked) {
                                        RetrofitClient.instance.todoComplete(
                                            data.id.toInt(),
                                            TodoCompleteRequest(completed = 0)
                                        ).enqueue(object : Callback<TodoCompleteResponse> {
                                            override fun onResponse(
                                                call: Call<TodoCompleteResponse>,
                                                response: Response<TodoCompleteResponse>
                                            ) {
                                                onRefresh()
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Todo Complete",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }

                                            override fun onFailure(
                                                call: Call<TodoCompleteResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    t.message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        })
                                    }
                                }
                            })
                    swipeRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<ArrayList<TodosResponse>>, t: Throwable) {
                    Log.e("MainActivity", t.toString())
                }
            })
    }

    private fun setupRecylerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false
    }

    override fun onRefresh() {
        getTodosComplete()
    }
}