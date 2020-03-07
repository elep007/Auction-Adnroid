package com.emp.auction;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.design.widget.BottomNavigationView;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.MediaController;
        import android.widget.Spinner;
        import android.widget.Toast;
        import android.widget.VideoView;

        import com.esafirm.imagepicker.features.ImagePicker;
        import com.esafirm.imagepicker.model.Image;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonElement;
        import com.google.gson.JsonObject;
        import com.koushikdutta.async.future.FutureCallback;
        import com.koushikdutta.ion.Ion;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {
    private EditText _mEditName;
    private ImageView imageView;

    Image image;
    String filePath;

    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        _mEditName = (EditText) findViewById(R.id.editCategoryName);


        imageView = (ImageView) findViewById(R.id.image_View);

//toolbar============================
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Category");
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
        mButton=(Button)findViewById(R.id.btnAdd);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    addCategory();
                }

            }
        });
        Button mButton1=(Button)findViewById(R.id.btnCancel);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            image = ImagePicker.getFirstImageOrNull(data);
            if(image!=null) {
                //imageView.setImageBitmap();
                filePath=image.getPath();

                imageView.setImageURI(Uri.parse(filePath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onPickImage(View view) {
        ImagePicker.create(this).single().toolbarFolderTitle("Folder").start();
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
    private boolean isValid(){
        boolean valid = true;

        String categoryName = _mEditName.getText().toString();

        if(image==null){
            valid=false;
        }
        if (categoryName.isEmpty()) {
            _mEditName.setError("Enter product name");
            valid = false;
        } else {
            _mEditName.setError(null);
        }

        return valid;
    }
    private void addCategory(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        mButton.setEnabled(false);
        mButton.setBackgroundColor(Color.GRAY);

        final File fileToUpload = new File(filePath);
        try {
            Ion.with(this)
                    .load("POST", Common.getInstance().getBaseURL()+"categoryregister.php")
                    .setMultipartParameter("categoryname", _mEditName.getText().toString())
                    .setMultipartFile("uploadfile", "media", fileToUpload)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), "The category is successfully registered.", Toast.LENGTH_LONG).show();
                                    updateCategory();

                                }
                                else if(status.equals("existcategory")){
                                    Toast.makeText(getBaseContext(), "Entered category name is already exist.", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getBaseContext(), "Register fail. Try again.", Toast.LENGTH_LONG).show();
                                }
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void updateCategory(){

        Common.getInstance().getCategories().clear();
        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"getcategories.php")
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

                                    Common.getInstance().getCategories().add(new Categories(id, name, image,count));
                                }

                                moveToHome();
                            }
                        }
                    }
                });
    }

}
