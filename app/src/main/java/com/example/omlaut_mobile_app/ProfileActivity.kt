package com.example.omlaut_mobile_app


import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.os.bundleOf
import java.util.Locale
import android.Manifest
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_STORAGE_PERMISSION = 101
    private val page_name = "Profile"
    private val client = OkHttpClient()
    private lateinit var sessionManager: SessionManager

    private lateinit var profilePhoto: ImageView
    private lateinit var changePhotoButton: Button
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)
        val authToken = sessionManager.fetchAuthToken()

        if (authToken.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

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

        val profileName: TextView = findViewById(R.id.profile_name)
        val profileBirthDay: TextView = findViewById(R.id.profile_birth_day)
        val profileBirthMonth: TextView = findViewById(R.id.profile_birth_month)
        val profileBirthYear: TextView = findViewById(R.id.profile_birth_year)
        val profilePhone: TextView = findViewById(R.id.profile_phone)
        val profileEmail: TextView = findViewById(R.id.profile_email)
        val orderHistoryButton: Button = findViewById(R.id.profile_order_history_button)
        val logoutButton: TextView = findViewById(R.id.logout_button)
        val deleteAccountButton: TextView = findViewById(R.id.delete_account_button)
        
        orderHistoryButton.setOnClickListener {
            Toast.makeText(this, "История заказов", Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmation()
        }

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

    private fun selectImage() {
        val options = arrayOf("Zrób zdjęcie", "Wybierz z galerii", "Anulowanie")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz zdjęcie profilowe")
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
                    savePhotoPath(currentPhotoPath)
                    val imageFile = File(currentPhotoPath)
                    val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    profilePhoto.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IOException("Could not access external storage")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
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
