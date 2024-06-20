package com.example.omlaut_mobile_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.*
import java.io.IOException

class ProductDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var product: Product
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Product(
                id = it.getString("productId") ?: "",
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

        productNameTextView.text = product.name
        productWeightTextView.text = product.weight
        productDescriptionTextView.text = product.description
        productPriceTextView.text = "${product.price} zl"

        orderButton.setOnClickListener {
            addProductToCart(product)
            dismiss()
        }
    }

    private fun addProductToCart(product: Product) {
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.fetchAuthToken()
        val url = "http://5.22.223.21:80/products/cart/add"

        val requestBody = FormBody.Builder()
            .add("product_id", product.id)
            .add("quantity", "1")
            .add("order_time", "15:51:30")
            .add("order_date", "2024-12-12")
            .add("address", "Twój adres")
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token ?: "")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Błąd podczas dodawania do koszyka: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Produkt dodany do koszyka", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Błąd podczas dodawania do koszyka: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("productId", product.id)
                    putString("productName", product.name)
                    putString("productWeight", product.weight)
                    putString("productDescription", product.description)
                    putString("productPrice", product.price.toString())
                }
            }
    }
}
