package com.emp.auction;

public class Winner {
    private String mUsername;
    private String mName;
    private String mMobile;
    private String mProductName;
    private int mPrice;
    private int mId;

    public Winner(String userName,String name, String mobile, String productName,int price,int id){
        this.mUsername=userName;
        this.mName=name;
        this.mMobile=mobile;
        this.mProductName=productName;
        this.mPrice=price;
        this.mId=id;
    }

    public int getmPrice() {
        return mPrice;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmMobile() {
        return mMobile;
    }

    public String getmProductName() {
        return mProductName;
    }

    public String getmUsername() {
        return mUsername;
    }
}
