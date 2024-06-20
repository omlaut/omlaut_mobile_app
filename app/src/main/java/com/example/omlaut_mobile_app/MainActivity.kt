package com.example.omlaut_mobile_app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private val page_name = "Glowna"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sessionManager = SessionManager(this)
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

        if (savedInstanceState == null) {
            val bundle = bundleOf("some_int" to 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_navigation_view, NavigationDrawerFragment().apply {
                    arguments = bundle
                })
                .commit()
        }

        val headerText = findViewById<TextView>(R.id.main_header_text)
        headerText.text = page_name

        // Promo slider setup
        val viewPager = findViewById<ViewPager>(R.id.promo_viewpager)
        val images = listOf(R.drawable.promo1, R.drawable.promo2, R.drawable.promo3) // Replace with your promo images
        val adapter = PromoAdapter(this, images)
        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        tabLayout.setupWithViewPager(viewPager, true)

        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as ViewGroup
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(16, 0, 16, 0)
            tab.requestLayout()
        }

        // Map setup
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Social media links setup
        setupSocialMediaLinks()
    }

    private fun setupSocialMediaLinks() {
        val facebookIcon = findViewById<ImageView>(R.id.facebook_icon)
        val instagramIcon = findViewById<ImageView>(R.id.instagram_icon)

        facebookIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/search/top?q=домашние%20торты%20ручной%20работы%20-%20omlaut"))
            startActivity(intent)
        }

        instagramIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/omlaut.warszawa?igsh=b3d5anNiNjhsbzdq"))
            startActivity(intent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Add markers for each location
        val locations = listOf(
            LatLng(52.203929, 21.014588), // Example location 1
            LatLng(52.231202, 21.002306)   // Example location 2
        )

        for (location in locations) {
            val markerOptions = MarkerOptions().position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker()))
            googleMap.addMarker(markerOptions)
        }

        // Move camera to the first location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 12f))
    }

    private fun createCustomMarker(): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        return Bitmap.createScaledBitmap(bitmap, 100, 100, false) // Resize the image as needed
    }
}
