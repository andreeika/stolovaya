package com.example.stolovaya

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
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
    private lateinit var table: TextView
    private val items = mutableListOf<InOformlenie>()
    var selectedTable: String? = null
    var priceInBd: String? = null
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView

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

        logoBack = findViewById(R.id.logoBack) //возврат на главную при нажатии на лого
        logoBack.setOnClickListener {
            val intent = Intent(this@MainActivity_Oformlenie, MainActivity::class.java)
            startActivity(intent)
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
//            if (key.endsWith("_priceWithRub")) {
//                items.add(InOformlenie(value.toString()))
//            }
        }
        for ((key, value) in allEntries){
            if (key.endsWith("_priceWithRub")) {
                val priceKey = key.replace("_name", "_priceWithRub")
                val priceValue = sharedPreferences.getString(priceKey, null)
                val numberRegex = Regex("(\\d+)")
                val numberMatch = numberRegex.find(priceValue!!)
                val price = numberMatch?.value?.toIntOrNull()
                if (price != null) {
                    totalPrice += price

                }
                text_price.setText(totalPrice.toString() + " руб")
                priceInBd = totalPrice.toString()
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
                    val query = "INSERT INTO Заказы (order_id, dishes, status, table_num, sum) VALUES (?,?, 'Не выполнен', ?,?)"
                    conn.prepareStatement(query).use { ps ->
                        ps.setString(1, orderId)
                        ps.setString(2, selectedDishesString)
                        ps.setString(3, selectedTable)
                        ps.setString(4, priceInBd)
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

        table = findViewById(R.id.textView22)
        table.setOnClickListener{ view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.tables_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    selectedTable = "1"
                    table.setText("1")
                    true // Обработано
                }
                R.id.menu_item_2 -> {
                    selectedTable = "2"
                    table.setText("2")
                    true // Обработано
                }
                R.id.menu_item_3 -> {
                    selectedTable = "3"
                    table.setText("3")
                    true // Обработано
                }
                R.id.menu_item_4 -> {
                    selectedTable = "4"
                    table.setText("4")
                    true // Обработано
                }
                R.id.menu_item_5 -> {
                    selectedTable = "5"
                    table.setText("5")
                    true // Обработано
                }

                else -> false // Не обработано
            }
        }

        popupMenu.show()
    }
}