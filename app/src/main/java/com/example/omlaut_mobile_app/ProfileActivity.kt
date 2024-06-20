package com.example.omlaut_mobile_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Locale
import android.Manifest

class ProfileActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_STORAGE_PERMISSION = 101
    private val page_name = "Profile"

    private lateinit var profilePhoto: ImageView
    private lateinit var changePhotoButton: Button
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val headerButton = findViewById<ImageView>(R.id.main_header_side_button)
        headerButton.setOnClickListener {
            val drawerLayout = findViewById<DrawerLayout>(R.id.Drawer)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        profilePhoto = findViewById(R.id.profile_photo)
        changePhotoButton = findViewById(R.id.change_photo_button)

        changePhotoButton.setOnClickListener {
            selectImage()
        }

        if (savedInstanceState == null) {
            loadSavedPhoto()
            val bundle = bundleOf("some_int" to 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_navigation_view, NavigationDrawerFragment().apply {
                    arguments = bundle
                })
                .commit()
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

        profileName.text = "profilename"
        profileBirthDay.text = "03"
        profileBirthMonth.text = "07"
        profileBirthYear.text = "2003"
        profilePhone.text = "+48728091017"
        profileEmail.text = "email@email.com"

        orderHistoryButton.setOnClickListener {
            Toast.makeText(this, "История заказов", Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_SHORT).show()
            // Логика для выхода из аккаунта
        }

        deleteAccountButton.setOnClickListener {
            Toast.makeText(this, "Удаление аккаунта", Toast.LENGTH_SHORT).show()
            // Логика для удаления аккаунта
        }
    }

    private fun selectImage() {
        val options = arrayOf("Сделать фото", "Выбрать из галереи", "Отмена")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите фотографию профиля")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (checkCameraPermission()) {
                        takePhotoFromCamera()
                    } else {
                        requestCameraPermission()
                    }
                }
                1 -> {
                    if (checkStoragePermission()) {
                        choosePhotoFromGallery()
                    } else {
                        requestStoragePermission()
                    }
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION
        )
    }

    private fun loadSavedPhoto() {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        currentPhotoPath = sharedPreferences.getString("photo_path", "") ?: ""

        if (currentPhotoPath.isNotEmpty()) {
            val imageFile = File(currentPhotoPath)
            if (imageFile.exists()) {
                val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                profilePhoto.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun getSavedPhotoPath(): String? {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("photo_path", null)
    }

    private fun savePhotoPath(path: String) {
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("photo_path", path).apply()
    }

    private fun takePhotoFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPhotoIntent ->
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Фотография сделана с камеры, сохраняем путь к файлу
                    savePhotoPath(currentPhotoPath)
                    val imageFile = File(currentPhotoPath)
                    val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    profilePhoto.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    // Фотография выбрана из галереи, сохраняем путь к файлу
                    data?.data?.let { uri ->
                        val imageFile = File(getRealPathFromURI(uri))
                        savePhotoPath(imageFile.absolutePath)
                        val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        profilePhoto.setImageBitmap(imageBitmap)
                    }
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return ""
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IOException("Could not access external storage")

        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    takePhotoFromCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required to take photo", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    choosePhotoFromGallery()
                } else {
                    Toast.makeText(this, "Storage permission is required to choose photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
