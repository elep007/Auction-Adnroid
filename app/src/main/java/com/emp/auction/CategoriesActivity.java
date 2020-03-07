package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class CategoriesActivity extends AppCompatActivity {
//    private ArrayList<Categories> categoriesList;
    private GridView gridView;
    private Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

 //toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Auction Category");
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
        navigation.setSelectedItemId(R.id.navigation_purchase);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------

        gridView = findViewById(R.id.list_item_categoires);
        //categoriesList = new ArrayList<>();
        updateCategory();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //   String itemValue = (String) listView.getItemAtPosition(position);
                // selected item
                String name =((TextView)view.findViewById(R.id.txtCategoryName)).getText().toString();

                int catid=Common.getInstance().getCategories().get(position).getmID();
                Intent intent = new Intent(view.getContext(), ProductsActivity.class)
                        .putExtra("catid", String.valueOf(catid)).putExtra("title", name);
                startActivity(intent);

            }
        });
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
                    moveToWinner();
                    return true;
            }
            return false;
        }
    };
    private void moveToHome(){
        Intent intent=new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void moveToWinner(){
        Intent intent=new Intent(this, WinnerActivity.class);
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
    private void updateCategory(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Common.getInstance().getCategories().clear();
        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"getcategories.php")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
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

                                    Common.getInstance().getCategories().add(new Categories(id, name, image,count));
                                }
                                if(Common.getInstance().getCategories().size()>0) {
                                    CategoriesAdapter mAdapter = new CategoriesAdapter(context, Common.getInstance().getCategories());
                                    gridView.setAdapter(mAdapter);
                                }

                            }
                        }
                    }
                });
    }

}
