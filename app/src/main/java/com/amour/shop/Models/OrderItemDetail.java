package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.amour.shop.classes.Constants;
import com.amour.shop.classes.UtilityApp;

public class OrderItemDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("h_product_name")
    @Expose
    private String hProductName;
    @SerializedName("h_unit_name")
    @Expose
    private String hUnitName;
    @SerializedName("product_barcode_id")
    @Expose
    private Integer productBarcodeId;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("cart_price")
    @Expose
    private Double cartPrice;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("limit_qty")
    @Expose
    private Integer limitQty;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("remark")
    @Expose
    private Object remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getHProductName() {
        return hProductName;
    }

    public void setHProductName(String hProductName) {
        this.hProductName = hProductName;
    }

    public String getHUnitName() {
        return hUnitName;
    }

    public void setHUnitName(String hUnitName) {
        this.hUnitName = hUnitName;
    }

    public Integer getProductBarcodeId() {
        return productBarcodeId;
    }

    public void setProductBarcodeId(Integer productBarcodeId) {
        this.productBarcodeId = productBarcodeId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(Double cartPrice) {
        this.cartPrice = cartPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getLimitQty() {
        return limitQty;
    }

    public void setLimitQty(Integer limitQty) {
        this.limitQty = limitQty;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Object getRemark() {
        return remark;
    }

    public void setRemark(Object remark) {
        this.remark = remark;
    }

    public String getName(){
        if(UtilityApp.INSTANCE.getLanguage().equals(Constants.English)){
            return productName;

        }
        else {
            return hProductName;
        }
    }

}
