package com.example.omlaut_mobile_app;

import android.app.Activity;
import android.content.Intent
import android.view.MenuItem

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

class navigation_initialaizer(
        private val activity:Activity,
        private val drawerLayout:DrawerLayout,
        private val navigationView:NavigationView
) {

    fun init() {
        setupDrawerContent()
    }

    private fun setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
                selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        val intent: Intent? = when (menuItem.itemId) {
            R.id.main_menu -> Intent(activity, MainActivity::class.java)
            R.id.login -> Intent(activity, LoginActivity::class.java)
            // добавьте остальные case для других активностей
            else -> null
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(it)
            drawerLayout.closeDrawers()
        }
    }
}

