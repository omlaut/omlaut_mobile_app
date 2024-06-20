package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class NavigationDrawerFragment : Fragment() {
    private lateinit var navigationView: NavigationView
    private var someInt: Int = 0

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
        setupNavigation()

        // Включаем сохранение состояния фрагмента
        retainInstance = true

        // Восстанавливаем состояние, если есть сохранённые данные
        savedInstanceState?.let {
            someInt = it.getInt("some_int", 0)
        }

        return view
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем состояние
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
                else -> false
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(requireContext(), activityClass)
        startActivity(intent)
        requireActivity().finish() // Закрываем текущую активность при переходе
    }
}
