package com.tegarpenemuan.todoapp.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.tegarpenemuan.todoapp.api.RetrofitClient
import com.tegarpenemuan.todoapp.model.MessageResponse
import com.tegarpenemuan.todoapp.R
import com.tegarpenemuan.todoapp.model.TodosRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDesc: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnUpdate: Button
    private lateinit var tvTitleForm: TextView
    private lateinit var ivBack: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.hide()

        tvTitleForm = findViewById(R.id.title_form)
        editTitle = findViewById(R.id.et_todo_title)
        editDesc = findViewById(R.id.et_todo_desc)
        btnCreate = findViewById(R.id.btn_create)
        btnUpdate = findViewById(R.id.btn_update)
        ivBack = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            finish()
        }

        when (intent.getStringExtra("status")) {
            "ubah" -> {
                btnUpdate.visibility = View.VISIBLE
                editTitle.setText(intent.getStringExtra("title"))
                editDesc.setText(intent.getStringExtra("desc"))
                tvTitleForm.text = "Update Data"
                buttonUpdate()
            }
            "tambah" -> {
                btnCreate.visibility = View.VISIBLE
                tvTitleForm.text = "Create Data"
                buttonCreate()
            }
            "detail" -> {
                tvTitleForm.text = "Detail Data"
                editTitle.setText(intent.getStringExtra("title"))
                editDesc.setText(intent.getStringExtra("desc"))
            }
        }


    }

    private fun buttonUpdate() {
        btnUpdate.setOnClickListener {
            val id = intent.getStringExtra("id")
            updateTodos(id!!.toInt(), editTitle.text.toString(), editDesc.text.toString())
        }
    }

    private fun buttonCreate() {
        btnCreate.setOnClickListener {
//            Toast.makeText(
//                applicationContext,
//                editTitle.text.toString() + editDesc.text.toString(),
//                Toast.LENGTH_SHORT
//            ).show()

            if (editTitle.text.toString().isEmpty()) {
                editTitle.error = "Title Required"
                editTitle.requestFocus()
                return@setOnClickListener
            } else {
                createTodos(editTitle.text.toString(), editDesc.text.toString())
            }
        }
    }

    private fun createTodos(title: String, desc: String) {
        RetrofitClient.instance.createTodos(title, desc)
            .enqueue(object : Callback<MessageResponse> {
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
                        finish()
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

    private fun updateTodos(id: Int, title: String, desc: String) {
        RetrofitClient.instance.updateTodos(
            id,
            TodosRequest(title = title, description = desc, completed = 0)
        )
            .enqueue(object : Callback<MessageResponse> {
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
                        finish()
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
}