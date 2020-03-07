package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;

public class WinnerActivity extends AppCompatActivity {
    private ArrayList<Products> productsList;
    private ListView listView;
    private Context context=this;
    private ProductsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);
        if(Common.getInstance().getUser().equals("guest12345")) onBackPressed();
//toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Won Auctions");
        //toolbar.setSubtitle("Subtitle");

        setSupportActionBar(toolbar);

//        toolbar.setNavigationIcon(R.drawable.backbutton);
//        toolbar.setNavigationOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onBackPressed();
//                    }
//                }
//        );
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_winner);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------

        listView = findViewById(R.id.list_item_products);

        updateProducts();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //   String itemValue = (String) listView.getItemAtPosition(position);
                // selected item
                String name =((TextView)view.findViewById(R.id.txtName)).getText().toString();


                Products theProduct=productsList.get(position);
                Intent intent = new Intent(view.getContext(), ProductDetailActivity.class).putExtra("name",theProduct.getmName())
                        .putExtra("price",theProduct.getmPrice()).putExtra("countdown",theProduct.getmCountdown())
                        .putExtra("lastbid",theProduct.getmLastBid()).putExtra("endtime",theProduct.getEndTime())
                        .putExtra("image",theProduct.getmImage()).putExtra("description",theProduct.getDescription())
                        .putExtra("id",theProduct.getmID()).putExtra("video",theProduct.getmVideo())
                        .putExtra("seller",theProduct.getmSeller()).putExtra("paystatus",theProduct.ismPayStatus());
                startActivityForResult(intent, 101);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == 102) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                updateProducts();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateProducts(){
        if(Common.getInstance().getUser().equals("guest12345")) return;
        productsList = new ArrayList<>();
        //ion request======================
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();

        json.addProperty("username", Common.getInstance().getUser());

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"getwinner.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    JsonArray products = result.get("products").getAsJsonArray();
                                    for (JsonElement productElement : products) {
                                        JsonObject theProdut = productElement.getAsJsonObject();

                                        int id = theProdut.get("id").getAsInt();
                                        int catid = theProdut.get("catid").getAsInt();
                                        String name = theProdut.get("name").getAsString();
                                        String image = theProdut.get("image").getAsString();
                                        int price = theProdut.get("price").getAsInt();
                                        int countdown = theProdut.get("countdown").getAsInt();
                                        String endtime = theProdut.get("endtime").getAsString();
                                        String lastbid = theProdut.get("lastbid").getAsString();
                                        String description = theProdut.get("description").getAsString();
                                        String video=theProdut.get("video").getAsString();
                                        String seller=theProdut.get("seller").getAsString();
                                        String payStatus=theProdut.get("paystatus").getAsString();
                                        productsList.add(new Products(id, catid, name, image, price, countdown, lastbid, endtime, description,video,seller,payStatus));
                                    }
                                    Collections.reverse(productsList);
                                    mAdapter = new ProductsAdapter(context, productsList);
                                    listView.setAdapter(mAdapter);
                                    //startCountdown();
                                }
                            } else {

                            }
                        }
                    });
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    //toolbar============
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                moveToProfile();
                break;
            case R.id.menu_logout:
                moveToLogin();
                break;
            default:
                break;
        }
        return true;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    moveToHome();
                    return true;
                case R.id.navigation_purchase:
                    moveToCategory();
                    return true;
                case R.id.navigation_contact:
                    moveToContact();
                    return true;
                case R.id.navigation_winner:

                    return true;
            }
            return false;
        }
    };
    private void moveToHome(){
        Intent intent=new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void moveToCategory(){
        Intent intent=new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }
    private void moveToContact(){
        Intent intent=new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
    private void moveToProfile(){
        Intent intent=new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private void moveToLogin(){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    //toolbar======================
}