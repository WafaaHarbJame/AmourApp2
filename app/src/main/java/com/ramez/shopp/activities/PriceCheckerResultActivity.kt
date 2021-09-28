package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.Models.ProductModel
import com.ramez.shopp.adapter.SimilierProductAdapter
import com.ramez.shopp.databinding.ActivityPriceCheckerResultBinding
import java.util.*


class PriceCheckerResultActivity : ActivityBase(), SimilierProductAdapter.OnItemClick {
    var binding: ActivityPriceCheckerResultBinding? = null
    var productList: ArrayList<ProductModel>? = null
    private var adapter: SimilierProductAdapter? = null
    private var scanLauncher: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPriceCheckerResultBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        title = ""
        val linearLayoutManager = LinearLayoutManager(activiy, RecyclerView.HORIZONTAL, false)
        binding!!.offerRecycler.layoutManager = linearLayoutManager
        binding!!.offerRecycler.setHasFixedSize(true)
        binding!!.offerRecycler.itemAnimator = null
        productList = ArrayList()
        productList!!.add(ProductModel())
        productList!!.add(ProductModel())
        productList!!.add(ProductModel())
        productList!!.add(ProductModel())
        productList!!.add(ProductModel())
        initAdapter()
        scanLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {
            }
        }
        binding!!.scanAgainBut.setOnClickListener { v ->
            val intent = Intent(activiy, FullScannerActivity::class.java)
            scanLauncher!!.launch(intent)
        }
    }


    private fun initAdapter() {
        adapter = SimilierProductAdapter(activiy, productList, this, 0)
        binding!!.offerRecycler.adapter = adapter
    }

    override fun onItemClicked(position: Int, productModel: ProductModel) {}
}