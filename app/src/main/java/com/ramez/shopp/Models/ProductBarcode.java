package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductBarcode implements Serializable {

    @SerializedName("cart_quantity")
    @Expose
    private Integer cartQuantity;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("end_offer")
    @Expose
    private String endOffer;
    @SerializedName("from_offer")
    @Expose
    private String fromOffer;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("limit_qty")
    @Expose
    private Integer limitQty;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("special_price")
    @Expose
    private double specialPrice;
    @SerializedName("isSpecial")
    @Expose
    private Boolean isSpecial;
    @SerializedName("stock_qty")
    @Expose
    private Integer stockQty;
    @SerializedName("unit_id")
    @Expose
    private Integer unitId;
    @SerializedName("cart_id")
    @Expose
    private int cartId;
    @SerializedName("product_units")
    @Expose
    private ProductUnits productUnits;
    @SerializedName("weight")
    @Expose
    private Double weight;

    public Boolean getSpecial() {
        return isSpecial;
    }

    public void setSpecial(Boolean special) {
        isSpecial = special;
    }

    public ProductUnits getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(ProductUnits productUnits) {
        this.productUnits = productUnits;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(Integer cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getEndOffer() {
        return endOffer;
    }

    public void setEndOffer(String endOffer) {
        this.endOffer = endOffer;
    }

    public String getFromOffer() {
        return fromOffer;
    }

    public void setFromOffer(String fromOffer) {
        this.fromOffer = fromOffer;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
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

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

}