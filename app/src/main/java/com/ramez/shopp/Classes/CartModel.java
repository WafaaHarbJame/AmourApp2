package com.ramez.shopp.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartModel implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("product_barcode_id")
    @Expose
    private Integer productBarcodeId;
    @SerializedName("product_quantity")
    @Expose
    private Integer productQuantity;
    @SerializedName("limit_qty")
    @Expose
    private Integer limitQty;
    @SerializedName("scheduled")
    @Expose
    private Integer scheduled;
    @SerializedName("remark")
    @Expose
    private String remark="";
    @SerializedName("product_price")
    @Expose
    private Double productPrice;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("special_price")
    @Expose
    private Double specialPrice;
    @SerializedName("isSpecialPriceVisible")
    @Expose
    private Boolean isSpecialPriceVisible;
    @SerializedName("isPriceVisible")
    @Expose
    private Boolean isPriceVisible;
    @SerializedName("gst")
    @Expose
    private Integer gst;
    @SerializedName("product_gst")
    @Expose
    private Integer productGst;
    @SerializedName("gst_visible")
    @Expose
    private Boolean gstVisible;
    @SerializedName("product_igst")
    @Expose
    private Integer productIgst;
    @SerializedName("total_without_tax")
    @Expose
    private Double totalWithoutTax;
    @SerializedName("total_with_tax")
    @Expose
    private Double totalWithTax;
    @SerializedName("h_product_name")
    @Expose
    private String hProductName;
    @SerializedName("weight")
    @Expose
    private Integer weight;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("h_unit_name")
    @Expose
    private String hUnitName;
    @SerializedName("unit_code")
    @Expose
    private String unitCode;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("weight_unit")
    @Expose
    private String weightUnit;
    @SerializedName("minimumDate")
    @Expose
    private Object minimumDate;
    @SerializedName("isExtra")
    @Expose
    private boolean isExtra;
    private Double savePrice;

    public Double getSavePrice() {
        return savePrice;
    }

    public void setSavePrice(Double savePrice) {
        this.savePrice = savePrice;
    }

    public Boolean getSpecialPriceVisible() {
        return isSpecialPriceVisible;
    }

    public void setSpecialPriceVisible(Boolean specialPriceVisible) {
        isSpecialPriceVisible = specialPriceVisible;
    }

    public Boolean getPriceVisible() {
        return isPriceVisible;
    }

    public void setPriceVisible(Boolean priceVisible) {
        isPriceVisible = priceVisible;
    }

    public String gethProductName() {
        return hProductName;
    }

    public void sethProductName(String hProductName) {
        this.hProductName = hProductName;
    }

    public String gethUnitName() {
        return hUnitName;
    }

    public void sethUnitName(String hUnitName) {
        this.hUnitName = hUnitName;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean extra) {
        isExtra = extra;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getProductBarcodeId() {
        return productBarcodeId;
    }

    public void setProductBarcodeId(Integer productBarcodeId) {
        this.productBarcodeId = productBarcodeId;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Integer getLimitQty() {
        return limitQty;
    }

    public void setLimitQty(Integer limitQty) {
        this.limitQty = limitQty;
    }

    public Integer getScheduled() {
        return scheduled;
    }

    public void setScheduled(Integer scheduled) {
        this.scheduled = scheduled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(Double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public Boolean getIsSpecialPriceVisible() {
        return isSpecialPriceVisible;
    }

    public void setIsSpecialPriceVisible(Boolean isSpecialPriceVisible) {
        this.isSpecialPriceVisible = isSpecialPriceVisible;
    }

    public Boolean getIsPriceVisible() {
        return isPriceVisible;
    }

    public void setIsPriceVisible(Boolean isPriceVisible) {
        this.isPriceVisible = isPriceVisible;
    }

    public Integer getGst() {
        return gst;
    }

    public void setGst(Integer gst) {
        this.gst = gst;
    }

    public Integer getProductGst() {
        return productGst;
    }

    public void setProductGst(Integer productGst) {
        this.productGst = productGst;
    }

    public Boolean getGstVisible() {
        return gstVisible;
    }

    public void setGstVisible(Boolean gstVisible) {
        this.gstVisible = gstVisible;
    }

    public Integer getProductIgst() {
        return productIgst;
    }

    public void setProductIgst(Integer productIgst) {
        this.productIgst = productIgst;
    }

    public Double getTotalWithoutTax() {
        return totalWithoutTax;
    }

    public void setTotalWithoutTax(Double totalWithoutTax) {
        this.totalWithoutTax = totalWithoutTax;
    }

    public Double getTotalWithTax() {
        return totalWithTax;
    }

    public void setTotalWithTax(Double totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    public String getHProductName() {
        return hProductName;
    }

    public void setHProductName(String hProductName) {
        this.hProductName = hProductName;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
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

    public String getHUnitName() {
        return hUnitName;
    }

    public void setHUnitName(String hUnitName) {
        this.hUnitName = hUnitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Object getMinimumDate() {
        return minimumDate;
    }

    public void setMinimumDate(Object minimumDate) {
        this.minimumDate = minimumDate;
    }


    public String getName(){
        if(UtilityApp.getLanguage().equals(Constants.English)){
            return productName;

        }
        else {
            return hProductName;
        }
    }
}
