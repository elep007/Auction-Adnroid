package com.emp.auction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.vincan.medialoader.MediaLoader;

import org.json.JSONException;

import java.math.BigDecimal;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "paymentExample";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AQQLvdR26OxNhCsQq_Glz3zmW8qjQ7xYZQfRyPbXFoRRFpwQvyZNQiRK63pi6Os6DTarZu-imCZFXOa-";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .rememberUser(false);



    MediaController mc;
    private Button mButton;

    private SliderLayout sliderLayout;
    private boolean isVideo=false;
    private CountDownTimer endTimer;

    private int mid;
    private String mName;
    private String mDescription;
    private int mPrice;
    private int mCountdown;
    private long mEndtime;
    private String mImage;
    private String mVideo;
    private String mLastbid;
    private String mSeller;
    private String mPayStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mc = new MediaController(this);

        mid = getIntent().getIntExtra("id", 0);
        mName=getIntent().getStringExtra("name");
        mImage = getIntent().getStringExtra("image");
        mEndtime = getIntent().getLongExtra("endtime",0);
        mLastbid = getIntent().getStringExtra("lastbid");
        mDescription = getIntent().getStringExtra("description");
        mPrice = getIntent().getIntExtra("price", 0);
        mCountdown = getIntent().getIntExtra("countdown", 0);
        mVideo = getIntent().getStringExtra("video");
        mSeller=getIntent().getStringExtra("seller");
        mPayStatus=getIntent().getStringExtra("paystatus");
        //loadProductData(id);


//toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((mName.isEmpty())?"Auction Detail":mName);//mProduct.getmName());
        //toolbar.setSubtitle("Subtitle");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.backbutton);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_purchase);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------
//dialog====================
        final Context c = this;
        mButton=(Button)findViewById(R.id.btnBid);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mButton.getText().toString().equals("Contact")){
                    getContact();
                    return;
                }
                if(Common.getInstance().getUser()=="guest12345"){
                    new android.app.AlertDialog.Builder(c)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Auction")
                            .setMessage("Your should login.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    moveToSignup();
                                    finish();
                                }

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return;
                }
                if(mSeller.equals(Common.getInstance().getUser())){
                    new android.app.AlertDialog.Builder(c)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Auction")
                            .setMessage("You can't bid on your product.")
                            .setNegativeButton("Close", null)
                            .show();
                    return;
                }
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputValue = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Bid", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String temp=userInputValue.getText().toString();
                                if(temp.matches("\\d+(?:\\.\\d+)?")) {
                                    upDateBid(Integer.valueOf(temp));
                                }
                                else{
                                    upDateBid(0);
                                }

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
        //dialog--------------
//slider
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(5); //set scroll delay in seconds :
        //setSliderViews();

        final VideoView videoView=(VideoView)findViewById(R.id.video_View);
        final ImageView image_video_toggle=(ImageView)findViewById(R.id.image_video_toggle);
        image_video_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVideo){
                    isVideo=false;
                    videoView.pause();
                    sliderLayout.setVisibility(View.VISIBLE);
                    image_video_toggle.setImageResource(R.drawable.ic_post_video);

                }
                else{
                    isVideo=true;
                    sliderLayout.setVisibility(View.GONE);
                    videoView.start();
                    image_video_toggle.setImageResource(R.drawable.ic_post_image);
                }

            }
        });
        initView();
        updateView();
        //Log.d("create","now");
    }

    private void getContact(){
        if(mPayStatus.equals("yes")){
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();


            JsonObject json = new JsonObject();
            json.addProperty("username",mSeller);
            try {
                Ion.with(this)
                        .load(Common.getInstance().getBaseURL()+"getuser.php")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                if (result != null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        JsonObject theProdut = result.get("user").getAsJsonObject();

                                        String mobile = theProdut.get("mobile").getAsString();
                                        String email = theProdut.get("email").getAsString();
                                        String name=theProdut.get("name").getAsString();
                                        showSellerInfo(name,email,mobile);
                                    }
                                }
                            }
                        });
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else{//paypal payment and register on server
            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal("0.99"), "USD", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

            startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //add to code to send payment info
                        mPayStatus="yes";
                        setResult(102);
                        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        JsonObject json = new JsonObject();
                        json.addProperty("id",mid);
                        try {
                            Ion.with(this)
                                    .load(Common.getInstance().getBaseURL()+"getseller.php")
                                    .setJsonObjectBody(json)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            progressDialog.dismiss();
                                            if (result != null) {
                                                String status = result.get("status").getAsString();
                                                if (status.equals("ok")) {
                                                    JsonObject theProdut = result.get("seller").getAsJsonObject();

                                                    String name= theProdut.get("name").getAsString();
                                                    String mobile = theProdut.get("mobile").getAsString();
                                                    String email = theProdut.get("email").getAsString();
                                                    showSellerInfo(name,email,mobile);

                                                    Toast.makeText(getBaseContext(), "Your payment is successful.", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(getBaseContext(), "Error. Please contact to administrator", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                        }catch(Exception e){
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        //Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                        Toast.makeText(getBaseContext(), "an extremely unlikely failure occurred:"+e, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Log.i(TAG, "The user canceled.");
                Toast.makeText(getBaseContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getBaseContext(), "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showSellerInfo(String name, String email, String mobile){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.seller_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        TextView _name=(TextView) mView.findViewById(R.id.dialogName);
        TextView _email=(TextView) mView.findViewById(R.id.dialogEmail);
        TextView _mobile=(TextView) mView.findViewById(R.id.dialogMobile);
        _name.setText(name);
        _email.setText(email);
        _mobile.setText(mobile);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    @Override
    protected void onStop() {
        Log.d("test","stoped");
        stopService(new Intent(this, PayPalService.class));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(endTimer!=null) {
            endTimer.cancel();
        }
        super.onDestroy();
        //Log.d("destroy","now");
    }

    private void upDateBid(int updatePrice){
        if(mPrice>=updatePrice){
            new android.app.AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Auction")
                    .setMessage("Your bid should be lager than the current bid.")
                    .setNegativeButton("Close", null)
                    .show();
            return;
        }

        //update mProduct, update price, lastbid, endtime, countdown in view and update Dateabase
        mPrice=updatePrice;
        mCountdown++;
        mLastbid=Common.getInstance().getUser();

        updateView();

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        mButton.setEnabled(false);
        mButton.setBackgroundColor(Color.GRAY);

        JsonObject json = new JsonObject();
        json.addProperty("lastbid", mLastbid);
        json.addProperty("price",mPrice);
        json.addProperty("countdown",mCountdown);
        json.addProperty("id",mid);
    try {
        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"updatebid.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result != null) {
                            String status = result.get("status").getAsString();
                            if (status.equals("ok")) {
                                Toast.makeText(getBaseContext(), "You bid is successfully sent.", Toast.LENGTH_LONG).show();
                                setResult(102);
                            }
                        }
                    }
                });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void initView(){
        //TextView mName = (TextView) findViewById(R.id.textProductName);
        //mName.setText(mProduct.getmName());

        TextView _Description = (TextView) findViewById(R.id.txtDescription);
        _Description.setText(mDescription);

        long currentTime=System.currentTimeMillis()/1000;
        if(currentTime>mEndtime){
            mButton = (Button) findViewById(R.id.btnBid);
            if(mLastbid.equals(Common.getInstance().getUser())){
                mButton.setText("Contact");

                //start paypal service
                Intent intent = new Intent(this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(intent);
            }
            else {
                mButton.setEnabled(false);
                mButton.setBackgroundColor(Color.GRAY);

                TextView mEndTime = (TextView) findViewById(R.id.txtEndTime);
                mEndTime.setText("Ended");
            }
        }
        else{
            countdownStart(mEndtime-currentTime);
        }

        if(!mVideo.isEmpty()) {
            VideoView video=(VideoView) findViewById(R.id.video_View);
            String proxyUrl = MediaLoader.getInstance(this).getProxyUrl(Common.getInstance().getBaseURL() + mVideo);
            video.setVideoPath(proxyUrl);
            video.setMediaController(mc);
            //video.start();
        }
        setSliderViews();
    }
    private void updateView(){
        TextView _Price = (TextView) findViewById(R.id.txtPrice);
        _Price.setText(Integer.toString(mPrice));

        TextView _Countdown = (TextView) findViewById(R.id.txtCountdown);
        _Countdown.setText(Integer.toString(mCountdown));

        TextView _LastBid = (TextView) findViewById(R.id.txtLastBid);
        _LastBid.setText(mLastbid);
    }

    private void setSliderViews() {
        String[] imageUrls=mImage.split("_split_");
        for (int i=0;i<imageUrls.length;i++) {
            String theImageUrl=Common.getInstance().getBaseURL()+imageUrls[i];

            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.setImageUrl(theImageUrl);

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            //sliderView.setDescription("The quick brown fox jumps over the lazy dog.\n" +
            //        "Jackdaws love my big sphinx of quartz. " + (i + 1));
            //final int finalI = i;

            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    // Toast.makeText(PostActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();

                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }
    //==================toolbar
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
        finish();
    }
    private void moveToWinner(){
        Intent intent=new Intent(this, WinnerActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToCategory(){
        Intent intent=new Intent(this, CategoriesActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToContact(){
        Intent intent=new Intent(this, ContactActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToProfile(){
        Intent intent=new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToLogin(){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToSignup(){
        Intent intent=new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
    //toolbar======================

    private void loadProductData(int id){
        JsonObject json = new JsonObject();
        json.addProperty("id", id);

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"getproductdata.php")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    JsonObject theProdut = result.get("product").getAsJsonObject();

                                    mid = theProdut.get("id").getAsInt();
                                    //int catid = theProdut.get("catid").getAsInt();
                                    mName = theProdut.get("name").getAsString();
                                    mImage = theProdut.get("image").getAsString();
                                    mPrice = theProdut.get("price").getAsInt();
                                    mCountdown = theProdut.get("countdown").getAsInt();
                                    //mEndtime = theProdut.get("endtime").getAsString();
                                    mLastbid = theProdut.get("lastbid").getAsString();
                                    mDescription = theProdut.get("description").getAsString();
                                    String isvideo = theProdut.get("isvideo").getAsString();
                                    mVideo=theProdut.get("video").getAsString();

                                    initView();
                                    updateView();
                                }
                            }
                        }
                    });
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void countdownStart(long endtime){
        endTimer=new CountDownTimer(endtime*1000,1000){
            public void onTick(long millisUntilFinished){
                TextView mEndTime = (TextView) findViewById(R.id.txtEndTime);
                long countdownSeconds=millisUntilFinished/1000;
                long s=countdownSeconds % 60;
                long min=countdownSeconds/60;
                long h=countdownSeconds/3600;
                long i=min % 60;
                mEndTime.setText(String.format("%02d:%02d:%02d",h,i,s));
                //Log.d("counttimer",this.toString());
            }
            public void onFinish(){
                TextView mEndTime = (TextView) findViewById(R.id.txtEndTime);
                mEndTime.setText("Ended");
            }
        }.start();
    }
}
