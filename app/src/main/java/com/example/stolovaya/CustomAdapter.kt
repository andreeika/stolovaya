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

data class InOformlenie(val text: String, var quantity: Int = 1)
class Oformlenie(private val data: ArrayList<InOformlenie>){

}
data class ItemsViewModel(val image: Bitmap?, val text: String, val priceWithRub: String, var quantity: Int = 1)

class CustomAdapter(private val mList: List<ItemsViewModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    // Интерфейс для обработки нажатий на кнопку
    interface OnItemClickListener {
        fun onKorzinaClick(item: ItemsViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design_salades, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        Log.d("Adapter", "Binding item: ${ItemsViewModel.text}, Quantity: ${ItemsViewModel.quantity}")
        holder.btn.setOnClickListener {
            listener.onKorzinaClick(ItemsViewModel)// при нажатии на кнопку "+" интерфейс переносит информацию
                                                   // о конкретном блюде в корзину
        }
        holder.textView.text = ItemsViewModel.text
        holder.textView_price.text = ItemsViewModel.priceWithRub
        // Установка картинки
        ItemsViewModel.image?.let {
            holder.imageView.setImageBitmap(it)

        } ?: run {

        }
        holder.imageView.setImageBitmap(ItemsViewModel.image)
        if (holder.itemView.context is MainActivity_Korzina) {
            holder.quantity.visibility = View.VISIBLE
            holder.quantity.text = ItemsViewModel.quantity.toString()

        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val textView_price: TextView = itemView.findViewById(R.id.textView18)
        val btn: Button = itemView.findViewById(R.id.buttonAdd)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
    }


}


