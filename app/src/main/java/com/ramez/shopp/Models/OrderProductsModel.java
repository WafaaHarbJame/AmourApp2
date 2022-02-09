package com.ramez.shopp.Models;

import com.ramez.shopp.classes.UtilityApp;

public class OrderProductsModel {

    private int ip_id;
    private String pro_qua;
    private String pro_name;
    private String pro_total;
    private String pro_image;
    private  String pro_price;
    private int shop_id;
    String pro_price_before_discount;
    String pro_special_price;
    private int pro_id;
    private int inv_id;
    private int model_id;
    private String  pro_name_en;

    public OrderProductsModel(int ip_id,int pro_id, String pro_qua, String pro_name,String pro_name_en,String pro_price,
                           String pro_total,String pro_image,int model_id,int shop_id) {
        this.ip_id = ip_id;
        this.pro_qua = pro_qua;
        this.pro_name = pro_name;
        this.pro_total = pro_total;
        this.pro_image=pro_image;
        this.pro_price_before_discount=pro_price_before_discount;
        this.shop_id=shop_id;
        this.pro_id=pro_id;
        this.model_id=model_id;
        this.pro_price=pro_price;
        this.pro_name_en=pro_name_en;
    }

    public String getPro_name_en() {
        return pro_name_en;
    }

    public void setPro_name_en(String pro_name_en) {
        this.pro_name_en = pro_name_en;
    }

    public int getModel_id() {
        return model_id;
    }

    public void setModel_id(int model_id) {
        this.model_id = model_id;
    }

    public void setInv_id(int inv_id) {
        this.inv_id = inv_id;
    }

    public int getPro_id() {
        return pro_id;
    }

    public int getInv_id() {
        return inv_id;
    }

    public String getPro_price() {
        return pro_price;
    }

    public void setPro_price(String pro_price) {
        this.pro_price = pro_price;
    }

    public void setPro_id(int pro_id) {
        this.pro_id = pro_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getPro_price_before_discount() {
        return pro_price_before_discount;
    }

    public void setPro_price_before_discount(String pro_price_before_discount) {
        this.pro_price_before_discount = pro_price_before_discount;
    }

    public String getPro_special_price() {
        return pro_special_price;
    }

    public void setPro_special_price(String pro_special_price) {
        this.pro_special_price = pro_special_price;
    }

    public int getIp_id() {
        return ip_id;
    }

    public void setIp_id(int ip_id) {
        this.ip_id = ip_id;
    }

    public String getPro_qua() {
        return pro_qua;
    }

    public void setPro_qua(String pro_qua) {
        this.pro_qua = pro_qua;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public String getPro_total() {
        return pro_total;
    }

    public void setPro_total(String pro_total) {
        this.pro_total = pro_total;
    }

    public String getPro_image() {
        return pro_image;
    }

    public void setPro_image(String pro_image) {
        this.pro_image = pro_image;
    }
    public String getName() {

        return UtilityApp.isEnglish()? pro_name_en : pro_name;
    }
}

