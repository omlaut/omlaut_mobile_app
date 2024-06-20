package com.example.omlaut_mobile_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {
    private val page_name = "Catalog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

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

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ProductAdapter(getDummyProducts()) { product ->
            val fragment = ProductDetailsFragment.newInstance(product)
            fragment.show(supportFragmentManager, "productDetails")
        }
    }

    private fun getDummyProducts(): List<Product> {
        return listOf(
            Product("Malinowy biskopt z ziolami", 10, "120g", "Экстра вкус нашего торта Наполеон, удивит вас. Французское пирожное."),
            Product("Malinowy latte", 15, "220ml", "без сиропа; без лактозы"),
            Product("Malinowy biskopt z ziolami", 20, "250gr", "Экстра вкус нашего торта Наполеон, удивит вас. Французское пирожное.")
        )
    }
}