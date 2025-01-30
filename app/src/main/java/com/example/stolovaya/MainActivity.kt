package com.example.stolovaya

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var textView_salades: TextView
    private lateinit var textView_soup: TextView
    private lateinit var textView_myaso: TextView
    private lateinit var textView_garnir: TextView
    private lateinit var textView_bulochki: TextView
    private lateinit var textView_deserts: TextView
    private lateinit var textView_napitki: TextView
    private lateinit var button_korzina: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        // Инициализация View
        textView_salades = findViewById(R.id.textView)
        textView_soup = findViewById(R.id.textView3)
        textView_myaso = findViewById(R.id.textView4)
        textView_garnir = findViewById(R.id.textView5)
        textView_bulochki = findViewById(R.id.textView6)
        textView_deserts = findViewById(R.id.textView9)
        textView_napitki = findViewById(R.id.textView7)
        button_korzina = findViewById(R.id.button)

        // Обработчик нажатия на кнопку и переход на окно

        button_korzina.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Korzina::class.java)
            startActivity(intent)
        }

        textView_salades.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Salades::class.java)
            startActivity(intent)
        }

        textView_myaso.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Myaso::class.java)
            startActivity(intent)
        }

        textView_garnir.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Garnir::class.java)
            startActivity(intent)
        }


        textView_soup.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Soup::class.java)
            startActivity(intent)
        }

        textView_bulochki.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Bulochki::class.java)
            startActivity(intent)
        }

        textView_deserts.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Deserts::class.java)
            startActivity(intent)
        }

        textView_napitki.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Napitki::class.java)
            startActivity(intent)
        }


    }

}