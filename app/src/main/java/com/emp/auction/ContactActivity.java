package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        if(Common.getInstance().getUser().equals("guest12345")) {onBackPressed();}

        //toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Contact us");
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
        navigation.setSelectedItemId(R.id.navigation_contact);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------
        final ImageView imgSubmit=(ImageView) findViewById(R.id.imageSubmit);
        imgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSubmit.setEnabled(false);
                sendMessage();
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
    private void sendMessage(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        TextView editMessage =(TextView) findViewById(R.id.editMessage);
        String message=editMessage.getText().toString();
        if(message.trim().isEmpty()){
            editMessage.setError("Enter message");
        }else{
            editMessage.setError(null);

        }
        JsonObject json = new JsonObject();
        json.addProperty("username", Common.getInstance().getUser());
        json.addProperty("message",message);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL() + "sendMessage.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), "You message is successfully sent to administrator.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "You message is not sent.", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(getBaseContext(), "You message is not sent.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch (Exception e){

        }
    }
}
