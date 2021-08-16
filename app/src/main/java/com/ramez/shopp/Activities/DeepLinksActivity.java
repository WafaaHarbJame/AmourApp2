package com.ramez.shopp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;

import java.util.ArrayList;
import java.util.List;

public class DeepLinksActivity extends ActivityBase {
    ArrayList<CategoryModel> categoryModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        categoryModelList = new ArrayList<>();


        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
            categoryModelList = UtilityApp.getCategories();
        }


        if (data != null && data.getPathSegments() != null) {

            List<String> list = data.getPathSegments();
            if (list.size() > 0) {
                Log.i("Log all list ", "" + list.toString());
                Log.i("Log size", "" + list.size());
                Log.i("Log data", "" + data);
                Log.i("Log data getPath ", "" + data.getPath());
                Log.i("Log data", "" + data.getHost());
                Log.i("Log getHost", "" + data.getHost());
                Log.i("Log segment1", "" + list.get(0));
                // Log.i("Log segment1", "" + list.get(1));


                if (list.size() == 1 && list.get(0).equals("category")) {

                    //https://ramezshopping.com/category

                    Intent intent = new Intent(getActiviy(), MainActivity.class);
//                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORIES);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                if (list.size() == 2 && list.get(0).equals("category")) {
                    //https://ramezshopping.com/category/233
                    int categoryId = Integer.parseInt(list.get(1));
//                    CategoryModel categoryModel = new CategoryModel();
//                    categoryModel.setId(categoryId);
                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORY_DETAILS);
//                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.SUB_CAT_ID, categoryId);
//                    intent.putExtra(Constants.CAT_MODEL, categoryModel);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 4 && list.get(0).equals("product")) {

                    //https://ramezshopping.com/product/12557/store/7263
                    ProductModel productModel = new ProductModel();
                    int productId = Integer.parseInt(list.get(1));
                    int storeId = Integer.parseInt(list.get(3));
                    productModel.setId(productId);
                    productModel.setStoreId(storeId);
                    Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
                    intent.putExtra(Constants.DB_productModel, productModel);
                    intent.putExtra(Constants.storeId, storeId);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 4 && list.get(0).equals("brochures")) {

                    //https://ramezshopping.com/brochures/29/store/7263

                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    int bookletId = Integer.parseInt(list.get(1));
                    int storeId = Integer.parseInt(list.get(3));
                    BookletsModel bookletsModel = new BookletsModel();
                    bookletsModel.setId(bookletId);
                    bookletsModel.setStoreID(storeId);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE);
//                    intent.putExtra(Constants.TO_BROSHER, true);
                    intent.putExtra(Constants.bookletsModel, bookletsModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 5 && list.get(0).equals("brochures")) {

                    /// https://ramezshopping.com/brochures/58/bahrain/store/7263

                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    int bookletId = Integer.parseInt(list.get(1));
                    int storeId = Integer.parseInt(list.get(4));
                    BookletsModel bookletsModel = new BookletsModel();
                    bookletsModel.setId(bookletId);
                    bookletsModel.setStoreID(storeId);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE);
//                    intent.putExtra(Constants.TO_BROSHER, true);
                    intent.putExtra(Constants.bookletsModel, bookletsModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 1 && list.get(0).equals("brands")) {

                    // https://ramezshopping.com/brands

                    Intent intent = new Intent(getActiviy(), AllBookleteActivity.class);
                    intent.putExtra(Constants.Activity_type, Constants.BRANDS);
                    intent.putExtra(Constants.isNotify, true);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 2 && list.get(0).equals("brands")) {

                    //https://ramezshopping.com/brands/bh

                    Intent intent = new Intent(getActiviy(), AllBookleteActivity.class);
                    intent.putExtra(Constants.Activity_type, Constants.BRANDS);
                    intent.putExtra(Constants.isNotify, true);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 6 && list.get(0).equals("products")) {

                    //https://ramezshopping.com/products/brand/RAMEZ/bh/store/7263?brand=1

                    if (data.getQueryParameter("brand") != null) {
                        Log.i("segment brand", "Log brand" + data.getQueryParameter("brand"));
                    }

                    Intent intent = new Intent(getActiviy(), AllListActivity.class);
                    intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands));
                    intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter);
                    int brandId = Integer.parseInt(data.getQueryParameter("brand"));
                    intent.putExtra(Constants.brand_id, brandId);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 3 && list.get(0).equals("products")) {

                    // https://ramezshopping.com/products/brand/RAMEZ?brand=1

                    if (data.getQueryParameter("brand") != null) {
                        Log.i("segment brand", "Log brand" + data.getQueryParameter("brand"));
                    }

                    Intent intent = new Intent(getActiviy(), AllListActivity.class);
                    intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands));
                    intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter);
                    if(data.getQueryParameter("brand")!=null){
                        int brandId = Integer.parseInt(data.getQueryParameter("brand"));
                        intent.putExtra(Constants.brand_id, brandId);
                        intent.putExtra(Constants.isNotify, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                } else if (list.size() == 5 && list.get(0).equals("products")) {

                    //https://ramezshopping.com/products/search/bh/store/7263?text=AL-ZAHRAH%20SOAP%20FLAKES%20840GR

                    if (data.getQueryParameter("text") != null) {
                        Log.i("segment search", "Log search" + data.getQueryParameter("text"));
                    }

                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    String text = data.getQueryParameter("text").replace("%", " ");
                    intent.putExtra(Constants.inputType_text, text);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_SEARCH);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();


                }


                if (list.size() == 3 && list.get(0).equals("category")) {
                    //https://ramezshopping.com/category/241/Fruits

                    int categoryId = Integer.parseInt(list.get(1));
//                    CategoryModel categoryModel = new CategoryModel();
//                    categoryModel.setId(categoryId);
                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORY_DETAILS);
//                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.SUB_CAT_ID, categoryId);
//                    intent.putExtra(Constants.CAT_MODEL, categoryModel);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                if (list.size() == 6 && list.get(0).equals("category")) {

                    //https://ramezshopping.com/category/233/Fruits%20&%20Vegetables/bh/store/7263

                    int categoryId = Integer.parseInt(list.get(1));
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setId(categoryId);
                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_CATEGORY_DETAILS);
//                    intent.putExtra(Constants.category, true);
                    intent.putExtra(Constants.SUB_CAT_ID, categoryId);
//                    intent.putExtra(Constants.CAT_MODEL, categoryModel);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 6 && list.get(0).equals("product")) {
                    //https://ramezshopping.com/product/1538/tomato-jordani-1-kg/bh/store/7263
                    ProductModel productModel = new ProductModel();
                    int productId = Integer.parseInt(list.get(1));
                    int storeId = Integer.parseInt(list.get(5));
                    productModel.setId(productId);
                    productModel.setStoreId(storeId);
                    Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
                    intent.putExtra(Constants.DB_productModel, productModel);
                    intent.putExtra(Constants.storeId, storeId);
                    intent.putExtra(Constants.isNotify, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (list.size() == 5 && list.get(0).equals("products") && list.get(1).equals("offered")) {

                    //https://ramezshopping.com/products/offered/bh/store/7263

                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_OFFERS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


            } else {
                Intent intent = new Intent(getActiviy(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }


        }


    }

}