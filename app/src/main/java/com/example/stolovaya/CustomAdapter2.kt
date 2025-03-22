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
class CustomAdapter2(private val mList: List<ItemsViewModel_spisok>,  private val onItemChecked: (ItemsViewModel_spisok) -> Unit) :
    RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_spisok, parent, false)

        return ViewHolder(view)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel_spisok = mList[position]
        holder.textView.text = ItemsViewModel_spisok.text
        holder.idview.text = ItemsViewModel_spisok.id_dish.toString()
        holder.checkBox.isChecked = ItemsViewModel_spisok.isSelected

        // Обработка изменения состояния CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            ItemsViewModel_spisok.isSelected = isChecked
            onItemChecked(ItemsViewModel_spisok) // Вызов лямбды при изменении состояния
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val idview: TextView = itemView.findViewById(R.id.textView12)
    }

}