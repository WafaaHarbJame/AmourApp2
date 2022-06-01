package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatModel {
    private String senderID, receiverID, messageKey;
    private String messageBody;
    private String   senderName;
    private String receiverName;
    private String senderImage;
    private String receiverImage;
    private String messageType;
    private String inputType;
    private String imageUrl;
    private String lat;
    private String lng;
    private long messageTime;
    private boolean is_read;

    public ChatModel(String senderID, String receiverID, String messageKey, String messageBody, String senderName, String receiverName, String senderImage, String receiverImage, String messageType, String inputType, String imageUrl, String lat, String lng, long messageTime, boolean is_read) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageKey = messageKey;
        this.messageBody = messageBody;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderImage = senderImage;
        this.receiverImage = receiverImage;
        this.messageType = messageType;
        this.inputType = inputType;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.messageTime = messageTime;
        this.is_read = is_read;
    }

    public ChatModel() {
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

}
