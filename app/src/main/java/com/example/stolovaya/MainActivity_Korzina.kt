package com.example.stolovaya

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity_Korzina : AppCompatActivity(), CustomAdapter.OnItemClickListener {
    private val data = ArrayList<ItemsViewModel>()
    private lateinit var btnClear: Button
    private lateinit var logoBack: ImageView
    private lateinit var save_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_korzina)

        val recyclerview = findViewById<RecyclerView>(R.id.rvKorzina)
        recyclerview.layoutManager = GridLayoutManager(this, 2)

        val adapter = CustomAdapter(data, this)//с помощью адаптера принимаем информацию
        recyclerview.adapter = adapter

        // Загружаем данные из SharedPreferences
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        for ((key, value) in allEntries) {
            if (key.endsWith("_name")) {
                val imageKey = key.replace("_name", "_image")
                val imageBase64 = sharedPreferences.getString(imageKey, null)
                if (imageBase64 != null) {
                    // Преобразуем Base64 обратно в Bitmap
                    val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // Создаем объект ItemsViewModel
                    val newItem = ItemsViewModel(bitmap, value.toString(), value.toString())
                    data.add(newItem)
                }
            }
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }
        btnClear = findViewById(R.id.btnClearKorzina)
        btnClear.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // Удаляем все данные
            editor.apply()

            data.clear()
            adapter.notifyDataSetChanged()
        }

        logoBack = findViewById(R.id.logoBackKorzina) //возврат на главную при нажатии на лого
        logoBack.setOnClickListener {
            val intent = Intent(this@MainActivity_Korzina, MainActivity::class.java)
            startActivity(intent)
        }

        save_button = findViewById(R.id.saveButton2)

        save_button.setOnClickListener {
            val intent =
                Intent(this@MainActivity_Korzina, MainActivity_Oformlenie::class.java)
            startActivity(intent)
        }

    }

    override fun onKorzinaClick(item: ItemsViewModel) {
        TODO("Not yet implemented")
    }

}