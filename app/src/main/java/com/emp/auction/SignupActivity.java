package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _email;
    EditText _usernameText;
    EditText _usernameTextRepeat;
    EditText _mobileText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;
    private String selectedCountryCode;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_signup);
//        getSupportActionBar().hide(); //hide the title bar

        _nameText = (EditText) findViewById(R.id.input_name);
        _email=(EditText) findViewById(R.id.input_email);
        _usernameText = (EditText) findViewById(R.id.input_username);
        _usernameTextRepeat=(EditText) findViewById(R.id.input_username_repeat);
        _mobileText = (EditText) findViewById(R.id.input_mobile);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        setCountryCode();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Enter your information correctly.");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _email.getText().toString().toLowerCase ();
        String username = _usernameText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        //String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();
        json.addProperty("name",name);
        json.addProperty("email",email);
        json.addProperty("mobile",selectedCountryCode+mobile);
        json.addProperty("username", username);
        json.addProperty("password",password);
    try {
        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"userregister.php")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result != null) {
                            String status = result.get("status").getAsString();
                            if (status.equals("ok")) {
                                onSignupSuccess();
                            } else if (status.equals("existuser")) {
                                onSignupFailed("Your username is already using.");
                            } else if (status.equals("existmobile")) {
                                onSignupFailed("Your mobile has been already registered.");
                            } else if (status.equals("fail")) {
                                onSignupFailed("Sign in fail. Try again.");
                            }
                        } else {

                        }
                    }
                });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);

        Common.getInstance().setUser(_usernameText.getText().toString());
        finish();
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String reusername=_usernameTextRepeat.getText().toString();
        String email = _email.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("at least 4 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("Enter valid email");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (username.isEmpty() || username.length()<4){
            _usernameText.setError("Enter a valid username more than 4 characters");
            valid = false;
        }
        else if(!username.equals(reusername)){
            _usernameTextRepeat.setError("Username do not match");
            valid = false;
        }
        else {
            _usernameText.setError(null);
        }

        if (mobile.isEmpty() || !mobile.matches("\\d+(?:\\.\\d+)?") || mobile.length()<8) {
            _mobileText.setError("Enter valid mobile number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("Please enter  more than 4 character.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
    private String readCountryCodeWithJsonString() throws java.io.IOException {
        String base64 = "W3sibmFtZSI6IkFmZ2hhbmlzdGFuIiwiZGlhbF9jb2RlIjoiKzkzIiwiY29kZSI6IkFGIn0seyJuYW1lIjoiQWxiYW5pYSIsImRpYWxfY29kZSI6IiszNTUiLCJjb2RlIjoiQUwifSx7Im5hbWUiOiJBbGdlcmlhIiwiZGlhbF9jb2RlIjoiKzIxMyIsImNvZGUiOiJEWiJ9LHsibmFtZSI6IkFtZXJpY2FuU2Ftb2EiLCJkaWFsX2NvZGUiOiIrMSA2ODQiLCJjb2RlIjoiQVMifSx7Im5hbWUiOiJBbmRvcnJhIiwiZGlhbF9jb2RlIjoiKzM3NiIsImNvZGUiOiJBRCJ9LHsibmFtZSI6IkFuZ29sYSIsImRpYWxfY29kZSI6IisyNDQiLCJjb2RlIjoiQU8ifSx7Im5hbWUiOiJBbmd1aWxsYSIsImRpYWxfY29kZSI6IisxIDI2NCIsImNvZGUiOiJBSSJ9LHsibmFtZSI6IkFudGFyY3RpY2EiLCJkaWFsX2NvZGUiOiIrNjcyIiwiY29kZSI6IkFRIn0seyJuYW1lIjoiQW50aWd1YSBhbmQgQmFyYnVkYSIsImRpYWxfY29kZSI6IisxMjY4IiwiY29kZSI6IkFHIn0seyJuYW1lIjoiQXJnZW50aW5hIiwiZGlhbF9jb2RlIjoiKzU0IiwiY29kZSI6IkFSIn0seyJuYW1lIjoiQXJtZW5pYSIsImRpYWxfY29kZSI6IiszNzQiLCJjb2RlIjoiQU0ifSx7Im5hbWUiOiJBcnViYSIsImRpYWxfY29kZSI6IisyOTciLCJjb2RlIjoiQVcifSx7Im5hbWUiOiJBdXN0cmFsaWEiLCJkaWFsX2NvZGUiOiIrNjEiLCJjb2RlIjoiQVUifSx7Im5hbWUiOiJBdXN0cmlhIiwiZGlhbF9jb2RlIjoiKzQzIiwiY29kZSI6IkFUIn0seyJuYW1lIjoiQXplcmJhaWphbiIsImRpYWxfY29kZSI6Iis5OTQiLCJjb2RlIjoiQVoifSx7Im5hbWUiOiJCYWhhbWFzIiwiZGlhbF9jb2RlIjoiKzEgMjQyIiwiY29kZSI6IkJTIn0seyJuYW1lIjoiQmFocmFpbiIsImRpYWxfY29kZSI6Iis5NzMiLCJjb2RlIjoiQkgifSx7Im5hbWUiOiJCYW5nbGFkZXNoIiwiZGlhbF9jb2RlIjoiKzg4MCIsImNvZGUiOiJCRCJ9LHsibmFtZSI6IkJhcmJhZG9zIiwiZGlhbF9jb2RlIjoiKzEgMjQ2IiwiY29kZSI6IkJCIn0seyJuYW1lIjoiQmVsYXJ1cyIsImRpYWxfY29kZSI6IiszNzUiLCJjb2RlIjoiQlkifSx7Im5hbWUiOiJCZWxnaXVtIiwiZGlhbF9jb2RlIjoiKzMyIiwiY29kZSI6IkJFIn0seyJuYW1lIjoiQmVsaXplIiwiZGlhbF9jb2RlIjoiKzUwMSIsImNvZGUiOiJCWiJ9LHsibmFtZSI6IkJlbmluIiwiZGlhbF9jb2RlIjoiKzIyOSIsImNvZGUiOiJCSiJ9LHsibmFtZSI6IkJlcm11ZGEiLCJkaWFsX2NvZGUiOiIrMSA0NDEiLCJjb2RlIjoiQk0ifSx7Im5hbWUiOiJCaHV0YW4iLCJkaWFsX2NvZGUiOiIrOTc1IiwiY29kZSI6IkJUIn0seyJuYW1lIjoiQm9saXZpYSwgUGx1cmluYXRpb25hbCBTdGF0ZSBvZiIsImRpYWxfY29kZSI6Iis1OTEiLCJjb2RlIjoiQk8ifSx7Im5hbWUiOiJCb3NuaWEgYW5kIEhlcnplZ292aW5hIiwiZGlhbF9jb2RlIjoiKzM4NyIsImNvZGUiOiJCQSJ9LHsibmFtZSI6IkJvdHN3YW5hIiwiZGlhbF9jb2RlIjoiKzI2NyIsImNvZGUiOiJCVyJ9LHsibmFtZSI6IkJyYXppbCIsImRpYWxfY29kZSI6Iis1NSIsImNvZGUiOiJCUiJ9LHsibmFtZSI6IkJyaXRpc2ggSW5kaWFuIE9jZWFuIFRlcnJpdG9yeSIsImRpYWxfY29kZSI6IisyNDYiLCJjb2RlIjoiSU8ifSx7Im5hbWUiOiJCcnVuZWkgRGFydXNzYWxhbSIsImRpYWxfY29kZSI6Iis2NzMiLCJjb2RlIjoiQk4ifSx7Im5hbWUiOiJCdWxnYXJpYSIsImRpYWxfY29kZSI6IiszNTkiLCJjb2RlIjoiQkcifSx7Im5hbWUiOiJCdXJraW5hIEZhc28iLCJkaWFsX2NvZGUiOiIrMjI2IiwiY29kZSI6IkJGIn0seyJuYW1lIjoiQnVydW5kaSIsImRpYWxfY29kZSI6IisyNTciLCJjb2RlIjoiQkkifSx7Im5hbWUiOiJDYW1ib2RpYSIsImRpYWxfY29kZSI6Iis4NTUiLCJjb2RlIjoiS0gifSx7Im5hbWUiOiJDYW1lcm9vbiIsImRpYWxfY29kZSI6IisyMzciLCJjb2RlIjoiQ00ifSx7Im5hbWUiOiJDYW5hZGEiLCJkaWFsX2NvZGUiOiIrMSIsImNvZGUiOiJDQSJ9LHsibmFtZSI6IkNhcGUgVmVyZGUiLCJkaWFsX2NvZGUiOiIrMjM4IiwiY29kZSI6IkNWIn0seyJuYW1lIjoiQ2F5bWFuIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrIDM0NSIsImNvZGUiOiJLWSJ9LHsibmFtZSI6IkNlbnRyYWwgQWZyaWNhbiBSZXB1YmxpYyIsImRpYWxfY29kZSI6IisyMzYiLCJjb2RlIjoiQ0YifSx7Im5hbWUiOiJDaGFkIiwiZGlhbF9jb2RlIjoiKzIzNSIsImNvZGUiOiJURCJ9LHsibmFtZSI6IkNoaWxlIiwiZGlhbF9jb2RlIjoiKzU2IiwiY29kZSI6IkNMIn0seyJuYW1lIjoiQ2hpbmEiLCJkaWFsX2NvZGUiOiIrODYiLCJjb2RlIjoiQ04ifSx7Im5hbWUiOiJDaHJpc3RtYXMgSXNsYW5kIiwiZGlhbF9jb2RlIjoiKzYxIiwiY29kZSI6IkNYIn0seyJuYW1lIjoiQ29jb3MgKEtlZWxpbmcpIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrNjEiLCJjb2RlIjoiQ0MifSx7Im5hbWUiOiJDb2xvbWJpYSIsImRpYWxfY29kZSI6Iis1NyIsImNvZGUiOiJDTyJ9LHsibmFtZSI6IkNvbW9yb3MiLCJkaWFsX2NvZGUiOiIrMjY5IiwiY29kZSI6IktNIn0seyJuYW1lIjoiQ29uZ28iLCJkaWFsX2NvZGUiOiIrMjQyIiwiY29kZSI6IkNHIn0seyJuYW1lIjoiQ29uZ28sIFRoZSBEZW1vY3JhdGljIFJlcHVibGljIG9mIHRoZSIsImRpYWxfY29kZSI6IisyNDMiLCJjb2RlIjoiQ0QifSx7Im5hbWUiOiJDb29rIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrNjgyIiwiY29kZSI6IkNLIn0seyJuYW1lIjoiQ29zdGEgUmljYSIsImRpYWxfY29kZSI6Iis1MDYiLCJjb2RlIjoiQ1IifSx7Im5hbWUiOiJDb3RlIGQnSXZvaXJlIiwiZGlhbF9jb2RlIjoiKzIyNSIsImNvZGUiOiJDSSJ9LHsibmFtZSI6IkNyb2F0aWEiLCJkaWFsX2NvZGUiOiIrMzg1IiwiY29kZSI6IkhSIn0seyJuYW1lIjoiQ3ViYSIsImRpYWxfY29kZSI6Iis1MyIsImNvZGUiOiJDVSJ9LHsibmFtZSI6IkN5cHJ1cyIsImRpYWxfY29kZSI6IiszNTciLCJjb2RlIjoiQ1kifSx7Im5hbWUiOiJDemVjaCBSZXB1YmxpYyIsImRpYWxfY29kZSI6Iis0MjAiLCJjb2RlIjoiQ1oifSx7Im5hbWUiOiJEZW5tYXJrIiwiZGlhbF9jb2RlIjoiKzQ1IiwiY29kZSI6IkRLIn0seyJuYW1lIjoiRGppYm91dGkiLCJkaWFsX2NvZGUiOiIrMjUzIiwiY29kZSI6IkRKIn0seyJuYW1lIjoiRG9taW5pY2EiLCJkaWFsX2NvZGUiOiIrMSA3NjciLCJjb2RlIjoiRE0ifSx7Im5hbWUiOiJEb21pbmljYW4gUmVwdWJsaWMiLCJkaWFsX2NvZGUiOiIrMSA4NDkiLCJjb2RlIjoiRE8ifSx7Im5hbWUiOiJFY3VhZG9yIiwiZGlhbF9jb2RlIjoiKzU5MyIsImNvZGUiOiJFQyJ9LHsibmFtZSI6IkVneXB0IiwiZGlhbF9jb2RlIjoiKzIwIiwiY29kZSI6IkVHIn0seyJuYW1lIjoiRWwgU2FsdmFkb3IiLCJkaWFsX2NvZGUiOiIrNTAzIiwiY29kZSI6IlNWIn0seyJuYW1lIjoiRXF1YXRvcmlhbCBHdWluZWEiLCJkaWFsX2NvZGUiOiIrMjQwIiwiY29kZSI6IkdRIn0seyJuYW1lIjoiRXJpdHJlYSIsImRpYWxfY29kZSI6IisyOTEiLCJjb2RlIjoiRVIifSx7Im5hbWUiOiJFc3RvbmlhIiwiZGlhbF9jb2RlIjoiKzM3MiIsImNvZGUiOiJFRSJ9LHsibmFtZSI6IkV0aGlvcGlhIiwiZGlhbF9jb2RlIjoiKzI1MSIsImNvZGUiOiJFVCJ9LHsibmFtZSI6IkZhbGtsYW5kIElzbGFuZHMgKE1hbHZpbmFzKSIsImRpYWxfY29kZSI6Iis1MDAiLCJjb2RlIjoiRksifSx7Im5hbWUiOiJGYXJvZSBJc2xhbmRzIiwiZGlhbF9jb2RlIjoiKzI5OCIsImNvZGUiOiJGTyJ9LHsibmFtZSI6IkZpamkiLCJkaWFsX2NvZGUiOiIrNjc5IiwiY29kZSI6IkZKIn0seyJuYW1lIjoiRmlubGFuZCIsImRpYWxfY29kZSI6IiszNTgiLCJjb2RlIjoiRkkifSx7Im5hbWUiOiJGcmFuY2UiLCJkaWFsX2NvZGUiOiIrMzMiLCJjb2RlIjoiRlIifSx7Im5hbWUiOiJGcmVuY2ggR3VpYW5hIiwiZGlhbF9jb2RlIjoiKzU5NCIsImNvZGUiOiJHRiJ9LHsibmFtZSI6IkZyZW5jaCBQb2x5bmVzaWEiLCJkaWFsX2NvZGUiOiIrNjg5IiwiY29kZSI6IlBGIn0seyJuYW1lIjoiR2Fib24iLCJkaWFsX2NvZGUiOiIrMjQxIiwiY29kZSI6IkdBIn0seyJuYW1lIjoiR2FtYmlhIiwiZGlhbF9jb2RlIjoiKzIyMCIsImNvZGUiOiJHTSJ9LHsibmFtZSI6Ikdlb3JnaWEiLCJkaWFsX2NvZGUiOiIrOTk1IiwiY29kZSI6IkdFIn0seyJuYW1lIjoiR2VybWFueSIsImRpYWxfY29kZSI6Iis0OSIsImNvZGUiOiJERSJ9LHsibmFtZSI6IkdoYW5hIiwiZGlhbF9jb2RlIjoiKzIzMyIsImNvZGUiOiJHSCJ9LHsibmFtZSI6IkdpYnJhbHRhciIsImRpYWxfY29kZSI6IiszNTAiLCJjb2RlIjoiR0kifSx7Im5hbWUiOiJHcmVlY2UiLCJkaWFsX2NvZGUiOiIrMzAiLCJjb2RlIjoiR1IifSx7Im5hbWUiOiJHcmVlbmxhbmQiLCJkaWFsX2NvZGUiOiIrMjk5IiwiY29kZSI6IkdMIn0seyJuYW1lIjoiR3JlbmFkYSIsImRpYWxfY29kZSI6IisxIDQ3MyIsImNvZGUiOiJHRCJ9LHsibmFtZSI6Ikd1YWRlbG91cGUiLCJkaWFsX2NvZGUiOiIrNTkwIiwiY29kZSI6IkdQIn0seyJuYW1lIjoiR3VhbSIsImRpYWxfY29kZSI6IisxIDY3MSIsImNvZGUiOiJHVSJ9LHsibmFtZSI6Ikd1YXRlbWFsYSIsImRpYWxfY29kZSI6Iis1MDIiLCJjb2RlIjoiR1QifSx7Im5hbWUiOiJHdWVybnNleSIsImRpYWxfY29kZSI6Iis0NCIsImNvZGUiOiJHRyJ9LHsibmFtZSI6Ikd1aW5lYSIsImRpYWxfY29kZSI6IisyMjQiLCJjb2RlIjoiR04ifSx7Im5hbWUiOiJHdWluZWEtQmlzc2F1IiwiZGlhbF9jb2RlIjoiKzI0NSIsImNvZGUiOiJHVyJ9LHsibmFtZSI6Ikd1eWFuYSIsImRpYWxfY29kZSI6Iis1OTUiLCJjb2RlIjoiR1kifSx7Im5hbWUiOiJIYWl0aSIsImRpYWxfY29kZSI6Iis1MDkiLCJjb2RlIjoiSFQifSx7Im5hbWUiOiJIb2x5IFNlZSAoVmF0aWNhbiBDaXR5IFN0YXRlKSIsImRpYWxfY29kZSI6IiszNzkiLCJjb2RlIjoiVkEifSx7Im5hbWUiOiJIb25kdXJhcyIsImRpYWxfY29kZSI6Iis1MDQiLCJjb2RlIjoiSE4ifSx7Im5hbWUiOiJIb25nIEtvbmciLCJkaWFsX2NvZGUiOiIrODUyIiwiY29kZSI6IkhLIn0seyJuYW1lIjoiSHVuZ2FyeSIsImRpYWxfY29kZSI6IiszNiIsImNvZGUiOiJIVSJ9LHsibmFtZSI6IkljZWxhbmQiLCJkaWFsX2NvZGUiOiIrMzU0IiwiY29kZSI6IklTIn0seyJuYW1lIjoiSW5kaWEiLCJkaWFsX2NvZGUiOiIrOTEiLCJjb2RlIjoiSU4ifSx7Im5hbWUiOiJJbmRvbmVzaWEiLCJkaWFsX2NvZGUiOiIrNjIiLCJjb2RlIjoiSUQifSx7Im5hbWUiOiJJcmFuLCBJc2xhbWljIFJlcHVibGljIG9mIiwiZGlhbF9jb2RlIjoiKzk4IiwiY29kZSI6IklSIn0seyJuYW1lIjoiSXJhcSIsImRpYWxfY29kZSI6Iis5NjQiLCJjb2RlIjoiSVEifSx7Im5hbWUiOiJJcmVsYW5kIiwiZGlhbF9jb2RlIjoiKzM1MyIsImNvZGUiOiJJRSJ9LHsibmFtZSI6IklzbGUgb2YgTWFuIiwiZGlhbF9jb2RlIjoiKzQ0IiwiY29kZSI6IklNIn0seyJuYW1lIjoiSXNyYWVsIiwiZGlhbF9jb2RlIjoiKzk3MiIsImNvZGUiOiJJTCJ9LHsibmFtZSI6Ikl0YWx5IiwiZGlhbF9jb2RlIjoiKzM5IiwiY29kZSI6IklUIn0seyJuYW1lIjoiSmFtYWljYSIsImRpYWxfY29kZSI6IisxIDg3NiIsImNvZGUiOiJKTSJ9LHsibmFtZSI6IkphcGFuIiwiZGlhbF9jb2RlIjoiKzgxIiwiY29kZSI6IkpQIn0seyJuYW1lIjoiSmVyc2V5IiwiZGlhbF9jb2RlIjoiKzQ0IiwiY29kZSI6IkpFIn0seyJuYW1lIjoiSm9yZGFuIiwiZGlhbF9jb2RlIjoiKzk2MiIsImNvZGUiOiJKTyJ9LHsibmFtZSI6IkthemFraHN0YW4iLCJkaWFsX2NvZGUiOiIrNyA3IiwiY29kZSI6IktaIn0seyJuYW1lIjoiS2VueWEiLCJkaWFsX2NvZGUiOiIrMjU0IiwiY29kZSI6IktFIn0seyJuYW1lIjoiS2lyaWJhdGkiLCJkaWFsX2NvZGUiOiIrNjg2IiwiY29kZSI6IktJIn0seyJuYW1lIjoiS29yZWEsIERlbW9jcmF0aWMgUGVvcGxlJ3MgUmVwdWJsaWMgb2YiLCJkaWFsX2NvZGUiOiIrODUwIiwiY29kZSI6IktQIn0seyJuYW1lIjoiS29yZWEsIFJlcHVibGljIG9mIiwiZGlhbF9jb2RlIjoiKzgyIiwiY29kZSI6IktSIn0seyJuYW1lIjoiS3V3YWl0IiwiZGlhbF9jb2RlIjoiKzk2NSIsImNvZGUiOiJLVyJ9LHsibmFtZSI6Ikt5cmd5enN0YW4iLCJkaWFsX2NvZGUiOiIrOTk2IiwiY29kZSI6IktHIn0seyJuYW1lIjoiTGFvIFBlb3BsZSdzIERlbW9jcmF0aWMgUmVwdWJsaWMiLCJkaWFsX2NvZGUiOiIrODU2IiwiY29kZSI6IkxBIn0seyJuYW1lIjoiTGF0dmlhIiwiZGlhbF9jb2RlIjoiKzM3MSIsImNvZGUiOiJMViJ9LHsibmFtZSI6IkxlYmFub24iLCJkaWFsX2NvZGUiOiIrOTYxIiwiY29kZSI6IkxCIn0seyJuYW1lIjoiTGVzb3RobyIsImRpYWxfY29kZSI6IisyNjYiLCJjb2RlIjoiTFMifSx7Im5hbWUiOiJMaWJlcmlhIiwiZGlhbF9jb2RlIjoiKzIzMSIsImNvZGUiOiJMUiJ9LHsibmFtZSI6IkxpYnlhbiBBcmFiIEphbWFoaXJpeWEiLCJkaWFsX2NvZGUiOiIrMjE4IiwiY29kZSI6IkxZIn0seyJuYW1lIjoiTGllY2h0ZW5zdGVpbiIsImRpYWxfY29kZSI6Iis0MjMiLCJjb2RlIjoiTEkifSx7Im5hbWUiOiJMaXRodWFuaWEiLCJkaWFsX2NvZGUiOiIrMzcwIiwiY29kZSI6IkxUIn0seyJuYW1lIjoiTHV4ZW1ib3VyZyIsImRpYWxfY29kZSI6IiszNTIiLCJjb2RlIjoiTFUifSx7Im5hbWUiOiJNYWNhbyIsImRpYWxfY29kZSI6Iis4NTMiLCJjb2RlIjoiTU8ifSx7Im5hbWUiOiJNYWNlZG9uaWEsIFRoZSBGb3JtZXIgWXVnb3NsYXYgUmVwdWJsaWMgb2YiLCJkaWFsX2NvZGUiOiIrMzg5IiwiY29kZSI6Ik1LIn0seyJuYW1lIjoiTWFkYWdhc2NhciIsImRpYWxfY29kZSI6IisyNjEiLCJjb2RlIjoiTUcifSx7Im5hbWUiOiJNYWxhd2kiLCJkaWFsX2NvZGUiOiIrMjY1IiwiY29kZSI6Ik1XIn0seyJuYW1lIjoiTWFsYXlzaWEiLCJkaWFsX2NvZGUiOiIrNjAiLCJjb2RlIjoiTVkifSx7Im5hbWUiOiJNYWxkaXZlcyIsImRpYWxfY29kZSI6Iis5NjAiLCJjb2RlIjoiTVYifSx7Im5hbWUiOiJNYWxpIiwiZGlhbF9jb2RlIjoiKzIyMyIsImNvZGUiOiJNTCJ9LHsibmFtZSI6Ik1hbHRhIiwiZGlhbF9jb2RlIjoiKzM1NiIsImNvZGUiOiJNVCJ9LHsibmFtZSI6Ik1hcnNoYWxsIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrNjkyIiwiY29kZSI6Ik1IIn0seyJuYW1lIjoiTWFydGluaXF1ZSIsImRpYWxfY29kZSI6Iis1OTYiLCJjb2RlIjoiTVEifSx7Im5hbWUiOiJNYXVyaXRhbmlhIiwiZGlhbF9jb2RlIjoiKzIyMiIsImNvZGUiOiJNUiJ9LHsibmFtZSI6Ik1hdXJpdGl1cyIsImRpYWxfY29kZSI6IisyMzAiLCJjb2RlIjoiTVUifSx7Im5hbWUiOiJNYXlvdHRlIiwiZGlhbF9jb2RlIjoiKzI2MiIsImNvZGUiOiJZVCJ9LHsibmFtZSI6Ik1leGljbyIsImRpYWxfY29kZSI6Iis1MiIsImNvZGUiOiJNWCJ9LHsibmFtZSI6Ik1pY3JvbmVzaWEsIEZlZGVyYXRlZCBTdGF0ZXMgb2YiLCJkaWFsX2NvZGUiOiIrNjkxIiwiY29kZSI6IkZNIn0seyJuYW1lIjoiTW9sZG92YSwgUmVwdWJsaWMgb2YiLCJkaWFsX2NvZGUiOiIrMzczIiwiY29kZSI6Ik1EIn0seyJuYW1lIjoiTW9uYWNvIiwiZGlhbF9jb2RlIjoiKzM3NyIsImNvZGUiOiJNQyJ9LHsibmFtZSI6Ik1vbmdvbGlhIiwiZGlhbF9jb2RlIjoiKzk3NiIsImNvZGUiOiJNTiJ9LHsibmFtZSI6Ik1vbnRlbmVncm8iLCJkaWFsX2NvZGUiOiIrMzgyIiwiY29kZSI6Ik1FIn0seyJuYW1lIjoiTW9udHNlcnJhdCIsImRpYWxfY29kZSI6IisxNjY0IiwiY29kZSI6Ik1TIn0seyJuYW1lIjoiTW9yb2NjbyIsImRpYWxfY29kZSI6IisyMTIiLCJjb2RlIjoiTUEifSx7Im5hbWUiOiJNb3phbWJpcXVlIiwiZGlhbF9jb2RlIjoiKzI1OCIsImNvZGUiOiJNWiJ9LHsibmFtZSI6Ik15YW5tYXIiLCJkaWFsX2NvZGUiOiIrOTUiLCJjb2RlIjoiTU0ifSx7Im5hbWUiOiJOYW1pYmlhIiwiZGlhbF9jb2RlIjoiKzI2NCIsImNvZGUiOiJOQSJ9LHsibmFtZSI6Ik5hdXJ1IiwiZGlhbF9jb2RlIjoiKzY3NCIsImNvZGUiOiJOUiJ9LHsibmFtZSI6Ik5lcGFsIiwiZGlhbF9jb2RlIjoiKzk3NyIsImNvZGUiOiJOUCJ9LHsibmFtZSI6Ik5ldGhlcmxhbmRzIiwiZGlhbF9jb2RlIjoiKzMxIiwiY29kZSI6Ik5MIn0seyJuYW1lIjoiTmV0aGVybGFuZHMgQW50aWxsZXMiLCJkaWFsX2NvZGUiOiIrNTk5IiwiY29kZSI6IkFOIn0seyJuYW1lIjoiTmV3IENhbGVkb25pYSIsImRpYWxfY29kZSI6Iis2ODciLCJjb2RlIjoiTkMifSx7Im5hbWUiOiJOZXcgWmVhbGFuZCIsImRpYWxfY29kZSI6Iis2NCIsImNvZGUiOiJOWiJ9LHsibmFtZSI6Ik5pY2FyYWd1YSIsImRpYWxfY29kZSI6Iis1MDUiLCJjb2RlIjoiTkkifSx7Im5hbWUiOiJOaWdlciIsImRpYWxfY29kZSI6IisyMjciLCJjb2RlIjoiTkUifSx7Im5hbWUiOiJOaWdlcmlhIiwiZGlhbF9jb2RlIjoiKzIzNCIsImNvZGUiOiJORyJ9LHsibmFtZSI6Ik5pdWUiLCJkaWFsX2NvZGUiOiIrNjgzIiwiY29kZSI6Ik5VIn0seyJuYW1lIjoiTm9yZm9sayBJc2xhbmQiLCJkaWFsX2NvZGUiOiIrNjcyIiwiY29kZSI6Ik5GIn0seyJuYW1lIjoiTm9ydGhlcm4gTWFyaWFuYSBJc2xhbmRzIiwiZGlhbF9jb2RlIjoiKzEgNjcwIiwiY29kZSI6Ik1QIn0seyJuYW1lIjoiTm9yd2F5IiwiZGlhbF9jb2RlIjoiKzQ3IiwiY29kZSI6Ik5PIn0seyJuYW1lIjoiT21hbiIsImRpYWxfY29kZSI6Iis5NjgiLCJjb2RlIjoiT00ifSx7Im5hbWUiOiJQYWtpc3RhbiIsImRpYWxfY29kZSI6Iis5MiIsImNvZGUiOiJQSyJ9LHsibmFtZSI6IlBhbGF1IiwiZGlhbF9jb2RlIjoiKzY4MCIsImNvZGUiOiJQVyJ9LHsibmFtZSI6IlBhbGVzdGluaWFuIFRlcnJpdG9yeSwgT2NjdXBpZWQiLCJkaWFsX2NvZGUiOiIrOTcwIiwiY29kZSI6IlBTIn0seyJuYW1lIjoiUGFuYW1hIiwiZGlhbF9jb2RlIjoiKzUwNyIsImNvZGUiOiJQQSJ9LHsibmFtZSI6IlBhcHVhIE5ldyBHdWluZWEiLCJkaWFsX2NvZGUiOiIrNjc1IiwiY29kZSI6IlBHIn0seyJuYW1lIjoiUGFyYWd1YXkiLCJkaWFsX2NvZGUiOiIrNTk1IiwiY29kZSI6IlBZIn0seyJuYW1lIjoiUGVydSIsImRpYWxfY29kZSI6Iis1MSIsImNvZGUiOiJQRSJ9LHsibmFtZSI6IlBoaWxpcHBpbmVzIiwiZGlhbF9jb2RlIjoiKzYzIiwiY29kZSI6IlBIIn0seyJuYW1lIjoiUGl0Y2Fpcm4iLCJkaWFsX2NvZGUiOiIrODcyIiwiY29kZSI6IlBOIn0seyJuYW1lIjoiUG9sYW5kIiwiZGlhbF9jb2RlIjoiKzQ4IiwiY29kZSI6IlBMIn0seyJuYW1lIjoiUG9ydHVnYWwiLCJkaWFsX2NvZGUiOiIrMzUxIiwiY29kZSI6IlBUIn0seyJuYW1lIjoiUHVlcnRvIFJpY28iLCJkaWFsX2NvZGUiOiIrMSA5MzkiLCJjb2RlIjoiUFIifSx7Im5hbWUiOiJRYXRhciIsImRpYWxfY29kZSI6Iis5NzQiLCJjb2RlIjoiUUEifSx7Im5hbWUiOiJSb21hbmlhIiwiZGlhbF9jb2RlIjoiKzQwIiwiY29kZSI6IlJPIn0seyJuYW1lIjoiUnVzc2lhIiwiZGlhbF9jb2RlIjoiKzciLCJjb2RlIjoiUlUifSx7Im5hbWUiOiJSd2FuZGEiLCJkaWFsX2NvZGUiOiIrMjUwIiwiY29kZSI6IlJXIn0seyJuYW1lIjoiUsOpdW5pb24iLCJkaWFsX2NvZGUiOiIrMjYyIiwiY29kZSI6IlJFIn0seyJuYW1lIjoiU2FpbnQgQmFydGjDqWxlbXkiLCJkaWFsX2NvZGUiOiIrNTkwIiwiY29kZSI6IkJMIn0seyJuYW1lIjoiU2FpbnQgSGVsZW5hLCBBc2NlbnNpb24gYW5kIFRyaXN0YW4gRGEgQ3VuaGEiLCJkaWFsX2NvZGUiOiIrMjkwIiwiY29kZSI6IlNIIn0seyJuYW1lIjoiU2FpbnQgS2l0dHMgYW5kIE5ldmlzIiwiZGlhbF9jb2RlIjoiKzEgODY5IiwiY29kZSI6IktOIn0seyJuYW1lIjoiU2FpbnQgTHVjaWEiLCJkaWFsX2NvZGUiOiIrMSA3NTgiLCJjb2RlIjoiTEMifSx7Im5hbWUiOiJTYWludCBNYXJ0aW4iLCJkaWFsX2NvZGUiOiIrNTkwIiwiY29kZSI6Ik1GIn0seyJuYW1lIjoiU2FpbnQgUGllcnJlIGFuZCBNaXF1ZWxvbiIsImRpYWxfY29kZSI6Iis1MDgiLCJjb2RlIjoiUE0ifSx7Im5hbWUiOiJTYWludCBWaW5jZW50IGFuZCB0aGUgR3JlbmFkaW5lcyIsImRpYWxfY29kZSI6IisxIDc4NCIsImNvZGUiOiJWQyJ9LHsibmFtZSI6IlNhbW9hIiwiZGlhbF9jb2RlIjoiKzY4NSIsImNvZGUiOiJXUyJ9LHsibmFtZSI6IlNhbiBNYXJpbm8iLCJkaWFsX2NvZGUiOiIrMzc4IiwiY29kZSI6IlNNIn0seyJuYW1lIjoiU2FvIFRvbWUgYW5kIFByaW5jaXBlIiwiZGlhbF9jb2RlIjoiKzIzOSIsImNvZGUiOiJTVCJ9LHsibmFtZSI6IlNhdWRpIEFyYWJpYSIsImRpYWxfY29kZSI6Iis5NjYiLCJjb2RlIjoiU0EifSx7Im5hbWUiOiJTZW5lZ2FsIiwiZGlhbF9jb2RlIjoiKzIyMSIsImNvZGUiOiJTTiJ9LHsibmFtZSI6IlNlcmJpYSIsImRpYWxfY29kZSI6IiszODEiLCJjb2RlIjoiUlMifSx7Im5hbWUiOiJTZXljaGVsbGVzIiwiZGlhbF9jb2RlIjoiKzI0OCIsImNvZGUiOiJTQyJ9LHsibmFtZSI6IlNpZXJyYSBMZW9uZSIsImRpYWxfY29kZSI6IisyMzIiLCJjb2RlIjoiU0wifSx7Im5hbWUiOiJTaW5nYXBvcmUiLCJkaWFsX2NvZGUiOiIrNjUiLCJjb2RlIjoiU0cifSx7Im5hbWUiOiJTbG92YWtpYSIsImRpYWxfY29kZSI6Iis0MjEiLCJjb2RlIjoiU0sifSx7Im5hbWUiOiJTbG92ZW5pYSIsImRpYWxfY29kZSI6IiszODYiLCJjb2RlIjoiU0kifSx7Im5hbWUiOiJTb2xvbW9uIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrNjc3IiwiY29kZSI6IlNCIn0seyJuYW1lIjoiU29tYWxpYSIsImRpYWxfY29kZSI6IisyNTIiLCJjb2RlIjoiU08ifSx7Im5hbWUiOiJTb3V0aCBBZnJpY2EiLCJkaWFsX2NvZGUiOiIrMjciLCJjb2RlIjoiWkEifSx7Im5hbWUiOiJTb3V0aCBHZW9yZ2lhIGFuZCB0aGUgU291dGggU2FuZHdpY2ggSXNsYW5kcyIsImRpYWxfY29kZSI6Iis1MDAiLCJjb2RlIjoiR1MifSx7Im5hbWUiOiJTcGFpbiIsImRpYWxfY29kZSI6IiszNCIsImNvZGUiOiJFUyJ9LHsibmFtZSI6IlNyaSBMYW5rYSIsImRpYWxfY29kZSI6Iis5NCIsImNvZGUiOiJMSyJ9LHsibmFtZSI6IlN1ZGFuIiwiZGlhbF9jb2RlIjoiKzI0OSIsImNvZGUiOiJTRCJ9LHsibmFtZSI6IlN1cmluYW1lIiwiZGlhbF9jb2RlIjoiKzU5NyIsImNvZGUiOiJTUiJ9LHsibmFtZSI6IlN2YWxiYXJkIGFuZCBKYW4gTWF5ZW4iLCJkaWFsX2NvZGUiOiIrNDciLCJjb2RlIjoiU0oifSx7Im5hbWUiOiJTd2F6aWxhbmQiLCJkaWFsX2NvZGUiOiIrMjY4IiwiY29kZSI6IlNaIn0seyJuYW1lIjoiU3dlZGVuIiwiZGlhbF9jb2RlIjoiKzQ2IiwiY29kZSI6IlNFIn0seyJuYW1lIjoiU3dpdHplcmxhbmQiLCJkaWFsX2NvZGUiOiIrNDEiLCJjb2RlIjoiQ0gifSx7Im5hbWUiOiJTeXJpYW4gQXJhYiBSZXB1YmxpYyIsImRpYWxfY29kZSI6Iis5NjMiLCJjb2RlIjoiU1kifSx7Im5hbWUiOiJUYWl3YW4iLCJkaWFsX2NvZGUiOiIrODg2IiwiY29kZSI6IlRXIn0seyJuYW1lIjoiVGFqaWtpc3RhbiIsImRpYWxfY29kZSI6Iis5OTIiLCJjb2RlIjoiVEoifSx7Im5hbWUiOiJUYW56YW5pYSwgVW5pdGVkIFJlcHVibGljIG9mIiwiZGlhbF9jb2RlIjoiKzI1NSIsImNvZGUiOiJUWiJ9LHsibmFtZSI6IlRoYWlsYW5kIiwiZGlhbF9jb2RlIjoiKzY2IiwiY29kZSI6IlRIIn0seyJuYW1lIjoiVGltb3ItTGVzdGUiLCJkaWFsX2NvZGUiOiIrNjcwIiwiY29kZSI6IlRMIn0seyJuYW1lIjoiVG9nbyIsImRpYWxfY29kZSI6IisyMjgiLCJjb2RlIjoiVEcifSx7Im5hbWUiOiJUb2tlbGF1IiwiZGlhbF9jb2RlIjoiKzY5MCIsImNvZGUiOiJUSyJ9LHsibmFtZSI6IlRvbmdhIiwiZGlhbF9jb2RlIjoiKzY3NiIsImNvZGUiOiJUTyJ9LHsibmFtZSI6IlRyaW5pZGFkIGFuZCBUb2JhZ28iLCJkaWFsX2NvZGUiOiIrMSA4NjgiLCJjb2RlIjoiVFQifSx7Im5hbWUiOiJUdW5pc2lhIiwiZGlhbF9jb2RlIjoiKzIxNiIsImNvZGUiOiJUTiJ9LHsibmFtZSI6IlR1cmtleSIsImRpYWxfY29kZSI6Iis5MCIsImNvZGUiOiJUUiJ9LHsibmFtZSI6IlR1cmttZW5pc3RhbiIsImRpYWxfY29kZSI6Iis5OTMiLCJjb2RlIjoiVE0ifSx7Im5hbWUiOiJUdXJrcyBhbmQgQ2FpY29zIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrMSA2NDkiLCJjb2RlIjoiVEMifSx7Im5hbWUiOiJUdXZhbHUiLCJkaWFsX2NvZGUiOiIrNjg4IiwiY29kZSI6IlRWIn0seyJuYW1lIjoiVWdhbmRhIiwiZGlhbF9jb2RlIjoiKzI1NiIsImNvZGUiOiJVRyJ9LHsibmFtZSI6IlVrcmFpbmUiLCJkaWFsX2NvZGUiOiIrMzgwIiwiY29kZSI6IlVBIn0seyJuYW1lIjoiVW5pdGVkIEFyYWIgRW1pcmF0ZXMiLCJkaWFsX2NvZGUiOiIrOTcxIiwiY29kZSI6IkFFIn0seyJuYW1lIjoiVW5pdGVkIEtpbmdkb20iLCJkaWFsX2NvZGUiOiIrNDQiLCJjb2RlIjoiR0IifSx7Im5hbWUiOiJVbml0ZWQgU3RhdGVzIiwiZGlhbF9jb2RlIjoiKzEiLCJjb2RlIjoiVVMifSx7Im5hbWUiOiJVcnVndWF5IiwiZGlhbF9jb2RlIjoiKzU5OCIsImNvZGUiOiJVWSJ9LHsibmFtZSI6IlV6YmVraXN0YW4iLCJkaWFsX2NvZGUiOiIrOTk4IiwiY29kZSI6IlVaIn0seyJuYW1lIjoiVmFudWF0dSIsImRpYWxfY29kZSI6Iis2NzgiLCJjb2RlIjoiVlUifSx7Im5hbWUiOiJWZW5lenVlbGEsIEJvbGl2YXJpYW4gUmVwdWJsaWMgb2YiLCJkaWFsX2NvZGUiOiIrNTgiLCJjb2RlIjoiVkUifSx7Im5hbWUiOiJWaWV0IE5hbSIsImRpYWxfY29kZSI6Iis4NCIsImNvZGUiOiJWTiJ9LHsibmFtZSI6IlZpcmdpbiBJc2xhbmRzLCBCcml0aXNoIiwiZGlhbF9jb2RlIjoiKzEgMjg0IiwiY29kZSI6IlZHIn0seyJuYW1lIjoiVmlyZ2luIElzbGFuZHMsIFUuUy4iLCJkaWFsX2NvZGUiOiIrMSAzNDAiLCJjb2RlIjoiVkkifSx7Im5hbWUiOiJXYWxsaXMgYW5kIEZ1dHVuYSIsImRpYWxfY29kZSI6Iis2ODEiLCJjb2RlIjoiV0YifSx7Im5hbWUiOiJZZW1lbiIsImRpYWxfY29kZSI6Iis5NjciLCJjb2RlIjoiWUUifSx7Im5hbWUiOiJaYW1iaWEiLCJkaWFsX2NvZGUiOiIrMjYwIiwiY29kZSI6IlpNIn0seyJuYW1lIjoiWmltYmFid2UiLCJkaWFsX2NvZGUiOiIrMjYzIiwiY29kZSI6IlpXIn0seyJuYW1lIjoiw4VsYW5kIElzbGFuZHMiLCJkaWFsX2NvZGUiOiIrMzU4IiwiY29kZSI6IkFYIn1d";
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }
    private void setCountryCode() {
//        ArrayList<Country> toReturn = new ArrayList<>();
//        toReturn.add(new Country("RU", "Russia", "+7"));
//        toReturn.add(new Country("TJ", "Tajikistan", "+992"));
//        toReturn.add(new Country("US", "United States", "+1"));
//        return toReturn;
        Spinner spinner = (Spinner) this.findViewById(R.id.spinCountry);

        List<String> countryList = new ArrayList<>();

        try {
            JSONArray countryArray = new JSONArray(readCountryCodeWithJsonString());
            //toReturn = new ArrayList<>();
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject jsonObject = countryArray.getJSONObject(i);
                String countryName = jsonObject.getString("name");
                String countryDialCode = jsonObject.getString("dial_code");
                String countryCode = jsonObject.getString("code");
                countryList.add(countryName+"("+countryDialCode+")");
            }
//            Collections.sort(toReturn, new Comparator<Country>() {
//                @Override
//                public int compare(Country lhs, Country rhs) {
//                    return lhs.getName().compareTo(rhs.getName());
//                }
//            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_spinner_item,countryList);
        spinner.setAdapter(adapter);
        spinner.setSelection(113);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                String countrycode=parent.getItemAtPosition(pos).toString();
                int start=countrycode.lastIndexOf("(");
                int end=countrycode.length();
                selectedCountryCode=countrycode.substring(start,end);
                //Toast.makeText(getBaseContext(), , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}