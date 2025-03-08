package com.example.stolovaya

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ItemsViewModel(val image: Bitmap?, val text: String)
class CustomAdapter(private val mList: List<ItemsViewModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.textView.text = ItemsViewModel.text


        // Установка картинки
        ItemsViewModel.image?.let {
            holder.imageView.setImageBitmap(it)

        } ?: run {

        }
        holder.imageView.setImageBitmap(ItemsViewModel.image)
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.imageView.setAdjustViewBounds(true);








    }


    override fun getItemCount(): Int {
        return mList.size
    }



    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val imageView3: ImageView = itemView.findViewById(R.id.imageview3)
        val textView14: TextView = itemView.findViewById(R.id.textView14)



    }


}


