package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailsMode {
    @SerializedName("total_amount")
    @Expose
    private Double totalAmount;
    @SerializedName("client_name")
    @Expose
    private String clientName;
    @SerializedName("client_mobile_number")
    @Expose
    private String clientMobileNumber;
    @SerializedName("adrs_mobile_number")
    @Expose
    private Object adrsMobileNumber;
    @SerializedName("adrs_name")
    @Expose
    private Object adrsName;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("order_code")
    @Expose
    private String orderCode;
    @SerializedName("full_address")
    @Expose
    private String fullAddress;
    @SerializedName("delivery_date")
    @Expose
    private String deliveryDate;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("drivered_date")
    @Expose
    private Object driveredDate;
    @SerializedName("orderItemDetails")
    @Expose
    private List<OrderItemDetail> orderItemDetails = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientMobileNumber() {
        return clientMobileNumber;
    }

    public void setClientMobileNumber(String clientMobileNumber) {
        this.clientMobileNumber = clientMobileNumber;
    }

    public Object getAdrsMobileNumber() {
        return adrsMobileNumber;
    }

    public void setAdrsMobileNumber(Object adrsMobileNumber) {
        this.adrsMobileNumber = adrsMobileNumber;
    }

    public Object getAdrsName() {
        return adrsName;
    }

    public void setAdrsName(Object adrsName) {
        this.adrsName = adrsName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Object getDriveredDate() {
        return driveredDate;
    }

    public void setDriveredDate(Object driveredDate) {
        this.driveredDate = driveredDate;
    }

    public List<OrderItemDetail> getOrderItemDetails() {
        return orderItemDetails;
    }

    public void setOrderItemDetails(List<OrderItemDetail> orderItemDetails) {
        this.orderItemDetails = orderItemDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

}

