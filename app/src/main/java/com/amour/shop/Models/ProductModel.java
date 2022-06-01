package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.amour.shop.classes.Constants;
import com.amour.shop.classes.UtilityApp;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable, Comparable<ProductModel> {
    @SerializedName("brand_id")
    @Expose
    private int brandId;
    @SerializedName("category_id")
    @Expose
    private int categoryId;
    @SerializedName("kind_id")
    @Expose
    private int kindId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("favourite")
    @Expose
    private boolean favourite;
    @SerializedName("h_description")
    @Expose
    private String hDescription;
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("rate")
    @Expose
    private int rate;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("product_brand")
    @Expose
    private ProductBrand productBrand;
    @SerializedName("product_barcodes")
    @Expose
    private List<ProductBarcode> productBarcodes = null;
    private int storeId;

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Object getKindId() {
        return kindId;
    }

    public void setKindId(int kindId) {
        this.kindId = kindId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String gethDescription() {
        return hDescription;
    }

    public void sethDescription(String hDescription) {
        this.hDescription = hDescription;
    }

    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
        this.hName = hName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public ProductBrand getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(ProductBrand productBrand) {
        this.productBrand = productBrand;
    }

    public List<ProductBarcode> getProductBarcodes() {
        return productBarcodes;
    }

    public void setProductBarcodes(List<ProductBarcode> productBarcodes) {
        this.productBarcodes = productBarcodes;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }



    @Override
    public int compareTo(ProductModel productModel) {

        if (getFirstProductBarcodes().getPrice() > productModel.getFirstProductBarcodes().getPrice()) {
            return 1;
        }
        else if (productModel.getFirstProductBarcodes().getPrice() <  getFirstProductBarcodes().getPrice()) {
            return -1;
        }
        else {
            return 0;
        }

    }

    public ProductBarcode getFirstProductBarcodes() {
        return productBarcodes!= null && productBarcodes.size()> 0 ?   productBarcodes.get(0) : new ProductBarcode() ;
    }

    public String getProductName() {
        if (UtilityApp.INSTANCE.getLanguage().equals(Constants.English)) {
            return name;

        } else {
            return hName;
        }
    }


//
//    @SerializedName("brand_id")
//    @Expose
//    private int brandId;
//    @SerializedName("category_id")
//    @Expose
//    private int categoryId;
//    @SerializedName("description")
//    @Expose
//    private String description;
//    @SerializedName("favourite")
//    @Expose
//    private boolean favourite;
//    @SerializedName("product_barcodes")
//    @Expose
//    private List<ProductBarcode> productBarcodes = null;
//    @SerializedName("h_description")
//    @Expose
//    private String hDescription;
//    @SerializedName("h_name")
//    @Expose
//    private String hName;
//    @SerializedName("id")
//    @Expose
//    private int id;
//    @SerializedName("images")
//    @Expose
//    private List<String> images = null;
//    @SerializedName("name")
//    @Expose
//    private String name;
//    @SerializedName("product_brand")
//    @Expose
//    private ProductBrand productBrand;
//    @SerializedName("rate")
//    @Expose
//    private int rate;
//    private int storeId;
//
//
//    public int getBrandId() {
//        return brandId;
//    }
//
//    public void setBrandId(int brandId) {
//        this.brandId = brandId;
//    }
//
//    public int getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(int categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public boolean isFavourite() {
//        return favourite;
//    }
//
//    public void setFavourite(boolean favourite) {
//        this.favourite = favourite;
//    }
//
//    public List<ProductBarcode> getProductBarcodes() {
//        return productBarcodes;
//    }
//
//    public ProductBarcode getFirstProductBarcodes() {
//        return productBarcodes!= null && productBarcodes.size()> 0 ?   productBarcodes.get(0) : new ProductBarcode() ;
//    }
//
//    public void setProductBarcodes(List<ProductBarcode> productBarcodes) {
//        this.productBarcodes = productBarcodes;
//    }
//
//    public String gethDescription() {
//        return hDescription;
//    }
//
//    public void sethDescription(String hDescription) {
//        this.hDescription = hDescription;
//    }
//
//    public String gethName() {
//        return hName;
//    }
//
//    public void sethName(String hName) {
//        this.hName = hName;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public List<String> getImages() {
//        return images;
//    }
//
//    public void setImages(List<String> images) {
//        this.images = images;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public ProductBrand getProductBrand() {
//        return productBrand;
//    }
//
//    public void setProductBrand(ProductBrand productBrand) {
//        this.productBrand = productBrand;
//    }
//
//    public int getRate() {
//        return rate;
//    }
//
//    public void setRate(int rate) {
//        this.rate = rate;
//    }
//
//    public int getStoreId() {
//        return storeId;
//    }
//
//    public void setStoreId(int storeId) {
//        this.storeId = storeId;
//    }
//
//    public String getProductName() {
//        if (UtilityApp.getLanguage().equals(Constants.English)) {
//            return name;
//
//        } else {
//            return hName;
//        }
//    }
//
//
//    @Override
//    public int compareTo(ProductModel productModel) {
////        return Integer.compare((int) getFirstProductBarcodes().getPrice(),
////                (int) productModel.getFirstProductBarcodes().getPrice());
//
//
//        if (getFirstProductBarcodes().getPrice() > productModel.getFirstProductBarcodes().getPrice()) {
//            return 1;
//        }
//        else if (productModel.getFirstProductBarcodes().getPrice() <  getFirstProductBarcodes().getPrice()) {
//            return -1;
//        }
//        else {
//            return 0;
//        }
//
//    }
}
