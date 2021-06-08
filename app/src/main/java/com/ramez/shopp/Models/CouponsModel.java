package com.ramez.shopp.Models;

import java.io.Serializable;

public class CouponsModel implements Serializable {
    public int id;
    public int user_id;
    public String coupon_code;
    public int points;
    public float value;
    private String created_at;
    public String updated_at;
    public String currency;

    public CouponsModel(int id, int user_id, String coupon_code, int points, float value, String created_at, String updated_at, String currency) {
        this.id = id;
        this.user_id = user_id;
        this.coupon_code = coupon_code;
        this.points = points;
        this.value = value;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.currency = currency;
    }

    public CouponsModel() {
    }
}
