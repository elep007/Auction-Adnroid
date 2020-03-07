package com.emp.auction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PostActivity extends AppCompatActivity {
    private EditText _mEditName;
    private TextView _mEditDescription;
    private EditText _mEditPrice;
    private Button mButton;
    private String selectedCategory;

    private SliderLayout sliderLayout;
    private ImageView imageView;
    private VideoView videoView;
    private List<String> imageUrls;
    private String videoPath=null;

    private MediaController mc;

    private ImageView image_video_toggle;
    private boolean isVideo;

    private boolean imageDone;
    private boolean priceDone;
    private boolean descriptionDone;
    private boolean nameDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if(Common.getInstance().getUser().equals("guest12345")) onBackPressed();

        _mEditName = (EditText) findViewById(R.id.editProductName);
        _mEditDescription = (EditText) findViewById(R.id.txtDescription);
        _mEditPrice = (EditText) findViewById(R.id.editPrice);

        imageView = (ImageView) findViewById(R.id.image_View);
        videoView=(VideoView) findViewById(R.id.video_View);
        mc = new MediaController(this);

//toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Auction Post");
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
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------
        mButton=(Button)findViewById(R.id.btnPost);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPost()){

                    //add post code

                    newPost();
                }

            }
        });

        Spinner spinner = (Spinner) this.findViewById(R.id.spinCategory);

        List<String> list = new ArrayList<>();

        list=Common.getInstance().getCategoryString();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                selectedCategory=parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageUrls=new ArrayList<>();
        videoPath="";
        imageDone=false;
        setImageEventListner();

//slider
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(5); //set scroll delay in seconds :
        //setSliderViews();
    }
    private void setImageEventListner(){
        image_video_toggle=(ImageView)findViewById(R.id.image_video_toggle);
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
        _mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckName=(ImageView) findViewById(R.id.imageCheckName);
                if(s.toString().trim().isEmpty()){
                    nameDone=false;
                    imageCheckName.setVisibility(View.GONE);
                }
                else{
                    nameDone=true;
                    imageCheckName.setImageResource(R.drawable.ic_done);
                    imageCheckName.setVisibility(View.VISIBLE);
                }
            }
        });
        _mEditDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckDescription=(ImageView) findViewById(R.id.imageCheckDescription);
                if(s.toString().trim().isEmpty()){
                    descriptionDone=false;
                    imageCheckDescription.setVisibility(View.GONE);
                }
                else{
                    descriptionDone=true;
                    imageCheckDescription.setImageResource(R.drawable.ic_done);
                    imageCheckDescription.setVisibility(View.VISIBLE);
                }
            }
        });
        _mEditPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckPrice=(ImageView) findViewById(R.id.imageCheckPrice);
                if(s.toString().isEmpty() || !s.toString().matches("\\d+(?:\\.\\d+)?") || Integer.valueOf(s.toString())<=0){
                    priceDone=false;
                    imageCheckPrice.setVisibility(View.GONE);
                }
                else{
                    priceDone=true;
                    imageCheckPrice.setImageResource(R.drawable.ic_done);
                    imageCheckPrice.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);

            for(Image theImage : images){
                String filePath=theImage.getPath();
                if( filePath.endsWith("jpg") || filePath.endsWith("JPG") || filePath.endsWith("png") || filePath.endsWith("bmp")) {
                    if(imageUrls.size()<5) {
                        imageUrls.add(filePath);
                    }
                }
                else{
                   videoPath=filePath;
                   videoView.setVideoURI(Uri.parse(filePath));
                   videoView.setMediaController(mc);
                   if(isVideo){
                       videoView.start();
                   }
                }
            }
            setSliderViews();

            ImageView imageCheckImage=(ImageView)findViewById(R.id.imageCheckImage);
            if(imageUrls.size()>0){
                imageDone=true;
                imageCheckImage.setImageResource(R.drawable.ic_done);
                imageCheckImage.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setSliderViews() {
        sliderLayout.clearSliderViews();
        for (String theImageUrl: imageUrls) {

            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.setImageUrl(theImageUrl);

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);

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

    public void onPickImage(View view) {
        if(imageUrls.size()>=5 && !videoPath.isEmpty()) return;
        LinearLayout layAddImage=(LinearLayout) findViewById(R.id.imageSelectButton);
        layAddImage.setVisibility(View.GONE);

        ImagePicker.create(this).limit(6).includeVideo(true).start();
//                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
//                .folderMode(true) // folder mode (false by default)
//                .toolbarFolderTitle("Folder") // folder selection title
//                .toolbarImageTitle("Tap to select") // image selection title
//                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
//                .includeVideo(true) // Show video on image picker
//                .single() // single mode
//                .multi() // multi mode (default mode)
//                .limit(10) // max images can be selected (99 by default)
//                .showCamera(true) // show camera or not (true by default)
//                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .enableLog(false) // disabling log
//                .start(); // start image picker activity with request code
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
    //toolbar======================
    private boolean isValidPost(){
        ImageView imageCheckName=(ImageView) findViewById(R.id.imageCheckName);
        ImageView imageCheckImage=(ImageView) findViewById(R.id.imageCheckImage);
        ImageView imageCheckDescription=(ImageView) findViewById(R.id.imageCheckDescription);
        ImageView imageCheckPrice=(ImageView) findViewById(R.id.imageCheckPrice);
        boolean valid = true;

//        String productName = _mEditName.getText().toString();
//        String description = _mEditDescription.getText().toString();
//        String price = _mEditPrice.getText().toString();

        if(selectedCategory.isEmpty()){
            valid = false;
        }
        if(imageDone==false){
            valid=false;
            imageCheckImage.setVisibility(View.VISIBLE);
            imageCheckImage.setImageResource(R.drawable.ic_error);
        }
        if(descriptionDone==false){
            valid=false;
            imageCheckDescription.setVisibility(View.VISIBLE);
            imageCheckDescription.setImageResource(R.drawable.ic_error);
        }
        if(priceDone==false){
            valid=false;
            imageCheckPrice.setVisibility(View.VISIBLE);
            imageCheckPrice.setImageResource(R.drawable.ic_error);
        }
        if(nameDone==false){
            valid=false;
            imageCheckName.setVisibility(View.VISIBLE);
            imageCheckName.setImageResource(R.drawable.ic_error);
        }

        return valid;
    }
    private void newPost(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mButton.setEnabled(false);
        mButton.setBackgroundColor(Color.GRAY);

        JSONArray uploadImages = new JSONArray();
        for(String theImage:imageUrls){
            Bitmap bm = BitmapFactory.decodeFile(theImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            uploadImages.put(encodedImage);
        }
        if(!videoPath.isEmpty()) {
            String videodata = "";
            try {
                File file = new File(videoPath);
                byte[] buffer = new byte[(int) file.length() + 100];
                int length = new FileInputStream(file).read(buffer);
                videodata = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImages.put(videodata);
        }
        JsonObject json = new JsonObject();
        json.addProperty("categoryname", selectedCategory);
        json.addProperty("name",_mEditName.getText().toString());
        json.addProperty("isvideo",videoPath.isEmpty() ? "no":"video");
        json.addProperty("description",  _mEditDescription.getText().toString());
        json.addProperty("price", _mEditPrice.getText().toString());
        json.addProperty("lastbid", "no bid");
        json.addProperty("uploadfile", uploadImages.toString());
        json.addProperty("seller", Common.getInstance().getUser());
        //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"productregister.php")
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
                                    mButton.setEnabled(false);
                                } else {
                                    Toast.makeText(getBaseContext(), "You bid fail. Try again.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Sorry. connect fail.", Toast.LENGTH_LONG).show();
                                Log.d("err",e.toString());
                            }
                            moveToCategory();
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
