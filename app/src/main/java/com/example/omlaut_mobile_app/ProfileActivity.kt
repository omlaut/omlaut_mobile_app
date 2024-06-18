package com.example.omlaut_mobile_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
        val menuButton: ImageButton = findViewById(R.id.menu_button)

        // Установка тестовых данных
        profileName.text = "SAVELII"
        profileBirthDay.text = "03"
        profileBirthMonth.text = "07"
        profileBirthYear.text = "2003"
        profilePhone.text = "+48728091017"
        profileEmail.text = "sovvva7@gmail.com"

        // Добавление обработчиков для кнопок
        orderHistoryButton.setOnClickListener {
            Toast.makeText(this, "История заказов", Toast.LENGTH_SHORT).show()
            // Логика для открытия истории заказов
        }

        logoutButton.setOnClickListener {
            Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_SHORT).show()
            // Логика для выхода из аккаунта
        }

        deleteAccountButton.setOnClickListener {
            Toast.makeText(this, "Удаление аккаунта", Toast.LENGTH_SHORT).show()
            // Логика для удаления аккаунта
        }

        menuButton.setOnClickListener {
            // Логика для открытия бокового меню
            Toast.makeText(this, "Открытие меню", Toast.LENGTH_SHORT).show()
        }
    }
}
