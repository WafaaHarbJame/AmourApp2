package com.amour.shop.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetProductVariation  implements Serializable {
    @SerializedName("cart_quantity")
    @Expose
    private Integer cartQuantity;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("limit_qty")
    @Expose
    private Integer limitQty;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("product_barcode_id")
    @Expose
    private String productBarcodeId;
    @SerializedName("product_barcodes")
    @Expose
    private ProductBarcode productBarcodes;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_units")
    @Expose
    private ProductUnits_ productUnits;
    @SerializedName("selected_index")
    @Expose
    private Integer selectedIndex;
    @SerializedName("special_price")
    @Expose
    private String specialPrice;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("stock_qty")
    @Expose
    private Integer stockQty;
    @SerializedName("unit_id")
    @Expose
    private Integer unitId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("weight")
    @Expose
    private Integer weight;

    public Integer getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(Integer cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLimitQty() {
        return limitQty;
    }

    public void setLimitQty(Integer limitQty) {
        this.limitQty = limitQty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String  price) {
        this.price = price;
    }

    public String getProductBarcodeId() {
        return productBarcodeId;
    }

    public void setProductBarcodeId(String productBarcodeId) {
        this.productBarcodeId = productBarcodeId;
    }

    public ProductBarcode getProductBarcodes() {
        return productBarcodes;
    }

    public void setProductBarcodes(ProductBarcode productBarcodes) {
        this.productBarcodes = productBarcodes;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public ProductUnits_ getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(ProductUnits_ productUnits) {
        this.productUnits = productUnits;
    }

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public String getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(String specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
