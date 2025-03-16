package com.example.stolovaya

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.sql.Connection

class MainActivity_Oformlenie : AppCompatActivity() {
    var connect: Connection? = null
    var connectionResult: String = ""
    private val data = ArrayList<InOformlenie>()
    private lateinit var save_button: Button
    private lateinit var text: TextView
    private val items = mutableListOf<InOformlenie>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_oformlenie)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }


        // Загружаем данные из SharedPreferences
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        for ((key, value) in allEntries) {
            if (key.endsWith("_name")) {
                data.add(InOformlenie(value.toString()))
                items.add(InOformlenie(value.toString()))
            }
        }

        save_button = findViewById(R.id.saveButton3)
        save_button.setOnClickListener {
            Toast.makeText(this, "Данные сохраняются...", Toast.LENGTH_SHORT).show()
            Log.d("Cat", items.toString())
            try {
                val selectedItems = data
                if (selectedItems.isEmpty()) {
                    Toast.makeText(this@MainActivity_Oformlenie, "Выберите блюда!", Toast.LENGTH_SHORT).show()
                }
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()
                connect?.use { conn ->
                    // Внесение новых данных
                    val query = "INSERT INTO Заказы (dishes) VALUES (?)"
                    conn.prepareStatement(query).use { ps ->
                        ps.setString(1, selectedItems.toString())
                        ps.addBatch() // Добавляем в пакет
                        ps.executeBatch() // Выполняем все запросы разом
                    }
                }
                Toast.makeText(this@MainActivity_Oformlenie, "Данные сохранены!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DB_SAVE_ERROR", e.toString())
                Toast.makeText(this@MainActivity_Oformlenie, "Ошибка сохранения: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}