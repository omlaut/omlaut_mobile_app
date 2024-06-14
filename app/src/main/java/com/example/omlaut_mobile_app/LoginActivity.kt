package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailPhoneEditText = findViewById<EditText>(R.id.et_login_email_phone)
        val passwordEditText = findViewById<EditText>(R.id.et_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)

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
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
