package com.example.omlaut_mobile_app

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.example.omlaut_mobile_app.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private val pageName = "main page"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val headers = NavigationInitialaizer(this, pageName)
        headers.init()
    }
}
