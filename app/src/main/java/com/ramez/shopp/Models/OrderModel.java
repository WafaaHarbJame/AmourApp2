package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OrderModel implements Serializable {
    @SerializedName("order_id")
    @Expose
    private Integer orderId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

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
    @SerializedName("full_address")
    @Expose
    private String fullAddress;
    @SerializedName("address_name")
    @Expose
    private String addressName;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    private List<OrderProductModel> orderProductsDMS;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @SerializedName("store_name")
    @Expose
    private String storeName;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<OrderProductModel> getOrderProductsDMS() {
        return orderProductsDMS;
    }

    public void setOrderProductsDMS(List<OrderProductModel> orderProductsDMS) {
        this.orderProductsDMS = orderProductsDMS;
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

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }



}