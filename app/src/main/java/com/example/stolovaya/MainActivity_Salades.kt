package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_Salades : AppCompatActivity(), CustomAdapter.OnItemClickListener {
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var button_korzina: Button //кнопка перехода в корзину

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_salades)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        button_korzina = findViewById(R.id.button7)

        button_korzina.setOnClickListener {//Переход в корзину
            val intent = Intent(this@MainActivity_Salades, MainActivity_Korzina::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        val data = ArrayList<ItemsViewModel>()

        val adapter = CustomAdapter(data, this)//Передаём информацию data при помощи интерфейса listener
        recyclerview.adapter = adapter

        try {
            val connectionHelper = ConnectionHelper();
            connect = connectionHelper.connectionclass()
            if (connect != null) {
                var query: String = "SELECT name_dish, photo_dish  FROM Блюда where photo_dish is not null"

                var st: Statement = connect!!.createStatement()
                var rs: ResultSet = st.executeQuery(query);

                val tempList = mutableListOf<Pair<Bitmap?, String>>()
                while (rs.next()) {

                    val name = rs.getString("name_dish")
                    val imageBytes: ByteArray = rs.getBytes("photo_dish") // Двоичные данные картинки
                    val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    tempList.add(Pair(bitmap, name))
                }
                for (i in 0 until tempList.size step 1) {
                    val item1 = tempList[i]

                    val groupedItem = ItemsViewModel(
                        item1.first, // image (Bitmap?)
                        item1.second  // text (String)
                    )
                    data.add(groupedItem)
                }
            } else {
                connectionResult = "Check Connection";
            }
        }
        catch (ex: Exception) {

        }
    }

    // Реализация метода интерфейса переноса блюд в корзину
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
        editor.putString("${uniqueKey}_name", item.text) // Название блюда
        editor.putString("${uniqueKey}_image", imageBase64) // Изображение в Base64
        editor.apply()

    }
}