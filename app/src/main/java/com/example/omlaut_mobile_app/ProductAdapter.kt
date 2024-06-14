package com.example.omlaut_mobile_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Product(val name: String, val price: String, val weight: String, val description: String = "Экстра вкус нашего торта Наполеон, удивит вас. Французское пирожное.")

class ProductAdapter(private val products: List<Product>, private val onProductClick: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price
        holder.productWeight.text = product.weight
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
        // Для изображения можно использовать любую библиотеку для загрузки изображений, например, Glide или Picasso
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productWeight: TextView = itemView.findViewById(R.id.product_weight)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
    }
}
