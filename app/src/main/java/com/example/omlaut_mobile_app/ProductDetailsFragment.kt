package com.example.omlaut_mobile_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProductDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Product(
                name = it.getString("productName") ?: "",
                weight = it.getString("productWeight") ?: "",
                description = it.getString("productDescription") ?: "",
                price = it.getString("productPrice")?.toIntOrNull() ?: 0
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productImage = view.findViewById<ImageView>(R.id.product_image)
        val productNameTextView = view.findViewById<TextView>(R.id.product_name)
        val productWeightTextView = view.findViewById<TextView>(R.id.product_weight)
        val productDescriptionTextView = view.findViewById<TextView>(R.id.product_description)
        val productPriceTextView = view.findViewById<TextView>(R.id.product_price)
        val orderButton = view.findViewById<Button>(R.id.btn_order)

        // Задайте данные продукта
        productNameTextView.text = product.name
        productWeightTextView.text = product.weight
        productDescriptionTextView.text = product.description
        productPriceTextView.text = "${product.price} zl"

        // Добавьте логику для кнопки заказа
        orderButton.setOnClickListener {
            Cart.addItem(product)
            dismiss()
            // Переход обратно в MenuActivity
            val intent = Intent(activity, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("productName", product.name)
                    putString("productWeight", product.weight)
                    putString("productDescription", product.description)
                    putString("productPrice", product.price.toString())
                }
            }
    }
}
