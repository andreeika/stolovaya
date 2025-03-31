package com.example.stolovaya

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ItemsViewModel_Korzina(val image: Bitmap?, val text: String, val priceWithRub: String, var quantity: Int = 1)

class CustomAdapter_Korzina(private val mList: List<ItemsViewModel_Korzina>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CustomAdapter_Korzina.ViewHolder>() {
    // Интерфейс для обработки нажатий на кнопку
    interface OnItemClickListener {
        fun AddDish(item: ItemsViewModel_Korzina)
        fun DeleteDish(item: ItemsViewModel_Korzina)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design_korzina, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel_Korzina = mList[position]
        Log.d("Adapter", "Binding item: ${ItemsViewModel_Korzina.text}, Quantity: ${ItemsViewModel_Korzina.quantity}")
        holder.btnAdd.setOnClickListener {
            listener.AddDish(ItemsViewModel_Korzina)// при нажатии на кнопку "+" интерфейс переносит информацию
            // о конкретном блюде в корзину
        }
        holder.btnDel.setOnClickListener {
            listener.DeleteDish(ItemsViewModel_Korzina)
        }
        holder.textView.text = ItemsViewModel_Korzina.text
        holder.textView_price.text = ItemsViewModel_Korzina.priceWithRub
        // Установка картинки
        ItemsViewModel_Korzina.image?.let {
            holder.imageView.setImageBitmap(it)

        } ?: run {

        }
        holder.imageView.setImageBitmap(ItemsViewModel_Korzina.image)
        if (holder.itemView.context is MainActivity_Korzina) {
            holder.quantity.visibility = View.VISIBLE
            holder.quantity.text = ItemsViewModel_Korzina.quantity.toString()

        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val textView_price: TextView = itemView.findViewById(R.id.textView18)
        val btnAdd: Button = itemView.findViewById(R.id.buttonAdd)
        val btnDel: Button = itemView.findViewById(R.id.buttonDel)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
    }


}


