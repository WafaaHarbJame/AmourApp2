package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.UtilityApp;

import java.io.Serializable;
import java.util.ArrayList;


public class OrderProductModel implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("weight")
    @Expose
    private Double weight;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("h_unit_name")
    @Expose
    private String hUnitName;
    @SerializedName("order_code")
    @Expose
    private String orderCode;
    @SerializedName("delivery_date")
    @Expose
    private String deliveryDate;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("delivery_time")
    @Expose
    private String deliveryTime;
    @SerializedName("order_total")
    @Expose
    private Double orderTotal;
    @SerializedName("cart_id")
    @Expose
    private Integer cartId;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("total_with_tax")
    @Expose
    private Double totalWithTax;
    @SerializedName("total_without_tax")
    @Expose
    private Double totalWithoutTax;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("h_product_name")
    @Expose
    private String hProductName;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("product_variation_id")
    @Expose
    private Integer productVariationId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("full_address")
    @Expose
    private String fullAddress;
    @SerializedName("address_name")
    @Expose
    private String addressName;
    @SerializedName("mobile_number")
    @Expose
    private Object mobileNumber;
    private ArrayList<OrderProductModel> orderProductsDMS;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String gethUnitName() {
        return hUnitName;
    }

    public void sethUnitName(String hUnitName) {
        this.hUnitName = hUnitName;
    }

    public String gethProductName() {
        return hProductName;
    }

    public void sethProductName(String hProductName) {
        this.hProductName = hProductName;
    }

    public ArrayList<OrderProductModel> getOrderProductsDMS() {
        return orderProductsDMS;
    }

    public void setOrderProductsDMS(ArrayList<OrderProductModel> orderProductsDMS) {
        this.orderProductsDMS = orderProductsDMS;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getHUnitName() {
        return hUnitName;
    }

    public void setHUnitName(String hUnitName) {
        this.hUnitName = hUnitName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public Double getTotalWithTax() {
        return totalWithTax;
    }

    public void setTotalWithTax(Double totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    public Double getTotalWithoutTax() {
        return totalWithoutTax;
    }

    public void setTotalWithoutTax(Double totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductVariationId() {
        return productVariationId;
    }

    public void setProductVariationId(Integer productVariationId) {
        this.productVariationId = productVariationId;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Object getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Object mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getName(){
        if(UtilityApp.getLanguage().equals(Constants.English)){
            return productName;

        }
        else {
            return hProductName;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}