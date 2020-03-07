package com.emp.auction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {


    private int SPLASH_TIME = 1000;
    private boolean firstFlag;
    private boolean secondFlag;
    private ArrayList<Categories> categoriesList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_splash_screen);
//get categories===============
        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");

        firstFlag=false;
        secondFlag=false;
try {
        Ion.with(this)
            .load(Common.getInstance().getBaseURL()+"getcategories.php")
            .setJsonObjectBody(json)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {

                    if (result != null) {
                        String status = result.get("status").getAsString();
                        if (status.equals("ok")) {
                            JsonArray categories = result.get("categories").getAsJsonArray();
                            for (JsonElement categoryElement : categories) {
                                JsonObject theCategory = categoryElement.getAsJsonObject();

                                int id = theCategory.get("id").getAsInt();
                                int count=theCategory.get("count").getAsInt();
                                String name = theCategory.get("name").getAsString();
                                String image = theCategory.get("image").getAsString();

                                categoriesList.add(new Categories(id, name, image,count));
                            }
                            Common.getInstance().setCategories(categoriesList);
                            firstFlag = true;
                        }
                    }
                    if (secondFlag == true) {
                        moveToLogin();
                    }
                }
            });
}
catch(Exception e){
    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
 //------------------------------------
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondFlag=true;

                if(firstFlag==true){
                    moveToLogin();
                }
            }
        },3000);
//        Thread timer = new Thread() {
//            public void run() {
//                try {
//                    sleep(SPLASH_TIME);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
////                    if (SharedPref.getPrefForLoginStatus(SplashActivity.this)) {
////
////                        ProjectUtils.genericIntent(SplashActivity.this, HomeActivity.class, null, true);
////                    } else {
////                        ProjectUtils.genericIntent(SplashActivity.this, LoginActivity.class, null, true);
////                    }
//                    Log.i("Splash","Screen");
//
//                }
//            }
//        };
//        timer.start();
    }
    private void moveToLogin(){
        Intent intent=new Intent(SplashActivity.this, LoginActivity.class);//LoginActivity.class);
        startActivity(intent);
    }
}
