package com.example.omlaut_mobile_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val items: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productWeight: TextView = itemView.findViewById(R.id.product_weight)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.name
        holder.productWeight.text = item.weight
        holder.productDescription.text = item.description
        holder.productPrice.text = "${item.price} zl"
        holder.productQuantity.text = "1 szt" // Пока фиксированное количество
        // Загрузить изображение продукта, если оно есть
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
