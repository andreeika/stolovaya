package com.example.stolovaya

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

class MainActivity_Login : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button

    private val correctUsername = "user"
    private val correctPassword = "password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

        //Цвет для строки состояния
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }

        lateinit var logo: ImageView
        logo = findViewById(R.id.imageView4)
        logo.setOnClickListener{
            val intent = Intent(this@MainActivity_Login, MainActivity::class.java)
            startActivity(intent)
        }

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.button)


        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Проверка авторизации
            if (username == correctUsername && password == correctPassword) {
                // Авторизация успешна
                Toast.makeText(this, "Авторизация успешна!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity_Login, MainActivity_OknoPersonala::class.java)
                    startActivity(intent)

            } else {
                // Авторизация не удалась
                Toast.makeText(this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show()
            }


    }
}}