package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
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

class MainActivity_MenuPersonala : AppCompatActivity(){
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var button_zakaz: Button

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
            val intent = Intent(this@MainActivity_MenuPersonala, MainActivity_OknoPersonala::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview2)
        recyclerview.layoutManager = GridLayoutManager(this, 1)
        val data = ArrayList<ItemsViewModel_spisok>()

        val adapter = CustomAdapter2(data)//Передаём информацию data при помощи интерфейса listener
        recyclerview.adapter = adapter

        try {
            val connectionHelper = ConnectionHelper();
            connect = connectionHelper.connectionclass()
            if (connect != null) {
                var query: String = "SELECT name_dish FROM Блюда where id_dish < 6"

                var st: Statement = connect!!.createStatement()
                var rs: ResultSet = st.executeQuery(query);

                val tempList = mutableListOf<String>()
                while (rs.next()) {

                    val name = rs.getString("name_dish")
                    tempList.add(name)
                }
                for (i in 0 until tempList.size step 1) {
                    val item1 = tempList[i]

                    val groupedItem = ItemsViewModel_spisok(item1)
                    data.add(groupedItem)
                }
            } else {
                connectionResult = "Check Connection";
            }
        }
        catch (ex: Exception) {

        }
    }
}