package com.example.omlaut_mobile_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private val page_name = "Profile"
    private val client = OkHttpClient()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

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

        // Получение ссылок на элементы интерфейса
        val profileName: TextView = findViewById(R.id.profile_name)
        val profileBirthDay: TextView = findViewById(R.id.profile_birth_day)
        val profileBirthMonth: TextView = findViewById(R.id.profile_birth_month)
        val profileBirthYear: TextView = findViewById(R.id.profile_birth_year)
        val profilePhone: TextView = findViewById(R.id.profile_phone)
        val profileEmail: TextView = findViewById(R.id.profile_email)
        val orderHistoryButton: Button = findViewById(R.id.profile_order_history_button)
        val logoutButton: TextView = findViewById(R.id.logout_button)
        val deleteAccountButton: TextView = findViewById(R.id.delete_account_button)

        // Обработчики для кнопок
        orderHistoryButton.setOnClickListener {
            Toast.makeText(this, "История заказов", Toast.LENGTH_SHORT).show()
            // Логика для открытия истории заказов
        }

        logoutButton.setOnClickListener {
            logout()
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmation()
        }

        // Выполнение запроса данных пользователя
        fetchUserProfile(profileName, profileBirthDay, profileBirthMonth, profileBirthYear, profilePhone, profileEmail)
    }

    private fun fetchUserProfile(
        profileName: TextView,
        profileBirthDay: TextView,
        profileBirthMonth: TextView,
        profileBirthYear: TextView,
        profilePhone: TextView,
        profileEmail: TextView
    ) {
        val authToken = sessionManager.fetchAuthToken()

        val request = Request.Builder()
            .url("http://5.22.223.21:80/users/get")
            .get()
            .addHeader("Authorization", "$authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Failed to fetch profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody).getJSONObject("message")
                    val name = jsonObject.getString("name")
                    val birthDate = jsonObject.getString("date_of_birth")
                    val phone = jsonObject.getString("Phone_number")
                    val email = jsonObject.getString("email")

                    // Разделение даты рождения на день, месяц и год
                    val birthDateParts = birthDate.split("-")
                    val year = birthDateParts[0]
                    val month = birthDateParts[1]
                    val day = birthDateParts[2]

                    runOnUiThread {
                        profileName.text = name
                        profileBirthDay.text = day
                        profileBirthMonth.text = month
                        profileBirthYear.text = year
                        profilePhone.text = phone
                        profileEmail.text = email
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Failed to fetch profile: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Account Deletion")
            .setMessage("Are you sure you want to delete the account?")
            .setPositiveButton("Yes") { dialog, which ->
                showFinalDeleteAccountConfirmation()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showFinalDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Account Deletion")
            .setMessage("Do you definitely want to delete the account?")
            .setPositiveButton("Yes") { dialog, which ->
                deleteAccount()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAccount() {
        val authToken = sessionManager.fetchAuthToken()

        val request = Request.Builder()
            .url("http://5.22.223.21:80/users/delete")
            .delete()
            .addHeader("Authorization", "$authToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ProfileActivity, "Failed to delete account: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Account successfully deleted", Toast.LENGTH_SHORT).show()
                        logout()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Failed to delete account: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun logout() {
        sessionManager.saveAuthToken("")
        Toast.makeText(this, "Account logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
