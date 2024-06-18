package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CartAdapter(Cart.getItems())

        val totalPriceTextView = findViewById<TextView>(R.id.total_price)
        totalPriceTextView.text = "suma: ${calculateTotalPrice()}zl"

        // Установить слушатели для кнопок
        findViewById<Button>(R.id.continue_shopping_button).setOnClickListener {
            finish() // Возвращаемся назад к меню
        }

        findViewById<Button>(R.id.pay_button).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateTotalPrice(): Int {
        val cartItems = Cart.getItems()
        var totalPrice = 0
        for (item in cartItems) {
            totalPrice += item.price.toInt() // Предполагается, что цена хранится в строковом формате
        }
        return totalPrice
    }
}
