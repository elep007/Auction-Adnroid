package com.emp.auction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<Products> productsList;
    private ListView listView;
    private ArrayList<Winner> mWinners=new ArrayList<>();
    private Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------
//        ImageView imageSearch = (ImageView) findViewById(R.id.imageSearch);
//        imageSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //getSearchProduct();
//            }
//        });


//        listView = findViewById(R.id.list_item_products);
//        productsList = new ArrayList<>();
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                 Products theProduct=productsList.get(position);
//                Intent intent = new Intent(view.getContext(), ProductDetailActivity.class).putExtra("name",theProduct.getmName())
//                        .putExtra("price",theProduct.getmPrice()).putExtra("countdown",theProduct.getmCountdown())
//                        .putExtra("lastbid",theProduct.getmLastBid()).putExtra("endtime",theProduct.getEndTime())
//                        .putExtra("image",theProduct.getmImage()).putExtra("description",theProduct.getDescription())
//                        .putExtra("id",theProduct.getmID()).putExtra("isvideo",theProduct.getIsVideo());
//                //startActivity(intent);
//                startActivityForResult(intent, 101);
//
//            }
//        });
        LinearLayout _btnPost=(LinearLayout) findViewById(R.id.layNewPost);
        _btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAuctionPost();
            }
        });
        LinearLayout _btnAddCategory=(LinearLayout) findViewById(R.id.addCategoryLayout);
        _btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToAddCategory();
            }
        });
        updateBidCount();
        String temp=Common.getInstance().getUserRole();
        if(!Common.getInstance().getUserRole().equals("admin")){
            LinearLayout addCategory=(LinearLayout) findViewById(R.id.addCategoryLayout);
            addCategory.setVisibility(View.GONE);
        }
        if(Common.getInstance().getUser().equals("guest12345")){
            LinearLayout addPost=(LinearLayout) findViewById(R.id.layNewPost);
            addPost.setVisibility(View.GONE);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == 102) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //getSearchProduct();
            }
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

    private void newAuctionPost(){
            Intent intent=new Intent(this, PostActivity.class);
            startActivity(intent);
    }
    private void moveToAddCategory(){
        Intent intent=new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
    }
//    private void getSearchProduct(){
//        listView = findViewById(R.id.list_item_products);
//        listView.setAdapter(null);
//        productsList = new ArrayList<>();
//
//        EditText editSearch=(EditText) findViewById(R.id.inputSearch);
//        String pattern=editSearch.getText().toString();
//        if(pattern.isEmpty()){return;}
//
//        //ion request======================
//        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Loading...");
//        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        progressDialog.show();
//
//        JsonObject json = new JsonObject();
//        json.addProperty("pattern", pattern);
//
//        try {
//            Ion.with(this)
//                    .load(Common.getInstance().getBaseURL()+"getsearchproducts.php")
//                    .setJsonObjectBody(json)
//                    .asJsonObject()
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//                            progressDialog.dismiss();
//                            if (result != null) {
//                                String status = result.get("status").getAsString();
//                                if (status.equals("ok")) {
//                                    JsonArray products = result.get("products").getAsJsonArray();
//                                    for (JsonElement productElement : products) {
//                                        JsonObject theProdut = productElement.getAsJsonObject();
//
//                                        int id = theProdut.get("id").getAsInt();
//                                        int catid = theProdut.get("catid").getAsInt();
//                                        String name = theProdut.get("name").getAsString();
//                                        String image = theProdut.get("image").getAsString();
//                                        int price = theProdut.get("price").getAsInt();
//                                        int countdown = theProdut.get("countdown").getAsInt();
//                                        String endtime = theProdut.get("endtime").getAsString();
//                                        String lastbid = theProdut.get("lastbid").getAsString();
//                                        String description = theProdut.get("description").getAsString();
//                                        String isvideo=theProdut.get("isvideo").getAsString();
//                                        productsList.add(new Products(id, catid, name, image, price, countdown, lastbid, endtime, description,isvideo));
//                                    }
//                                    Collections.reverse(productsList);
//                                    ProductsAdapter mAdapter = new ProductsAdapter(context, productsList);
//                                    listView.setAdapter(mAdapter);
//                                }
//                            } else {
//                            }
//                        }
//                    });
//        }
//        catch(Exception e){
//            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
    private void updateBidCount(){
        if(Common.getInstance().getUser().equals("guest12345")) return;
        JsonObject json = new JsonObject();
        json.addProperty("username", Common.getInstance().getUser());
        final Context context=this.context;

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"getbiddata.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    int activeBids= result.get("active").getAsInt();
                                    int totalBids=result.get("total").getAsInt();
                                    int wonBids=result.get("won").getAsInt();
                                    String name=result.get("name").getAsString();
                                    String avatar=result.get("avatar").getAsString();

                                    TextView _username=(TextView) findViewById(R.id.txtUsername);
                                    TextView _name=(TextView) findViewById(R.id.txtName);
                                    TextView _activeBids=(TextView) findViewById(R.id.txtActiveBids);
                                    TextView _totalBids=(TextView) findViewById(R.id.txtTotalBids);
                                    TextView _wonBids=(TextView) findViewById(R.id.txtWonBids);

                                    _username.setText("@"+Common.getInstance().getUser());
                                    _name.setText(name);
                                    _activeBids.setText(Integer.toString(activeBids));
                                    _totalBids.setText(Integer.toString(totalBids));
                                    _wonBids.setText(Integer.toString(wonBids));

                                    if(avatar.trim().isEmpty()) return;
                                    final ImageView image = (ImageView) findViewById(R.id.profile_image);
                                    String imagePath=Common.getInstance().getBaseURL()+avatar;
                                    Ion.with(context)
                                            .load(imagePath)
                                            .asBitmap()
                                            .setCallback(new FutureCallback<Bitmap>() {
                                                @Override
                                                public void onCompleted(Exception e, Bitmap result) {
                                                    if(e==null){
                                                        image.setImageBitmap(result);
                                                    }
                                                }
                                            });
                                }
                            }
                            checkWinners();
                        }
                    });
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Auction")
                .setMessage("Are you sure you want to close this Auction?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveToLogin();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void checkWinners(){
        if(Common.getInstance().getUser().equals("guest12345")) return;
        mWinners.clear();
        final LinearLayout layWinner=(LinearLayout)findViewById(R.id.layWinner);
        JsonObject json = new JsonObject();
        json.addProperty("role", Common.getInstance().getUserRole());
        json.addProperty("username", Common.getInstance().getUser());
        try {
            Ion.with(context)
                    .load(Common.getInstance().getBaseURL()+"checkwinner.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    JsonArray winners = result.get("winners").getAsJsonArray();
                                    for (JsonElement winnerElement : winners) {
                                        JsonObject theWinner = winnerElement.getAsJsonObject();
                                        String username = theWinner.get("username").getAsString();
                                        String name = theWinner.get("name").getAsString();
                                        String mobile = theWinner.get("mobile").getAsString();
                                        String productname = theWinner.get("productname").getAsString();
                                        int price = theWinner.get("price").getAsInt();
                                        int id=theWinner.get("id").getAsInt();
                                        mWinners.add(new Winner(username,name, mobile, productname,price,id));
                                    }

                                    addNotification();

                                    layWinner.setVisibility(View.VISIBLE);
                                    TextView txtWinnerCount=(TextView)findViewById(R.id.textWinnerCount);
                                    String temp=(mWinners.size()==1)?"a auction":String.valueOf(mWinners.size())+" auctions";
                                    txtWinnerCount.setText("You won in "+temp);
                                }
                                else{
                                    //layWinner.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
            layWinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToWinner();
                }
            });
        }
        catch(Exception e){
            //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void addNotification(){ //check if there are winners and push notification

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        if(mWinners.size()>0){
            //tap Action

            int notificationID=-1;
            for(Winner theWinner: mWinners){

                //tap Action
                //Intent intent = new Intent(this, ProductDetailActivity.class).putExtra("id",theWinner.getmId());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                notificationID++;

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
                notificationBuilder.setAutoCancel(true)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setContentTitle("Winner : "+theWinner.getmUsername()+", Name:"+theWinner.getmName()+", Mobile:"+theWinner.getmMobile())
                        .setContentText("In Auction: "+theWinner.getmProductName()+", Price:"+Integer.toString(theWinner.getmPrice()))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        //.setContentIntent(pendingIntent)
                        .setContentInfo("Information");
                notificationManager.notify(notificationID, notificationBuilder.build());
            }
        }
    }
}
