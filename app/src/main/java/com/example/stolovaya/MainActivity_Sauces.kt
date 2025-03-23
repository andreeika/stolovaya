package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_Sauces : AppCompatActivity(), CustomAdapter.OnItemClickListener {

    private lateinit var button_korzina: Button
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_sauces)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        logoBack = findViewById(R.id.logoBack) //возврат на главную при нажатии на лого
        logoBack.setOnClickListener {
            val intent = Intent(this@MainActivity_Sauces, MainActivity::class.java)
            startActivity(intent)
        }

        button_korzina = findViewById(R.id.button10)

        button_korzina.setOnClickListener {
            val intent = Intent(this@MainActivity_Sauces, MainActivity_Korzina::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        val data = ArrayList<ItemsViewModel>()

        val adapter = CustomAdapter(data, this) // Передаём информацию data при помощи интерфейса listener
        recyclerview.adapter = adapter

        progressBar = findViewById(R.id.progressBar)

        // Запуск корутины для выполнения запроса к базе данных
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
            try {
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()

                if (connect != null) {
                    val query: String = "exec categoriesDivision 9"
                    val st: Statement = connect!!.createStatement()
                    val rs: ResultSet = st.executeQuery(query)

                    val tempList = mutableListOf<Triple<Bitmap?, String, String>>()
                    while (rs.next()) {
                        val name = rs.getString("name_dish")
                        val price = rs.getInt("price_dish")
                        val priceWithRub = "$price руб"
                        val imageBytes: ByteArray = rs.getBytes("photo_dish") // Двоичные данные картинки
                        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        tempList.add(Triple(bitmap, name, priceWithRub))
                    }

                    // Обновление UI в основном потоке
                    withContext(Dispatchers.Main) {
                        for (i in 0 until tempList.size) {
                            val item1 = tempList[i]
                            val groupedItem = ItemsViewModel(
                                item1.first, // image (Bitmap?)
                                item1.second,
                                item1.third// text (String)
                            )
                            data.add(groupedItem)
                        }
                        adapter.notifyDataSetChanged() // Уведомляем адаптер об изменении данных
                    }
                    progressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                } else {
                    withContext(Dispatchers.Main) {
                        connectionResult = "Check Connection"
                        Toast.makeText(this@MainActivity_Sauces, connectionResult, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    connectionResult = "Error: ${ex.message}"
                    Toast.makeText(this@MainActivity_Sauces, connectionResult, Toast.LENGTH_SHORT).show()
                }
            }
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
        editor.putString("${uniqueKey}_name", item.text) // Название блюда
        editor.putString("${uniqueKey}_priceWithRub", item.priceWithRub) // Название блюда
        editor.putString("${uniqueKey}_image", imageBase64) // Изображение в Base64
        editor.apply()
    }
}