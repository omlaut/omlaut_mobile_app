package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private val page_name = "login"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val emailPhoneEditText = findViewById<EditText>(R.id.et_login_email_phone)
        val passwordEditText = findViewById<EditText>(R.id.et_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)

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

        val headerButton = findViewById<ImageView>(R.id.main_header_side_button)
        headerButton.setOnClickListener {
            val drawerLayout = findViewById<DrawerLayout>(R.id.Drawer)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        loginButton.setOnClickListener {
            val emailPhone = emailPhoneEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (emailPhone.isNotEmpty() && password.isNotEmpty()) {
                // Логика логина пользователя

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // Переход на страницу меню
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()

                // Выполнение тестового запроса
                performTestRequest()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performTestRequest() {
        val request = Request.Builder()
            .url("https://8.8.8.8")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                println("Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println("Response: $responseBody")
                } else {
                    println("Response not successful: ${response.code}")
                }
            }
        })
    }
}
