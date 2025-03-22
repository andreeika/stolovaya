package com.example.stolovaya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ItemsViewModel_OknoPersonala(val order_id: String, val dishes: String,  var isSelected: Boolean = false)
class Adapter_oknoPersonala(private val mList: List<ItemsViewModel_OknoPersonala>,
                            private val onItemChecked: (ItemsViewModel_OknoPersonala) -> Unit) :
    RecyclerView.Adapter<Adapter_oknoPersonala.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_okno_personala, parent, false)

        return ViewHolder(view)


    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel_OknoPersonala = mList[position]
        holder.textView.text = ItemsViewModel_OknoPersonala.dishes
        holder.idview.text = ItemsViewModel_OknoPersonala.order_id.toString()
        holder.checkBox.isChecked = ItemsViewModel_OknoPersonala.isSelected

        // Обработка изменения состояния CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            ItemsViewModel_OknoPersonala.isSelected = isChecked
            onItemChecked(ItemsViewModel_OknoPersonala) // Вызов лямбды при изменении состояния
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView20)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox3)
        val idview: TextView = itemView.findViewById(R.id.textView21)
    }


}