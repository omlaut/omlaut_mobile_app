package com.example.omlaut_mobile_app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException

class CartActivity : AppCompatActivity() {
    private val page_name = "Cart"
    private val client = OkHttpClient()
    private val gson = Gson()
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var products: MutableList<Product>
    private lateinit var itemIdMap: MutableMap<Product, Int>
    private var cartId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sessionManager = SessionManager(this)

        val headerButton = findViewById<ImageView>(R.id.main_header_side_button)
        headerButton.setOnClickListener {
            val drawerLayout = findViewById<DrawerLayout>(R.id.Drawer)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        if (savedInstanceState == null) {
            val bundle = bundleOf("some_int" to 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_navigation_view, NavigationDrawerFragment().apply {
                    arguments = bundle
                })
                .commit()
            println("lala")
        }

        val headerText = findViewById<TextView>(R.id.main_header_text)
        headerText.text = page_name

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        totalPriceTextView = findViewById(R.id.total_price)


        findViewById<Button>(R.id.continue_shopping_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.pay_button).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.clear_cart_button).setOnClickListener {
            confirmAndClearCart()
        }

        fetchCartItems()
    }

    private fun fetchCartItems() {
        val authToken = sessionManager.fetchAuthToken()
        val url = "http://5.22.223.21/products/cart"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "$authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {

                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        try {
                            val responseString = responseBody.string()
                            val cartJsonObject = gson.fromJson(responseString, JsonObject::class.java)
                            val cartJsonArray = cartJsonObject.getAsJsonArray("cart")

                            products = mutableListOf()
                            itemIdMap = mutableMapOf()

                            for (cartElement in cartJsonArray) {
                                val cartObject = cartElement.asJsonObject
                                cartId = cartObject.getAsJsonObject("cart").get("id").asInt
                                val itemsArray = cartObject.getAsJsonArray("items")

                                for (itemElement in itemsArray) {
                                    val itemObject = itemElement.asJsonObject
                                    val id = itemObject.get("id")?.asString ?: ""
                                    val itemId = itemObject.get("id").asInt
                                    val name = itemObject.get("name")?.asString ?: ""
                                    val price = itemObject.get("price")?.asInt ?: 0
                                    val weight = itemObject.get("weight")?.asString ?: ""
                                    val description = itemObject.get("description")?.asString ?: ""
                                    val product = Product(id, name, price, "$weight g.", description)
                                    products.add(product)
                                    itemIdMap[product] = itemId
                                }
                            }

                            runOnUiThread {
                                recyclerView.adapter = CartAdapter(products) { product ->
                                    confirmAndDeleteItem(product)
                                }
                                totalPriceTextView.text = "suma: ${calculateTotalPrice(products)} zl"
                            }
                        } catch (e: JsonSyntaxException) {

                        }
                    }
                } else {

                }
            }
        })
    }

    private fun calculateTotalPrice(products: List<Product>): Int {
        var totalPrice = 0
        for (product in products) {
            totalPrice += product.price
        }
        return totalPrice
    }

    private fun confirmAndDeleteItem(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Usuwanie produktu")
            .setMessage("Czy na pewno chcesz usunąć ten produkt z koszyka?")
            .setPositiveButton("Да") { _, _ -> deleteItem(product) }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun deleteItem(product: Product) {
        val authToken = sessionManager.fetchAuthToken()
        val url = "http://5.22.223.21/products/cart/remove"
        val itemId = itemIdMap[product]
        val requestBody = FormBody.Builder()
            .add("cart_item_id", itemId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "$authToken")
            .delete(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        products.remove(product)
                        itemIdMap.remove(product)
                        recyclerView.adapter?.notifyDataSetChanged()
                        totalPriceTextView.text = "suma: ${calculateTotalPrice(products)} zl"
                    }
                } else {
                }
            }
        })
    }

    private fun confirmAndClearCart() {
        AlertDialog.Builder(this)
            .setTitle("Czyszczenie Kosza")
            .setMessage("Czy na pewno chcesz opróżnić cały kosz?")
            .setPositiveButton("Да") { _, _ -> clearCart() }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun clearCart() {
        val authToken = sessionManager.fetchAuthToken()
        val url = "http://5.22.223.21/products/cart/clear"
        val requestBody = FormBody.Builder()
            .add("cart_id", cartId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "$authToken")
            .delete(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        products.clear()
                        itemIdMap.clear()
                        recyclerView.adapter?.notifyDataSetChanged()
                        totalPriceTextView.text = "suma: 0 zl"
                    }
                } else {
                }
            }
        })
    }
}
