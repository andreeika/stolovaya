package com.example.stolovaya

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.sql.Connection

class MainActivity_Korzina : AppCompatActivity(), CustomAdapter_Korzina.OnItemClickListener {
    var connect: Connection? = null
    var connectionResult: String = ""
    private val data = ArrayList<ItemsViewModel_Korzina>()
    private lateinit var btnClear: Button
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView
    private lateinit var save_button: Button
    private lateinit var text_price: TextView
    private lateinit var adapter: CustomAdapter_Korzina
    var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_korzina)

        val recyclerview = findViewById<RecyclerView>(R.id.rvKorzina)
        recyclerview.layoutManager = GridLayoutManager(this, 1)

        adapter = CustomAdapter_Korzina(data, this)//с помощью адаптера принимаем информацию
        recyclerview.adapter = adapter

        text_price = findViewById(R.id.textView19)
        // Загружаем данные из SharedPreferences
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.endsWith("_name")) {
                val itemKey = key.replace("_name", "")
                val name = value.toString()
                val imageKey = "${itemKey}_image"
                val priceKey = "${itemKey}_priceWithRub"
                val pricePerItemKey = "${itemKey}_pricePerItem"
                val quantityKey = "${itemKey}_quantity"

                val imageBase64 = sharedPreferences.getString(imageKey, null)
                val priceWithRub = sharedPreferences.getString(priceKey, null)
                val pricePerItemValue = sharedPreferences.getString(pricePerItemKey, null)
                val quantity = sharedPreferences.getInt(quantityKey, 1)

                if (imageBase64 != null && priceWithRub != null && pricePerItemValue != null) {
                    val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    val newItem = ItemsViewModel_Korzina(bitmap, name, priceWithRub, quantity)
                    data.add(newItem)

                    // Извлекаем цену за единицу блюда
                    val numberRegex = Regex("(\\d+)")
                    val numberMatch = numberRegex.find(pricePerItemValue)
                    val pricePerItem = numberMatch?.value?.toIntOrNull() ?: 0

                    // Рассчитываем общую стоимость с учётом количества
                    totalPrice += pricePerItem * quantity
                    text_price.text = "$totalPrice руб"
                }
            }
        }

        //Цвет для нижней строки с кнопками домой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
        }
        btnClear = findViewById(R.id.btnClearKorzina)
        btnClear.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // Удаляем все данные
            editor.apply()

            data.clear()
            adapter.notifyDataSetChanged()

            totalPrice = 0 // Сбрасываем общую стоимость
            text_price.setText("0 руб")
            Log.d("Cat", totalPrice.toString())
        }

        logoBack = findViewById(R.id.logoBack) //возврат на главную при нажатии на лого
        logoBack.setOnClickListener {
            val intent = Intent(this@MainActivity_Korzina, MainActivity::class.java)
            startActivity(intent)
        }

        save_button = findViewById(R.id.saveButton2)

        save_button.setOnClickListener {
            val intent =
                Intent(this@MainActivity_Korzina, MainActivity_Oformlenie::class.java)
            startActivity(intent)
        }

    }

    override fun AddDish(item: ItemsViewModel_Korzina) {
        Log.d("Cat", "Это происходит тут")
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Находим ключ блюда в SharedPreferences
        val existingKey = findExistingItemKey(sharedPreferences, item.text)
        if (existingKey != null) {
            // Получаем текущее количество блюда
            val quantityKey = "${existingKey}_quantity"
            val currentQuantity = sharedPreferences.getInt(quantityKey, 1)

            // Извлекаем цену за один экземпляр блюда
            val numberRegex = Regex("(\\d+)")
            val numberMatch = numberRegex.find(item.priceWithRub)
            val pricePerItem = numberMatch?.value?.toIntOrNull() ?: 0
            val newQuantity = currentQuantity + 1
            editor.putInt(quantityKey, newQuantity)

            // Обновляем количество в списке данных
            val index = data.indexOfFirst { it.text == item.text }
            if (index != -1) {
                data[index].quantity = newQuantity
                adapter.notifyItemChanged(index)
            }

            // Вычитаем стоимость одного экземпляра блюда из общей стоимости
            totalPrice += pricePerItem
            text_price.text = "$totalPrice руб"

            editor.apply() // Применяем изменения в SharedPreferences
        }
    }

    override fun DeleteDish(item: ItemsViewModel_Korzina) {
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Находим ключ блюда в SharedPreferences
        val existingKey = findExistingItemKey(sharedPreferences, item.text)
        if (existingKey != null) {
            // Получаем текущее количество блюда
            val quantityKey = "${existingKey}_quantity"
            val currentQuantity = sharedPreferences.getInt(quantityKey, 1)

            // Извлекаем цену за один экземпляр блюда
            val numberRegex = Regex("(\\d+)")
            val numberMatch = numberRegex.find(item.priceWithRub)
            val pricePerItem = numberMatch?.value?.toIntOrNull() ?: 0

            if (currentQuantity > 1) {
                // Если количество больше 1, уменьшаем его на 1
                val newQuantity = currentQuantity - 1
                editor.putInt(quantityKey, newQuantity)

                // Обновляем количество в списке данных
                val index = data.indexOfFirst { it.text == item.text }
                if (index != -1) {
                    data[index].quantity = newQuantity
                    adapter.notifyItemChanged(index)
                }
            } else {
                // Если количество равно 1, удаляем блюдо из SharedPreferences и списка данных
                editor.remove("${existingKey}_name")
                editor.remove("${existingKey}_priceWithRub")
                editor.remove("${existingKey}_image")
                editor.remove(quantityKey)

                val index = data.indexOfFirst { it.text == item.text }
                if (index != -1) {
                    data.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }

            // Вычитаем стоимость одного экземпляра блюда из общей стоимости
            totalPrice -= pricePerItem
            text_price.text = "$totalPrice руб"

            editor.apply() // Применяем изменения в SharedPreferences
        }
        Log.d("Cat", totalPrice.toString())
    }

}
    private fun findExistingItemKey(sharedPreferences: SharedPreferences, itemName: String): String? {
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.endsWith("_name") && value == itemName) {
                return key.replace("_name", "")
            }
        }
        return null
    }