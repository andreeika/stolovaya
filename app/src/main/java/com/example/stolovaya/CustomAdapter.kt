package com.example.stolovaya

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class InOformlenie(val text: String)
class Oformlenie(private val data: ArrayList<InOformlenie>){

}
data class ItemsViewModel(val image: Bitmap?, val text: String)
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
        holder.btn.setOnClickListener {
            listener.onKorzinaClick(ItemsViewModel)// при нажатии на кнопку "+" интерфейс переносит информацию
                                                   // о конкретном блюде в корзину
        }
        holder.textView.text = ItemsViewModel.text
        // Установка картинки
        ItemsViewModel.image?.let {
            holder.imageView.setImageBitmap(it)

        } ?: run {

        }
        holder.imageView.setImageBitmap(ItemsViewModel.image)

    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val btn: Button = itemView.findViewById(R.id.buttonAdd)
    }


}


