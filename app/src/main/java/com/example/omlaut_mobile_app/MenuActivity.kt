package com.example.omlaut_mobile_app

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MenuActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val menuButton = findViewById<ImageButton>(R.id.menu_button)

        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Логика перехода на главную страницу
                    true
                }
                R.id.nav_menu -> {
                    // Логика перехода на страницу меню
                    true
                }
                R.id.nav_cart -> {
                    // Логика перехода на страницу корзины
                    true
                }
                R.id.nav_profile -> {
                    // Логика перехода на страницу профиля
                    true
                }
                R.id.nav_language -> {
                    // Логика изменения языка
                    true
                }
                R.id.nav_logout -> {
                    // Логика выхода из аккаунта
                    true
                }
                R.id.nav_about -> {
                    // Логика перехода на страницу "о нас"
                    true
                }
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = ProductAdapter(getDummyProducts()) { product ->
            val fragment = ProductDetailsFragment.newInstance(product)
            fragment.show(supportFragmentManager, "productDetails")
        }
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
