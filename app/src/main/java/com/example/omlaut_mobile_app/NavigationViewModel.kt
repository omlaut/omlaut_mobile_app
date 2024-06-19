package com.example.omlaut_mobile_app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class NavigationViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val SOME_INT_KEY = "some_int"
    }

    var someInt: Int
        get() = savedStateHandle[SOME_INT_KEY] ?: 0
        set(value) {
            savedStateHandle[SOME_INT_KEY] = value
        }
}
