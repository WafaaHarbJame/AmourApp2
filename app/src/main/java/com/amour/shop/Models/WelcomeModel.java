package com.amour.shop.Models;

public class WelcomeModel {
    private String infoTxtTitle;
    private int image;
    private String infoTxt2;


    public WelcomeModel(String infoTxtTitle, int image, String infoTxt2) {
        this.infoTxtTitle = infoTxtTitle;
        this.image = image;
        this.infoTxt2 = infoTxt2;
    }

    public String getInfoTxtTitle() {
        return infoTxtTitle;
    }

    public void setInfoTxtTitle(String infoTxtTitle) {
        this.infoTxtTitle = infoTxtTitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getInfoTxt2() {
        return infoTxt2;
    }

    public void setInfoTxt2(String infoTxt2) {
        this.infoTxt2 = infoTxt2;
    }
}
