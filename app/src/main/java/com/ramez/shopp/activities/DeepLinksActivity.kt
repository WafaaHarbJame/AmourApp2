package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ramez.shopp.Classes.CategoryModel
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.BookletsModel
import com.ramez.shopp.Models.ProductModel
import com.ramez.shopp.R
import java.util.ArrayList


class DeepLinksActivity : ActivityBase() {
    var categoryModelList: ArrayList<CategoryModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.data
        categoryModelList = ArrayList()
        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
            categoryModelList = UtilityApp.getCategories()
        }
        if (data != null && data.pathSegments != null) {
            val list = data.pathSegments
            if (list.size > 0) {
                Log.i("Log all list ", "" + list.toString())
                Log.i("Log size", "" + list.size)
                Log.i("Log data", "" + data)
                Log.i("Log data getPath ", "" + data.path)
                Log.i("Log data", "" + data.host)
                Log.i("Log getHost", "" + data.host)
                Log.i("Log segment1", "" + list[0])
                // Log.i("Log segment1", "" + list.get(1));
                if (list.size == 1 && list[0] == "category") {

                    //https://ramezshopping.com/category
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    //                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORIES)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 2 && list[0] == "category") {
                    //https://ramezshopping.com/category/233
                    //https://ramezshopping.com/category/bh
                    val categoryId = list[1].toIntOrNull() ?: 0
                    //                    CategoryModel categoryModel = new CategoryModel();
//                    categoryModel.setId(categoryId);
                    if (categoryId == 0 && list[1].lowercase() != UtilityApp.getLocalData()?.shortname?.lowercase()) {
                        Toast(R.string.this_not_from_your_country)
                        finish()
                        return
                    }
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    val fragType =
                        if (categoryId != 0) Constants.FRAG_CATEGORY_DETAILS else Constants.FRAG_CATEGORIES
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, fragType)
                    //                    intent.putExtra(Constants.category, true);
                    if (categoryId != 0)
                        intent.putExtra(Constants.SUB_CAT_ID, categoryId)
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 4 && list[0] == "product") {

                    //https://ramezshopping.com/product/12557/store/7263
                    val productModel = ProductModel()
                    val productId = list[1].toInt()
                    val storeId = list[3].toInt()
                    productModel.id = productId
                    productModel.storeId = storeId
                    val intent = Intent(activiy, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.DB_productModel, productModel)
                    intent.putExtra(Constants.storeId, storeId)
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 4 && list[0] == "brochures") {

                    //https://ramezshopping.com/brochures/29/store/7263
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    val bookletId = list[1].toInt()
                    val storeId = list[3].toInt()
                    val bookletsModel = BookletsModel()
                    bookletsModel.id = bookletId
                    bookletsModel.storeID = storeId
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE)
                    //                    intent.putExtra(Constants.TO_BROSHER, true);
                    intent.putExtra(Constants.bookletsModel, bookletsModel)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 5 && list[0] == "brochures") {

                    /// https://ramezshopping.com/brochures/58/bahrain/store/7263
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    val bookletId = list[1].toInt()
                    val storeId = list[4].toInt()
                    val bookletsModel = BookletsModel()
                    bookletsModel.id = bookletId
                    bookletsModel.storeID = storeId
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE)
                    //                    intent.putExtra(Constants.TO_BROSHER, true);
                    intent.putExtra(Constants.bookletsModel, bookletsModel)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 1 && list[0] == "brands") {

                    // https://ramezshopping.com/brands
                    val intent = Intent(activiy, AllBookleteActivity::class.java)
                    intent.putExtra(Constants.Activity_type, Constants.BRANDS)
                    intent.putExtra(Constants.isNotify, true)
                    startActivity(intent)
                    finish()
                } else if (list.size == 2 && list[0] == "brands") {

                    //https://ramezshopping.com/brands/bh
                    val intent = Intent(activiy, AllBookleteActivity::class.java)
                    intent.putExtra(Constants.Activity_type, Constants.BRANDS)
                    intent.putExtra(Constants.isNotify, true)
                    startActivity(intent)
                    finish()
                } else if (list.size == 6 && list[0] == "products") {

                    //https://ramezshopping.com/products/brand/RAMEZ/bh/store/7263?brand=1
                    if (data.getQueryParameter("brand") != null) {
                        Log.i("segment brand", "Log brand" + data.getQueryParameter("brand"))
                    }
                    val intent = Intent(activiy, AllListActivity::class.java)
                    intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands))
                    intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter)
                    val brandId = data.getQueryParameter("brand")!!.toInt()
                    intent.putExtra(Constants.brand_id, brandId)
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 3 && list[0] == "products") {

                    // https://ramezshopping.com/products/brand/RAMEZ?brand=1
                    if (data.getQueryParameter("brand") != null) {
                        Log.i("segment brand", "Log brand" + data.getQueryParameter("brand"))
                    }
                    val intent = Intent(activiy, AllListActivity::class.java)
                    intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands))
                    intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter)
                    if (data.getQueryParameter("brand") != null) {
                        val brandId = data.getQueryParameter("brand")!!.toInt()
                        intent.putExtra(Constants.brand_id, brandId)
                        intent.putExtra(Constants.isNotify, true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else if (list.size == 5 && list[0] == "products") {

                    //https://ramezshopping.com/products/search/bh/store/7263?text=AL-ZAHRAH%20SOAP%20FLAKES%20840GR
                    if (data.getQueryParameter("text") != null) {
                        val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                        Log.i("segment search", "Log search" + data.getQueryParameter("text"))
                        val text = data.getQueryParameter("text")!!.replace("%", " ")
                        intent.putExtra(Constants.inputType_text, text)
                        intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_SEARCH)
                        intent.putExtra(Constants.isNotify, true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else if (list.size == 3 && list[0] == "category") {
                    //https://ramezshopping.com/category/241/Fruits
                    val categoryId = list[1].toInt()
                    //                    CategoryModel categoryModel = new CategoryModel();
//                    categoryModel.setId(categoryId);
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORY_DETAILS)
                    //                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.SUB_CAT_ID, categoryId)
                    //                    intent.putExtra(Constants.CAT_MODEL, categoryModel);
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 6 && list[0] == "category") {

                    //https://ramezshopping.com/category/233/Fruits%20&%20Vegetables/bh/store/7263
                    val categoryId = list[1].toInt()
                    val categoryModel = CategoryModel()
                    categoryModel.id = categoryId
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORY_DETAILS)
                    //                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.SUB_CAT_ID, categoryId)
                    //                    intent.putExtra(Constants.CAT_MODEL, categoryModel);
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 6 && list[0] == "product") {
                    //https://ramezshopping.com/product/1538/tomato-jordani-1-kg/bh/store/7263
                    val productModel = ProductModel()
                    val productId = list[1].toInt()
                    val storeId = list[5].toInt()
                    productModel.id = productId
                    productModel.storeId = storeId
                    val intent = Intent(activiy, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.DB_productModel, productModel)
                    intent.putExtra(Constants.storeId, storeId)
                    intent.putExtra(Constants.isNotify, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else if (list.size == 5 && list[0] == "products" && list[1] == "offered") {

                    //https://ramezshopping.com/products/offered/bh/store/7263
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_OFFERS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
}