package com.tegarpenemuan.todoapp.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tegarpenemuan.todoapp.R
import com.tegarpenemuan.todoapp.model.TodosResponse

class Adapter(
    private val context: Context,
    private val list: ArrayList<TodosResponse>,
    private val listener: EventListener
) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvTitle.text = data.title
        holder.tvDesc.text = data.description

        if(data.completed == "1"){
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.disable))
            holder.tvDesc.setTextColor(ContextCompat.getColor(context, R.color.disable))
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvDesc.paintFlags = holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.checkbox.isChecked = true
            val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.disable))
            holder.checkbox.buttonTintList = colorStateList
        }

        holder.itemView.setOnLongClickListener {
            listener.onLongClick(data)
            true
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener.onItemClick(data,position, isChecked)
        }

        holder.itemView.setOnClickListener {
            listener.onClick(data)
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        val checkbox: CheckBox = itemView.findViewById(R.id.cb_complete)
    }

    interface EventListener {
        fun onLongClick(data: TodosResponse)
        fun onClick(data: TodosResponse)
        fun onItemClick(data: TodosResponse,position: Int, isChecked: Boolean)
    }
}
