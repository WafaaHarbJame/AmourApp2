package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferedProduct {
    @SerializedName("aws_image")
    @Expose
    private String awsImage;
    @SerializedName("brand_id")
    @Expose
    private Integer brandId;
    @SerializedName("cart_quantity")
    @Expose
    private Integer cartQuantity;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("cgst")
    @Expose
    private Integer cgst;
    @SerializedName("checkPickerLoad")
    @Expose
    private Integer checkPickerLoad;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("favourite")
    @Expose
    private String favourite;
    @SerializedName("get_product_variations")
    @Expose
    private List<GetProductVariation> getProductVariations = null;
    @SerializedName("h_description")
    @Expose
    private String hDescription;
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("igst")
    @Expose
    private Integer igst;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("is_featured")
    @Expose
    private Integer isFeatured;
    @SerializedName("is_offered")
    @Expose
    private Integer isOffered;
    @SerializedName("is_quick_grab")
    @Expose
    private Integer isQuickGrab;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("parent_category_id")
    @Expose
    private Integer parentCategoryId;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("product_brand")
    @Expose
    private ProductBrand productBrand;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("secondLoad")
    @Expose
    private Integer secondLoad;
    @SerializedName("selected_index")
    @Expose
    private Integer selectedIndex;
    @SerializedName("sgst")
    @Expose
    private Integer sgst;
    @SerializedName("special_price")
    @Expose
    private Integer specialPrice;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("weight")
    @Expose
    private Integer weight;

    public String getAwsImage() {
        return awsImage;
    }

    public void setAwsImage(String awsImage) {
        this.awsImage = awsImage;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(Integer cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCgst() {
        return cgst;
    }

    public void setCgst(Integer cgst) {
        this.cgst = cgst;
    }

    public Integer getCheckPickerLoad() {
        return checkPickerLoad;
    }

    public void setCheckPickerLoad(Integer checkPickerLoad) {
        this.checkPickerLoad = checkPickerLoad;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }

    public List<GetProductVariation> getGetProductVariations() {
        return getProductVariations;
    }

    public void setGetProductVariations(List<GetProductVariation> getProductVariations) {
        this.getProductVariations = getProductVariations;
    }

    public String getHDescription() {
        return hDescription;
    }

    public void setHDescription(String hDescription) {
        this.hDescription = hDescription;
    }

    public String getHName() {
        return hName;
    }

    public void setHName(String hName) {
        this.hName = hName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIgst() {
        return igst;
    }

    public void setIgst(Integer igst) {
        this.igst = igst;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Integer isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Integer getIsOffered() {
        return isOffered;
    }

    public void setIsOffered(Integer isOffered) {
        this.isOffered = isOffered;
    }

    public Integer getIsQuickGrab() {
        return isQuickGrab;
    }

    public void setIsQuickGrab(Integer isQuickGrab) {
        this.isQuickGrab = isQuickGrab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ProductBrand getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(ProductBrand productBrand) {
        this.productBrand = productBrand;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSecondLoad() {
        return secondLoad;
    }

    public void setSecondLoad(Integer secondLoad) {
        this.secondLoad = secondLoad;
    }

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public Integer getSgst() {
        return sgst;
    }

    public void setSgst(Integer sgst) {
        this.sgst = sgst;
    }

    public Integer getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(Integer specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
