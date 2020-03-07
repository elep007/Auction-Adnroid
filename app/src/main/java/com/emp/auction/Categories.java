package com.emp.auction;

import java.util.ArrayList;

public class Categories {
        // Store the id of the  movie poster
        private int mID;
        private String mName;
        private String mImage;
        private int mCategoryCount;

        // Constructor that is used to create an instance of the Products object
        public Categories(int ID, String name, String image,int categoryCount) {
            this.mID=ID;
            this.mName  = name;
            this.mImage = Common.getInstance().getBaseURL()+image;
            this.mCategoryCount=categoryCount;
        }

        public int getmCategoryCount() {
                return mCategoryCount;
        }

        public void setmCategoryCount(int mCategoryCount) {
                this.mCategoryCount = mCategoryCount;
        }

        public int getmID() {return mID;}
        public void setmId(int mID) {this.mID = mID;}

        public String getmName() { return mName;}
        public void setmName(String mName) {this.mName = mName; }

        public String getmImage() { return mImage; }
        public void setmImage(String mImage) { this.mImage = Common.getInstance().getBaseURL()+mImage; }

}
