package com.example.stolovaya

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

class MainActivity_Myaso : AppCompatActivity(), CustomAdapter.OnItemClickListener {

    private lateinit var button_korzina: Button
    var connect: Connection? = null
    var connectionResult: String = ""
    private lateinit var progressBar: ProgressBar // ProgressBar крутилка загрузки
    private lateinit var logoBack: ImageView
    var totalPrice: Int = 0

    private lateinit var data: ArrayList<ItemsViewModel>
    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_myaso)

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
            val intent = Intent(this@MainActivity_Myaso, MainActivity::class.java)
            startActivity(intent)
        }

        button_korzina = findViewById(R.id.button5)

        button_korzina.setOnClickListener {
            val intent = Intent(this@MainActivity_Myaso, MainActivity_Korzina::class.java)
            startActivity(intent)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        data = ArrayList<ItemsViewModel>()

        adapter = CustomAdapter(data, this) // Передаём информацию data при помощи интерфейса listener
        recyclerview.adapter = adapter

        progressBar = findViewById(R.id.progressBar)

        // Запуск корутины для выполнения запроса к базе данных
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
            try {
                val connectionHelper = ConnectionHelper()
                connect = connectionHelper.connectionclass()

                if (connect != null) {
                    val query: String = "exec categoriesDivision 4"
                    val st: Statement = connect!!.createStatement()
                    val rs: ResultSet = st.executeQuery(query)

                    val tempList = mutableListOf<ItemsViewModel>()
                    while (rs.next()) {
                        val name = rs.getString("name_dish")
                        val price = rs.getInt("price_dish")
                        val priceWithRub = "$price руб"
                        val imageBytes: ByteArray = rs.getBytes("photo_dish") // Двоичные данные картинки
                        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        // Проверяем, есть ли уже такое блюдо в корзине
                        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
                        val existingKey = findExistingItemKey(sharedPreferences, name)
                        val quantity = if (existingKey != null) {
                            // Если блюдо уже есть, увеличиваем его количество
                            val quantityKey = "${existingKey}_quantity"
                            val currentQuantity = sharedPreferences.getInt(quantityKey, 1)
                            currentQuantity + 1
                        } else {
                            // Если блюда нет, устанавливаем начальное количество 1
                            1
                        }

                        tempList.add(ItemsViewModel(bitmap, name, priceWithRub, quantity))
                        Log.d("Cat", tempList.toString())
                    }

                    // Обновление UI в основном потоке
                    withContext(Dispatchers.Main) {
                        for (i in 0 until tempList.size) {
                            val item1 = tempList[i]
                            val groupedItem = ItemsViewModel(
                                item1.image, // image (Bitmap?)
                                item1.text,
                                item1.priceWithRub,// text (String)
                                item1.quantity
                            )
                            data.add(groupedItem)
                        }
                        adapter.notifyDataSetChanged() // Уведомляем адаптер об изменении данных
                    }
                    progressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                } else {
                    withContext(Dispatchers.Main) {
                        connectionResult = "Check Connection"
                        Toast.makeText(this@MainActivity_Myaso, connectionResult, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    connectionResult = "Error: ${ex.message}"
                    Toast.makeText(this@MainActivity_Myaso, connectionResult, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onKorzinaClick(item: ItemsViewModel) {
        val sharedPreferences = getSharedPreferences("Korzina", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Проверяем, есть ли уже такое блюдо в корзине
        val existingKey = findExistingItemKey(sharedPreferences, item.text)
        val totalPriceForItem: Int
        if (existingKey != null) {
            // Если блюдо уже есть, увеличиваем его количество
            val quantityKey = "${existingKey}_quantity"
            val currentQuantity = sharedPreferences.getInt(quantityKey, 1)
            val newQuantity = currentQuantity + 1
            editor.putInt(quantityKey, newQuantity)

            // Получаем цену за единицу блюда (pricePerItem)
            val pricePerItemKey = "${existingKey}_pricePerItem"
            val pricePerItemValue = sharedPreferences.getString(pricePerItemKey, null)
            val numberRegex = Regex("(\\d+)")
            val numberMatch = numberRegex.find(pricePerItemValue!!)
            val pricePerItem = numberMatch?.value?.toIntOrNull() ?: 0

            // Рассчитываем общую стоимость для этого блюда
            totalPriceForItem = pricePerItem
            editor.putString("${existingKey}_pricePerItem", "$totalPriceForItem руб")

            // Обновляем количество в списке данных
            val index = data.indexOfFirst { it.text == item.text }
            if (index != -1) {
                data[index].quantity = newQuantity
                Log.d(
                    "Korzina",
                    "Updated item: ${data[index].text}, New quantity: ${data[index].quantity}"
                )
                adapter.notifyItemChanged(index) // Уведомляем адаптер об изменении
            }
        } else {
            // Если блюда нет, создаем новую запись
            val uniqueKey = "item_${System.currentTimeMillis()}"

            // Преобразуем Bitmap в Base64
            val stream = ByteArrayOutputStream()
            item.image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageBytes = stream.toByteArray()
            val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Извлекаем цену за единицу блюда
            val numberRegex = Regex("(\\d+)")
            val numberMatch = numberRegex.find(item.priceWithRub)
            val priceWithRub = numberMatch?.value?.toIntOrNull() ?: 0

            // Сохраняем данные
            editor.putString("${uniqueKey}_name", item.text) // Название блюда
            editor.putString(
                "${uniqueKey}_pricePerItem",
                "$priceWithRub руб"
            ) // Цена за единицу (неизменная)
            editor.putString(
                "${uniqueKey}_priceWithRub",
                "$priceWithRub руб"
            ) // Начальная общая стоимость (цена за единицу * 1)
            editor.putString("${uniqueKey}_image", imageBase64) // Изображение в Base64
            editor.putInt("${uniqueKey}_quantity", 1) // Начальное количество
        }
        editor.apply()
    }

    private fun findExistingItemKey(
        sharedPreferences: SharedPreferences,
        itemName: String
    ): String? {
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.endsWith("_name") && value == itemName) {
                // Возвращаем ключ без суффикса "_name"
                return key.replace("_name", "")
            }
        }
        return null
    }
}