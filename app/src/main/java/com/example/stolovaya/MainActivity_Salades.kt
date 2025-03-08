package com.example.stolovaya

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayInputStream
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_Salades : AppCompatActivity() {
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var button_korzina: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    var bitmap: Bitmap? = null

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

        button_korzina.setOnClickListener {
            val intent = Intent(this@MainActivity_Salades, MainActivity_Korzina::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<ItemsViewModel>()



        val adapter = CustomAdapter(data)
        recyclerview.adapter = adapter


//        val tx1: TextView = findViewById(R.id.textView12)
//        val tx2: TextView = findViewById(R.id.textView13)

        try {
            val connectionHelper = ConnectionHelper();
            connect = connectionHelper.connectionclass()
            if (connect != null) {
                var query: String = "SELECT name_dish, photo_dish FROM Блюда where id_dish = 1"

                var st: Statement = connect!!.createStatement()
                var rs: ResultSet = st.executeQuery(query);

                while (rs.next()) {

//                    tx1.setText(rs.getString(1));
//                    tx2.setText(rs.getString(2));
                    val name = rs.getString("name_dish")


                    val imageBytes: ByteArray = rs.getBytes("photo_dish")
                    val inputStream = ByteArrayInputStream(imageBytes)
                    bitmap = BitmapFactory.decodeStream(inputStream)


                    // Добавление данных в список
                    data.add(ItemsViewModel(bitmap, name))


                }


            } else {
                connectionResult = "Check Connection";
            }
        } catch (ex: Exception) {
    }





        }}