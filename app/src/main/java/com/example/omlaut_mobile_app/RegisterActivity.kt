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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private val page_name = "Register"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                val dateOfBirth = "$year-$month-$day"
                registerUser(name, phone, email, password, dateOfBirth)
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

    private fun registerUser(name: String, phone: String, email: String, password: String, dateOfBirth: String) {
        val json = """
            {
                "name": "$name",
                "phone_number": "$phone",
                "email": "$email",
                "password": "$password",
                "date_of_birth": "$dateOfBirth"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("http://5.22.223.21:80/users/create")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
