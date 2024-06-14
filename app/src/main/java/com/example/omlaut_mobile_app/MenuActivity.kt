package com.example.omlaut_mobile_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ProductAdapter(getDummyProducts())
    }

    private fun getDummyProducts(): List<Product> {
        return listOf(
            Product("Malinowy biskopt z ziolami", "od 8 zl", "120g"),
            Product("Malinowy biskopt z ziolami", "od 8 zl", "120g"),
            Product("Malinowy biskopt z ziolami", "od 8 zl", "120g"),
            Product("Malinowy biskopt z ziolami", "od 8 zl", "120g")
        )
    }
}
