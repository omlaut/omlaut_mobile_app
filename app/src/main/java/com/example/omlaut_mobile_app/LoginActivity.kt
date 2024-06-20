package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
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
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private val page_name = "login"
    private val client = OkHttpClient()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        val registerTextView = findViewById<TextView>(R.id.register_text_view)

        val spannableString = SpannableString("jeszcze nie ma konta? zarejestruj sie tutaj")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        val start = spannableString.indexOf("zarejestruj sie tutaj")
        val end = start + "zarejestruj sie tutaj".length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        registerTextView.text = spannableString
        registerTextView.movementMethod = LinkMovementMethod.getInstance()

        val emailEditText = findViewById<EditText>(R.id.et_login_email)
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
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://5.22.223.21:80/users/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("LoginActivity", "Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    if (jsonObject.has("accessToken")) {
                        val token = jsonObject.getString("accessToken")
                        sessionManager.saveAuthToken(token)

                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        handleLoginError(responseBody)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun handleLoginError(responseBody: String?) {
        runOnUiThread {
            val jsonObject = responseBody?.let { JSONObject(it) }
            val errorMessage = when (jsonObject?.getString("message")) {
                "User not found" -> "User not found"
                "Password is not valid" -> "Password is not valid"
                else -> "Login failed: Unknown error"
            }
            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
