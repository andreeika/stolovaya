package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.sql.Connection

class MainActivity_Korzina : AppCompatActivity(), CustomAdapter.OnItemClickListener {
    var connect: Connection? = null
    var connectionResult: String = ""
    private val data = ArrayList<ItemsViewModel>()
    private lateinit var btnClear: Button
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView
    private lateinit var save_button: Button
    private lateinit var text_price: TextView
    private lateinit var adapter: CustomAdapter
    var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_korzina)

        val recyclerview = findViewById<RecyclerView>(R.id.rvKorzina)
        recyclerview.layoutManager = GridLayoutManager(this, 2)

        adapter = CustomAdapter(data, this)//с помощью адаптера принимаем информацию
        recyclerview.adapter = adapter

        text_price = findViewById(R.id.textView19)
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
                    val priceKey = key.replace("_name", "_priceWithRub")
                    val priceValue = sharedPreferences.getString(priceKey, null)
                    // Создаем объект ItemsViewModel
                    val newItem = ItemsViewModel(bitmap, value.toString(), priceValue.toString())
                    data.add(newItem)
                    val numberRegex = Regex("(\\d+)")
                    val numberMatch = numberRegex.find(priceValue!!)
                    val price = numberMatch?.value?.toIntOrNull()
                    if (price != null) {
                        totalPrice += price
                    }
                    text_price.setText(totalPrice.toString() + " руб")
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

            text_price.setText("0 руб")
        }

        logoBack = findViewById(R.id.logoBack) //возврат на главную при нажатии на лого
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
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Преобразуем Bitmap в Base64
        val stream = ByteArrayOutputStream()
        item.image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()
        val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        // Генерируем уникальный ключ для каждого элемента
        val uniqueKey = "item_${System.currentTimeMillis()}"

        // Сохраняем данные
        editor.remove("${uniqueKey}_name") // Название блюда
        editor.remove("${uniqueKey}_priceWithRub") // Название блюда
        editor.remove("${uniqueKey}_image") // Изображение в Base64
        editor.apply()
        val numberRegex = Regex("(\\d+)")
        val numberMatch = numberRegex.find(item.priceWithRub)
        val price = numberMatch?.value?.toIntOrNull()
        totalPrice -= price!!
        text_price.setText(totalPrice.toString() + " руб")
        data.remove(item)
        adapter.notifyDataSetChanged()
    }

}