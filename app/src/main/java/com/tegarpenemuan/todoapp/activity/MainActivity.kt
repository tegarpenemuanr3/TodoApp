package com.tegarpenemuan.todoapp.activity

import androidx.activity.OnBackPressedCallback
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tegarpenemuan.todoapp.api.RetrofitClient
import com.tegarpenemuan.todoapp.adapter.Adapter
import com.tegarpenemuan.todoapp.R
import com.tegarpenemuan.todoapp.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var imageNoData: ImageView
    private lateinit var ivCheck: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        validateExit()

        imageNoData = findViewById(R.id.iv_no_data)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener(this)
        setupRecylerView()
        getTodos()

        fab = findViewById(R.id.btFab)
        fab.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("status", "tambah")
            startActivity(intent)
        }

        ivCheck = findViewById(R.id.iv_check)
        ivCheck.setOnClickListener {
            val intent = Intent(this, CompleteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateExit() {
        // Create a callback for the back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show a confirmation dialog or handle the back press as needed
                showConfirmationDialog()
            }
        }

        // Add the callback to the activity's back stack
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onStart() {
        super.onStart()
        getTodos()
    }

    private fun getTodos() {
        RetrofitClient.instance.getTodos().enqueue(object : Callback<ArrayList<TodosResponse>> {
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
                    Adapter(this@MainActivity, responseBody, object : Adapter.EventListener {
                        override fun onLongClick(data: TodosResponse) {
                            dialogAksi(data)
                        }

                        override fun onClick(data: TodosResponse) {
                            toForm(data)
                        }

                        override fun onItemClick(
                            data: TodosResponse,
                            position: Int,
                            isChecked: Boolean
                        ) {
                            if (isChecked) {
                                RetrofitClient.instance.todoComplete(
                                    data.id.toInt(),
                                    TodoCompleteRequest(completed = 1)
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

    private fun toForm(data: TodosResponse) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra("status", "detail")
        intent.putExtra("title", data.title)
        intent.putExtra("desc", data.description)
        startActivity(intent)
    }


    private fun setupRecylerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onRefresh() {
        getTodos()
    }

    private fun dialogAksi(data: TodosResponse) {
        AlertDialog.Builder(this)
            .setTitle("")
            .setMessage("Pilih aksi yang akan dilakukan?")
            .setPositiveButton("Hapus") { dialog, _ ->
                dialogDelete(data)
                dialog.dismiss()
            }
            .setNegativeButton("Ubah") { dialog, _ ->
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("status", "ubah")
                intent.putExtra("id", data.id)
                intent.putExtra("title", data.title)
                intent.putExtra("desc", data.description)
                startActivity(intent)
                dialog.dismiss()
            }
            .show()
    }

    private fun dialogDelete(data: TodosResponse) {
        AlertDialog.Builder(this)
            .setTitle("")
            .setMessage("Yakin hapus data Todo ???")
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton("Hapus") { dialog, _ ->
                deleteTodos(data.id.toInt())
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTodos(id: Int) {
        RetrofitClient.instance.deleteTodos(id).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        response.body()!!.message.success,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    onRefresh()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        })
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setMessage("Yakin keluar dari aplikasi?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                finish() // This will exit the app
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}