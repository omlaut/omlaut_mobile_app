package com.example.omlaut_mobile_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.GravityCompat
import com.example.omlaut_mobile_app.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var drawerLayout: DrawerLayout
    private lateinit var contentBinding: navigation_initialaizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val headers = navigation_initialaizer(this, binding.Drawer, binding.mainNavigationView.navigationView)
        headers.init()
        setContentView(binding.root)
        binding.apply {
            mainHeader.navBarButton.setOnClickListener {
                Drawer.openDrawer(GravityCompat.START)
            }
        }
    }
}
