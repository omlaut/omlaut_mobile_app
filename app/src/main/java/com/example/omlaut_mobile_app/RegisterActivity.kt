package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.et_name)
        val phoneEditText = findViewById<EditText>(R.id.et_phone)
        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val dayEditText = findViewById<EditText>(R.id.et_day)
        val monthEditText = findViewById<EditText>(R.id.et_month)
        val yearEditText = findViewById<EditText>(R.id.et_year)
        val registerButton = findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val day = dayEditText.text.toString()
            val month = monthEditText.text.toString()
            val year = yearEditText.text.toString()

            var isValid = true

            isValid = validateField(nameEditText, name) && isValid
            isValid = validateField(phoneEditText, phone) && isValid
            isValid = validateField(emailEditText, email) && isValid
            isValid = validateField(passwordEditText, password) && isValid
            isValid = validateField(dayEditText, day) && isValid
            isValid = validateField(monthEditText, month) && isValid
            isValid = validateField(yearEditText, year) && isValid

            if (isValid) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateField(editText: EditText, value: String): Boolean {
        return if (value.isEmpty()) {
            editText.setBackgroundResource(R.drawable.edit_text_error_background)
            editText.setHintTextColor(resources.getColor(R.color.red))
            false
        } else {
            editText.setBackgroundResource(R.drawable.bottom_border)
            true
        }
    }
}
