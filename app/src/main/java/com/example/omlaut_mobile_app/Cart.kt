package com.example.omlaut_mobile_app

object Cart {
    private val items = mutableListOf<Product>()

    fun addItem(item: Product) {
        items.add(item)
    }

    fun getItems(): List<Product> {
        return items
    }

    fun clear() {
        items.clear()
    }
}
