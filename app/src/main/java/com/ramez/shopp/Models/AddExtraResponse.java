package com.ramez.shopp.Models;

public class AddExtraResponse {
    /**
     * cart_count : 0
     * id : 76119
     * status : 200
     * message : Success
     */

    private int cart_count;
    private int id;
    private int status;
    private String message;

    public int getCart_count() {
        return cart_count;
    }

    public void setCart_count(int cart_count) {
        this.cart_count = cart_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //    @SerializedName("id")
//    @Expose
//    private int id;
//    @SerializedName("qty")
//    @Expose
//    private int qty;
//    @SerializedName("description")
//    @Expose
//    private String description;
//    @SerializedName("image")
//    @Expose
//    private String image;
//    @SerializedName("productid")
//    @Expose
//    private int productid;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
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
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public int getProductid() {
//        return productid;
//    }
//
//    public void setProductid(int productid) {
//        this.productid = productid;
//    }
//



}
