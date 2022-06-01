package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("phone_prefix")
    @Expose
    private String phonePrefix ="";
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber="";
    @SerializedName("address_type")
    @Expose
    private String addressType="";
    @SerializedName("house_no")
    @Expose
    private String houseNo="";
    @SerializedName("apartment_no")
    @Expose
    private String apartmentNo="";
    @SerializedName("area_id")
    @Expose
    private int areaId=0;
    @SerializedName("additional_direction")
    @Expose
    private String additionalDirection="";
    @SerializedName("landline_number")
    @Expose
    private String landlineNumber="0";
    @SerializedName("floor")
    @Expose
    private String floor="";
    @SerializedName("block")
    @Expose
    private String block="";
    @SerializedName("address_nickname")
    @Expose
    private String addressNickname="";
    @SerializedName("landmark_details")
    @Expose
    private String landmarkDetails="";
    @SerializedName("area_details")
    @Expose
    private String areaDetails="";
    @SerializedName("city")
    @Expose
    private String city="";
    @SerializedName("country")
    @Expose
    private String country="";
    @SerializedName("street_details")
    @Expose
    private String streetDetails="";
    @SerializedName("longitude")
    @Expose
    private Double longitude=0.0;
    @SerializedName("latitude")
    @Expose
    private Double latitude=0.0;
    @SerializedName("google_address")
    @Expose
    private String googleAddress="";
    @SerializedName("full_address")
    @Expose
    private String fullAddress="";
    @SerializedName("default_address")
    @Expose
    private Integer defaultAddress=0;
    @SerializedName("state")
    @Expose
    private int state=0;
    @SerializedName("pincode")
    @Expose
    private String pincode="";
    @SerializedName("apartment_name")
    @Expose
    private String apartmentName="";
    @SerializedName("isDefault")
    @Expose
    private Boolean isDefault=false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getApartmentNo() {
        return apartmentNo;
    }

    public void setApartmentNo(String apartmentNo) {
        this.apartmentNo = apartmentNo;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAdditionalDirection() {
        return additionalDirection;
    }

    public void setAdditionalDirection(String additionalDirection) {
        this.additionalDirection = additionalDirection;
    }

    public String  getLandlineNumber() {
        return landlineNumber;
    }

    public void setLandlineNumber(String landlineNumber) {
        this.landlineNumber = landlineNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getAddressNickname() {
        return addressNickname;
    }

    public void setAddressNickname(String addressNickname) {
        this.addressNickname = addressNickname;
    }

    public String getLandmarkDetails() {
        return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
        this.landmarkDetails = landmarkDetails;
    }

    public String getAreaDetails() {
        return areaDetails;
    }

    public void setAreaDetails(String areaDetails) {
        this.areaDetails = areaDetails;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetDetails() {
        return streetDetails;
    }

    public void setStreetDetails(String streetDetails) {
        this.streetDetails = streetDetails;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Integer getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Integer defaultAddress) {
        this.defaultAddress = defaultAddress;
    }




    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}

