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
import kotlin.random.Random

class MainActivity_Oformlenie : AppCompatActivity() {
    var connect: Connection? = null
    var connectionResult: String = ""
    private val data = ArrayList<InOformlenie>()
    private lateinit var save_button: Button
    private lateinit var text_price: TextView
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

        // Шаг 1: Генерация русской буквы
        val russianLetters = ('А'..'Я').toList() // Список русских букв от А до Я
        val randomLetter = russianLetters[Random.nextInt(russianLetters.size)]
        // Шаг 2: Генерация трех цифр
        val randomDigits = List(3) { Random.nextInt(0, 10) }.joinToString("")
        // Шаг 3: Объединение буквы и цифр
        val orderId = "$randomLetter$randomDigits" //id заказа


        // Загружаем данные из SharedPreferences
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        var totalPrice = 0
        text_price = findViewById(R.id.textView14)

        for ((key, value) in allEntries) {
            if (key.endsWith("_name")) {
                items.add(InOformlenie(value.toString()))
            }
            if (key.endsWith("_priceWithRub")) {
                items.add(InOformlenie(value.toString()))
                val priceKey = key.replace("_name", "_priceWithRub")
                val priceValue = sharedPreferences.getString(priceKey, null)
                val numberRegex = Regex("(\\d+)")
                val numberMatch = numberRegex.find(priceValue!!)
                val price = numberMatch?.value?.toIntOrNull()
                if (price != null) {
                    totalPrice += price
                }
                text_price.setText(totalPrice.toString() + " руб")
            }
        }
        val selectedDishNames = items.map { it.text }
        val selectedDishesString = selectedDishNames.joinToString(", ")


        save_button = findViewById(R.id.saveButton3)
        save_button.setOnClickListener {
            Toast.makeText(this, "Заказ оформляется...", Toast.LENGTH_SHORT).show()
            save_button.isEnabled = false
            try {
               // val selectedItems = data
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()
                connect?.use { conn ->
                    // Внесение новых данных
                    val query = "INSERT INTO Заказы (order_id, dishes) VALUES (?,?)"
                    conn.prepareStatement(query).use { ps ->
                        ps.setString(1, orderId)
                        ps.setString(2, selectedDishesString)
                        ps.addBatch() // Добавляем в пакет
                        ps.executeBatch() // Выполняем все запросы разом
                    }
                }
                Toast.makeText(this@MainActivity_Oformlenie, "Заказ оформлен!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DB_SAVE_ERROR", e.toString())
                Toast.makeText(this@MainActivity_Oformlenie, "Ошибка сохранения: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}