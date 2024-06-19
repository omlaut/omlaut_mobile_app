package com.example.omlaut_mobile_app;

import android.app.Activity;
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * NavigationInitializer is a helper class responsible for setting up the navigation drawer
 * and the header of the activity. It configures the drawer menu items and handles user authentication status.
 *
 * @property context Context of the activity where the navigation drawer is used.
 * @property pageName Name of the current page to be displayed in the header.
 */
class NavigationInitialaizer(
    private val activity: Activity,
    private val pageName: String
) {

    fun init() {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

        val drawerLayout = rootView.findViewById<DrawerLayout>(R.id.Drawer)
        val navigationView = rootView.findViewById<NavigationView>(R.id.main_navigation_view)
        Log.d("sdsg", "" + drawerLayout + navigationView)

        setupHeader()
        setupDrawerContent(activity, navigationView, drawerLayout)
    }

    private fun setupHeader() {
        Log.d("NavigationInitialaizer", "setupHeader called")

        val headerText = activity.findViewById<TextView>(R.id.main_header_text)
        headerText.text = pageName
        val headerButton = activity.findViewById<ImageView>(R.id.main_header_side_button)
        headerButton.setOnClickListener {
            val drawerLayout = activity.findViewById<DrawerLayout>(R.id.Drawer)
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupDrawerContent(activity: Activity, navigationView: NavigationView, drawerLayout: DrawerLayout) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(activity, menuItem, drawerLayout)
            true
        }
    }

    private fun selectDrawerItem(activity: Activity, menuItem: MenuItem, drawerLayout: DrawerLayout) {
        val intent: Intent? = when (menuItem.itemId) {
            R.id.link_main_menu_menu -> Intent(activity, MainActivity::class.java)
            R.id.link_catalog_menu -> Intent(activity, MenuActivity::class.java)
            R.id.link_login_menu -> Intent(activity, LoginActivity::class.java)
            else -> null
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(it)
            drawerLayout.closeDrawers()
        }
    }
//
//    private fun isUserLoggedIn(): Boolean {
//        val userSession = UserSession(context)
//        return userSession.isLoggedIn()
//    }
}

