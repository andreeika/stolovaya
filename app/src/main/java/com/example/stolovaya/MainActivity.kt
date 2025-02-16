package com.example.stolovaya

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import android.net.Uri
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.FrameLayout;
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private lateinit var textView_salades: TextView
    private lateinit var textView_soup: TextView
    private lateinit var textView_myaso: TextView
    private lateinit var textView_garnir: TextView
    private lateinit var textView_bulochki: TextView
    private lateinit var textView_deserts: TextView
    private lateinit var textView_napitki: TextView
    private lateinit var button_korzina: Button
    private lateinit var sibsiu_logo: ImageView
    private lateinit var terpi: VideoView
    private lateinit var logo: ImageView
    private var counter = 0

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
        sibsiu_logo = findViewById(R.id.imageView2)
        terpi = findViewById(R.id.videoView2)
        logo = findViewById(R.id.imageView4)
        val views = arrayOf(textView_salades, textView_soup, textView_myaso, textView_garnir, textView_bulochki, textView_deserts, textView_napitki, button_korzina, sibsiu_logo, terpi, logo)


        sibsiu_logo.setOnClickListener{
            counter += 1
            val url = Uri.parse("android.resource://" + packageName + "/" + R.raw.terpi_video)
            terpi.setVideoURI(url)
            if (counter == 30){
                Log.d("logo", "терпи")
                for (i in views) i.isClickable = false
                counter = 0
                terpi.visibility = View.VISIBLE
                terpi.start()
                terpi.setOnCompletionListener {
                   for (i in views) i.isClickable = true
                    terpi.visibility = View.GONE
                }
            }
        }

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

        logo.setOnClickListener { view ->
            showPopupMenu(view)
    }

}
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.my_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_item_1 -> { val intent = Intent(this@MainActivity, MainActivity_Login::class.java)
                    startActivity(intent)
                    true // Обработано
                }
                R.id.menu_item_2 -> {
                    true // Обработано
                }

                else -> false // Не обработано
            }
        }

        popupMenu.show()
    }
}