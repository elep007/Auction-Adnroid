package com.emp.auction;

public class Products {

    // Store the id of the  movie poster
    private int mID;
    private int mcatid;
    private String mName;
    private String mImage;
    private int mPrice;
    private int mCountdown;
    private String mLastBid;
    private long mEndTime;
    private String mDescription;
    private String mVideo;
    private String mSeller;
    private String mPayStatus;

    // Constructor that is used to create an instance of the Products object
    public Products(int ID, int catid, String name, String image,int price,int countdown, String lastBid,String endTime,String description,String video,String seller,String payStatus) {
        this.mID=ID;
        this.mcatid=catid;
        this.mName  = name;
        this.mImage = image;
        this.mPrice=price;
        this.mCountdown=countdown;
        this.mLastBid=lastBid;

        long currentTime=System.currentTimeMillis()/1000;
        long diff=stringToSeconds(endTime);
        this.mEndTime=currentTime+diff;
        this.mDescription=description;
        this.mVideo=video;

        this.mSeller=seller;
        this.mPayStatus=payStatus;
    }
    public String getIsVideo(){return mVideo;}

    public int getmID() {return mID;}
    public void setmId(int mID) {this.mID = mID;}

    public int mcatid() {return mcatid;}
    public void setmcatid(int catid) {this.mcatid = mID;}

    public String getmName() { return mName;}
    public void setmName(String mName) {this.mName = mName; }

    public String getmLastBid() { return mLastBid;}
    public void setmLastBid(String mLastBid) {this.mLastBid = mLastBid; }

    public String getmImage() { return mImage; }
    public void setmImage(String mImage) { this.mImage = mImage; }

    public String getmVideo() { return mVideo; }
    public void setmVideo(String video) { this.mVideo = video; }

    public int getmPrice() {      return mPrice;    }
    public void setmPrice(int mPrice) {        this.mPrice = mPrice ;    }

    public int getmCountdown() {      return mCountdown;    }
    public void setmCountdown(int mCountdown) {        this.mCountdown = mCountdown ;    }

    public long getEndTime() {
        return mEndTime;
    }
    public void setmEndTime(long mEndTime) {        this.mEndTime = mEndTime ;    }

    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {        this.mDescription = description ;    }

    private long stringToSeconds(String endtime){
        if(endtime.equals("Ended")){
            return -1;
        }
        String[] temp = endtime.split(":");
        return Integer.valueOf(temp[0]) * 3600 + Integer.valueOf(temp[1]) * 60 + Integer.valueOf(temp[2]);
    }

    public String ismPayStatus() {
        return mPayStatus;
    }

    public String getmSeller() {
        return mSeller;
    }

    public void setmSeller(String mSeller) {
        this.mSeller = mSeller;
    }
}