package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_MenuPersonala : AppCompatActivity() {
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var button_zakaz: Button
    private lateinit var saveButton: Button
    private val items = mutableListOf<ItemsViewModel_spisok>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu_personala)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        button_zakaz = findViewById(R.id.button14) //переход в заказы

        button_zakaz.setOnClickListener {
            val intent =
                Intent(this@MainActivity_MenuPersonala, MainActivity_OknoPersonala::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerview.layoutManager = GridLayoutManager(this, 1)
        val data = ArrayList<ItemsViewModel_spisok>()

        val adapter = CustomAdapter2(data) { ItemsViewModel_spisok ->
            val index = items.indexOf(ItemsViewModel_spisok)
            if (index != -1) { // Проверяем, что элемент найден
                items[index] = ItemsViewModel_spisok
            } else {
                // Обработка случая, когда элемент не найден
                println("Элемент не найден в списке: $ItemsViewModel_spisok")
            }
        }
        recyclerview.adapter = adapter

        try {
            val connectionHelper = ConnectionHelper();
            connect = connectionHelper.connectionclass()
            if (connect != null) {
                var query: String = "SELECT id_dish, name_dish FROM Блюда where id_dish < 6"

                var st: Statement = connect!!.createStatement()
                var rs: ResultSet = st.executeQuery(query);

                val tempList = mutableListOf<Pair<Int, String>>()
                while (rs.next()) {
                    val id_dish = rs.getInt("id_dish")
                    val name = rs.getString("name_dish")
                    tempList.add(Pair(id_dish, name))
                }
                for (i in 0 until tempList.size step 1) {
                    val item1 = tempList[i]

                    val groupedItem = ItemsViewModel_spisok(
                        item1.first,
                        item1.second
                    )
                    data.add(groupedItem)
                    items.add(groupedItem)
                }
            } else {
                connectionResult = "Check Connection";
            }
        } catch (ex: Exception) {

        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            Toast.makeText(this, "Данные сохраняются...", Toast.LENGTH_SHORT).show()
            Log.d("Cat", items.toString())
            try {
                val selectedItems = items.filter { it.isSelected }
                if (selectedItems.isEmpty()) {
                    Toast.makeText(this@MainActivity_MenuPersonala, "Выберите блюда!", Toast.LENGTH_SHORT).show()
                }
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()
                connect?.use { conn ->
                    // Удаление всех данных из таблицы
                    val deleteQuery = "TRUNCATE TABLE Меню"
                    conn.createStatement().use { stmt ->
                        stmt.executeUpdate(deleteQuery)
                    }
                    // Внесение новых данных
                    val query = "INSERT INTO Меню (id_dish) VALUES (?)"
                    conn.prepareStatement(query).use { ps ->
                        for (item in selectedItems) {
                            ps.setInt(1, item.id_dish)
                            ps.addBatch() // Добавляем в пакет
                        }
                        ps.executeBatch() // Выполняем все запросы разом
                    }
                }
                Toast.makeText(this@MainActivity_MenuPersonala, "Данные сохранены!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DB_SAVE_ERROR", e.toString())
                Toast.makeText(this@MainActivity_MenuPersonala, "Ошибка сохранения: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
        }

