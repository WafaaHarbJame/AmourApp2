package com.ramez.shopp.Classes;
public class MessageEvent {

    public static final String TYPE_invoice = "invoice";
    public static final String TYPE_READ_CART= "TYPE_READ_CART";
    public static final String TYPE_CATEGORY_PRODUCT = "TYPE_CATEGORY_PRODUCT";
    public static final String Type_offer = "Type_offer";
    public static final String TYPE_BROUSHERS = "TYPE_BROUSHERS";
    public static final String TYPE_view = "TYPE_view";
    public static final String TYPE_SORT = "TYPE_SORT";
    public static final String TYPE_cart = "cart";
    public static final String REFRESH_CART = "REFRESH_CART";
    public static final String TYPE_search= "TYPE_search";
    public static final String TYPE_CATEGORY = "TYPE_CATEGORY";
    public static final String TYPE_main = "main";
    public static final String TYPE_UPDATE_CART = "updateCart";
    public static final String TYPE_REFRESH = "refresh";
    public static final String TYPE_POSITION = "position";
    public static final String TYPE_Deep_links = "TYPE_Deep_links";


    //    public int PagerPosition;
    public Object data;
    public String type;

    public MessageEvent(String type, Object msgData) {
        this.data = msgData;
        this.type = type;
    }



    public MessageEvent(String type) {
        this.type = type;
    }

    public MessageEvent() {
    }

}

