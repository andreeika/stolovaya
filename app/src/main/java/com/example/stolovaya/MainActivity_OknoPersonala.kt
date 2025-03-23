package com.example.stolovaya

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_OknoPersonala : AppCompatActivity() {
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var button_menu: Button
    private lateinit var button_update: Button
    private lateinit var button_save: Button
    private val items = mutableListOf<ItemsViewModel_OknoPersonala>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_okno_personala)

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
            val intent = Intent(this@MainActivity_OknoPersonala, MainActivity::class.java)
            startActivity(intent)
        }

        button_menu = findViewById(R.id.button11) //переход в создание меню

        button_menu.setOnClickListener {
            val intent = Intent(this@MainActivity_OknoPersonala, MainActivity_MenuPersonala::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView_zakaz)
        recyclerview.layoutManager = GridLayoutManager(this, 1)
        val data = ArrayList<ItemsViewModel_OknoPersonala>()

        val adapter = Adapter_oknoPersonala(data) { ItemsViewModel_OknoPersonala ->
            val index = items.indexOf(ItemsViewModel_OknoPersonala)
            if (index != -1) { // Проверяем, что элемент найден
                items[index] = ItemsViewModel_OknoPersonala
            } else {
                // Обработка случая, когда элемент не найден
                println("Элемент не найден в списке: $ItemsViewModel_OknoPersonala")
            }
        }
        recyclerview.adapter = adapter

        // вывод заказов
        try {
            val connectionHelper = ConnectionHelper();
            connect = connectionHelper.connectionclass()
            if (connect != null) {
                var query: String = "SELECT order_id, dishes FROM Заказы where status = 'Не выполнен'"

                var st: Statement = connect!!.createStatement()
                var rs: ResultSet = st.executeQuery(query);

                val tempList = mutableListOf<Pair<String, String>>()
                while (rs.next()) {
                    val order_id = rs.getString("order_id")
                    val dishes = rs.getString("dishes")
                    tempList.add(Pair(order_id, dishes))
                }
                for (i in 0 until tempList.size step 1) {
                    val item1 = tempList[i]

                    val groupedItem = ItemsViewModel_OknoPersonala(
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

        button_save = findViewById(R.id.saveButton4) // завершение заказа
        button_save.setOnClickListener{
            Toast.makeText(this, "Статусы заказов обновляются...", Toast.LENGTH_SHORT).show()
            try {
                val selectedItems = items.filter { it.isSelected }
                if (selectedItems.isEmpty()) {
                    Toast.makeText(this@MainActivity_OknoPersonala, "Выберите заказы!", Toast.LENGTH_SHORT).show()
                }
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()
                connect?.use { conn ->
                    // обновление статуса
                    val query = "update Заказы set status = 'Выполнен' where order_id = (?)"
                    conn.prepareStatement(query).use { ps ->
                        for (item in selectedItems) {
                            ps.setString(1, item.order_id)
                            ps.addBatch() // Добавляем в пакет
                        }
                        ps.executeBatch() // Выполняем все запросы разом
                    }
                }
                Toast.makeText(this@MainActivity_OknoPersonala, "Данные сохранены!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DB_SAVE_ERROR", e.toString())
                Toast.makeText(this@MainActivity_OknoPersonala, "Ошибка сохранения: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        button_update = findViewById(R.id.button13)
        button_update.setOnClickListener{

            data.clear()
            adapter.notifyDataSetChanged()

            try {
                val connectionHelper = ConnectionHelper();
                connect = connectionHelper.connectionclass()
                if (connect != null) {
                    var query: String = "SELECT order_id, dishes FROM Заказы where status = 'Не выполнен'"

                    var st: Statement = connect!!.createStatement()
                    var rs: ResultSet = st.executeQuery(query);

                    val tempList = mutableListOf<Pair<String, String>>()
                    while (rs.next()) {
                        val order_id = rs.getString("order_id")
                        val dishes = rs.getString("dishes")
                        tempList.add(Pair(order_id, dishes))
                    }
                    for (i in 0 until tempList.size step 1) {
                        val item1 = tempList[i]

                        val groupedItem = ItemsViewModel_OknoPersonala(
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
        }

    }
}