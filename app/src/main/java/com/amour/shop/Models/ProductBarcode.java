package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductBarcode implements Serializable {


    @SerializedName("cart_quantity")
    @Expose
    private int cartQuantity=0;
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
    private int id=0;
    @SerializedName("limit_qty")
    @Expose
    private int limitQty=0;
    @SerializedName("price")
    @Expose
    private double price=0.0;
    @SerializedName("special_price")
    @Expose
    private double specialPrice=0.0;
    @SerializedName("isSpecial")
    @Expose
    private boolean isSpecial;
    @SerializedName("stock_qty")
    @Expose
    private int stockQty=0;
    @SerializedName("unit_id")
    @Expose
    private int unitId=0;
    @SerializedName("cart_id")
    @Expose
    private int cartId=0;
    @SerializedName("product_units")
    @Expose
    private ProductUnits productUnits;
    @SerializedName("weight")
    @Expose
    private double weight=0.0;
    @SerializedName("description")
    @Expose
    private String description;

    public int getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimitQty() {
        return limitQty;
    }

    public void setLimitQty(int limitQty) {
        this.limitQty = limitQty;
    }

    public double getPrice() {
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

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public ProductUnits getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(ProductUnits productUnits) {
        this.productUnits = productUnits;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}