package com.emp.auction;

import java.util.ArrayList;

public class Common {
    private String user;
    private String userRole;
    private String baseURL="http://The-work-kw.com/auction/admin/backend/";//"http://10.0.2.2/jsontest/admin/backend/";//
    private ArrayList<Categories> categories;

    private static Common instance = new Common();

    public void Comon(){
        //this.baseURL="http://localhost/jsontest/";
    }

    public static Common getInstance()
    {
        return instance;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories = categories;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getCategoryString() {
        ArrayList<String> returnVal=new ArrayList<String>();
        for (Categories theCategory:categories) {
            returnVal.add(theCategory.getmName());
        }
        return returnVal;
    }

}
