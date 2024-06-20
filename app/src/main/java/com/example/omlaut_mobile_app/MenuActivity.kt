package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException

class MenuActivity : AppCompatActivity() {
    private val page_name = "Catalog"
    private lateinit var recyclerView: RecyclerView
    private val client = OkHttpClient()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val sessionManager = SessionManager(this)
        val authToken = sessionManager.fetchAuthToken()

        if (authToken.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


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
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchProducts()
    }

    private fun fetchProducts() {
        val request = Request.Builder()
            .url("http://5.22.223.21:80/products/products")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MenuActivity", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        try {
                            val productsJsonObject = gson.fromJson(responseBody.string(), JsonObject::class.java)
                            val productsJsonArray = productsJsonObject.getAsJsonArray("products")
                            val products = productsJsonArray.map { jsonElement ->
                                val jsonObject = jsonElement.asJsonObject
                                val id = jsonObject.get("id").asString
                                val name = jsonObject.get("name").asString
                                val price = jsonObject.get("price").asInt
                                val weight = "${jsonObject.get("weight").asString} g."
                                val description = jsonObject.get("description").asString
                                Product(id, name, price, weight, description)
                            }

                            runOnUiThread {
                                recyclerView.adapter = ProductAdapter(products) { product ->
                                    val fragment = ProductDetailsFragment.newInstance(product)
                                    fragment.show(supportFragmentManager, "productDetails")
                                }
                            }
                        } catch (e: JsonSyntaxException) {
                            Log.e("MenuActivity", "Error parsing JSON: ${e.message}")
                        }
                    }
                } else {
                    Log.e("MenuActivity", "Error: ${response.code}")
                }
            }
        })
    }
}
