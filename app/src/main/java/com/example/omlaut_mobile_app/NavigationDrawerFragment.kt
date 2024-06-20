package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class NavigationDrawerFragment : Fragment() {
    private lateinit var navigationView: NavigationView
    private var someInt: Int = 0
    private lateinit var sessionManager: SessionManager

    companion object {
        private var instance: NavigationDrawerFragment? = null

        fun getInstance(): NavigationDrawerFragment {
            if (instance == null) {
                instance = NavigationDrawerFragment()
            }
            return instance!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_navigation_view, container, false)
        navigationView = view.findViewById(R.id.navigation_view)
        sessionManager = SessionManager(requireContext())
        setupNavigation()

        retainInstance = true

        savedInstanceState?.let {
            someInt = it.getInt("some_int", 0)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateNavigationHeader()
        updateMenuItems()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("some_int", someInt)
    }

    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.link_main_menu_menu -> {
                    navigateTo(MainActivity::class.java)
                    true
                }
                R.id.link_login_menu -> {
                    navigateTo(LoginActivity::class.java)
                    true
                }
                R.id.link_cart_menu -> {
                    navigateTo(CartActivity::class.java)
                    true
                }
                R.id.link_profile_manu -> {
                    navigateTo(ProfileActivity::class.java)
                    true
                }
                R.id.link_catalog_menu -> {
                    navigateTo(MenuActivity::class.java)
                    true
                }
                R.id.link_logout_menu -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.textView2)
        val userPhoneTextView = headerView.findViewById<TextView>(R.id.textView3)

        if (sessionManager.fetchAuthToken() != null) {
            val user = sessionManager.fetchUserDetails()
            userNameTextView.text = user?.name ?: "User"
            userPhoneTextView.text = user?.phone ?: "No phone"
            userPhoneTextView.visibility = View.VISIBLE
        } else {
            userNameTextView.text = "You are not logged in"
            userPhoneTextView.visibility = View.INVISIBLE
        }
    }

    private fun updateMenuItems() {
        val menu = navigationView.menu
        val isLoggedIn = sessionManager.fetchAuthToken() != null

        menu.findItem(R.id.link_login_menu).isVisible = !isLoggedIn
        menu.findItem(R.id.link_logout_menu).isVisible = isLoggedIn
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(requireContext(), activityClass)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun logout() {
        sessionManager.saveAuthToken(null)
        sessionManager.saveUserDetails(null)
        navigateTo(LoginActivity::class.java)
    }
}
