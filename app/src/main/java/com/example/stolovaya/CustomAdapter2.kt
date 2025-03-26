package com.example.stolovaya

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.CheckBox

data class ItemsViewModel_spisok(val id_dish: Int, val text: String,  var isSelected: Boolean = false)
class CustomAdapter2(
    private var mList: List<ItemsViewModel_spisok>,
    private val onItemChecked: (ItemsViewModel_spisok) -> Unit
) : RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {

    private var filteredList: List<ItemsViewModel_spisok> = mList

    // Остальные методы остаются без изменений
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_spisok, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.textView.text = item.text
        holder.idview.text = item.id_dish.toString()
        holder.checkBox.isChecked = item.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onItemChecked(item)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    // Новый метод для фильтрации
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            mList
        } else {
            mList.filter {
                it.text.contains(query, ignoreCase = true) ||
                        it.id_dish.toString().contains(query)
            }
        }
        notifyDataSetChanged()
    }

    // Обновление данных
    fun updateList(newList: List<ItemsViewModel_spisok>) {
        mList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val idview: TextView = itemView.findViewById(R.id.textView12)
    }
}