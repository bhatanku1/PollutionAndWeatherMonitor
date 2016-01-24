package com.example.ankur.pollutionandweathermonitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    //LoginButton and callbackManager are for facebook login
    private LoginButton loginButton;
    CallbackManager callbackManager;
    /*firstName is to store the firstname of the logged in facebook user*/
    private String firstName;
    /*profile and accessToken are for to get the current session tokens of the logged in users*/
    private Profile profile;
    private AccessToken accessToken;
    /*permissionStatus is to store the ACCESS_FINE_LOCATION permission of the user*/
    private boolean permissionStatus;
    /*EXTRA_MESSAGE_FIRSTNAME is to send the firstname of the logged in facebook user in the intent*/
    public final static String EXTRA_MESSAGE_FIRSTNAME = "USERNAME";
    /*EXTRA_MESSAGE_LOCATION_PERMISSION is to send the ACCESS_FINE_LOCATION permission value in the intent*/
    public final static String EXTRA_MESSAGE_LOCATION_PERMISSION = "PERMISSION";
    /*Logs generated in this class are accessed through the Tag LOGTAG*/
    private String LOGTAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the facebook SDK for login
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        //Call the CheckFacebookToken() function to see if there is a logged in user already.
        //The function returns the firstname if there is a logged in user, else returns null
        firstName = CheckFacebookToken();
        if(firstName != null) {
            //If there is an active current Session, then check if the user has granted the
            // ACCESS_FINE_LOCATION permission. This check is only for Android >= 6.0
            //For Android <6, the permission can be put in the AndroidMenifestFile
            CheckLocationPermission();
        }
        //create the facebook login Button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        try{
            loginButton.setReadPermissions("user_friends");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v(LOGTAG, "successfully connected to facebook");
                    //After the login is successful, get the firstname of the logged in user
                    firstName = CheckFacebookToken();
                    //If there is an active current Session, then check if the user has granted the
                    // ACCESS_FINE_LOCATION permission. This check is only for Android >= 6.0
                    //For Android <6, the permission can be put in the AndroidMenifestFile
                    CheckLocationPermission();
                }

                @Override
                public void onCancel() {
                    Log.v(LOGTAG, " connection to facebook cancelled");
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), "Fb Login Error", Toast.LENGTH_LONG);
                    Log.v(LOGTAG, "Error on  connection to facebook");
                }
            });
        }catch (Exception e){
            Log.v(LOGTAG, "Error in the loginButton facebook");
            e.printStackTrace();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    //check if the user has granted the ACCESS_FINE_LOCATION permission.
    // This check is only for Android >= 6.0. For Android <6, the permission can be put in the AndroidMenifestFile
    protected void CheckLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        else{
            //Redirects the user to the Activity DisplayInformation
            RedirectToDisplayInformation();
        }

    }
    //The function CheckFacebookToken returns the firstname of the logged in facebook user, else returns null
    protected String CheckFacebookToken(){
        //Check if there is an active facebook session
        String fName = null;
        try{
            accessToken = AccessToken.getCurrentAccessToken();
            Log.v(LOGTAG, accessToken.getToken());

        } catch (Exception e){
            e.printStackTrace();
        }
        //If there is an Active facebook session, get the firstname and redirect user to the
        //DisplayInformation Activity, send the username through the intent
        if(accessToken != null) {
            try {
                profile = Profile.getCurrentProfile();
                if(profile != null) {
                    fName = profile.getFirstName();
                    Log.v(LOGTAG, "There is an active session. The logged in user is  " + fName);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            Log.v(LOGTAG, "No Active sessions found");
        }
        return fName;
    }
    //The function RedirectToDisplayInformation redirects the user to the Activity DisplayInformation
    //The intent also sends the firstname of the logged in facebook user
    protected void RedirectToDisplayInformation(){
        Log.v(LOGTAG, "successfully called the function RedirectToDisplayInformation");
        Intent intent = new Intent(this, DisplayInformation.class);
        intent.putExtra(EXTRA_MESSAGE_LOCATION_PERMISSION, String.valueOf(permissionStatus));
        intent.putExtra(EXTRA_MESSAGE_FIRSTNAME,firstName );
        startActivity(intent);
    }

        //This function is to call the callback functions of facebook after the login attempt
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //The function onRequestPermissionResult is a callback to promt the user to either allow or deny the
    //permissions. This function is significant only for Android >6
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        Log.v(LOGTAG, "onRequestPermissionResult");

        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus = true;
                    Log.v(LOGTAG, "Permission Granted. Status is: " + String.valueOf(permissionStatus));
                }
                else {
                    permissionStatus = false;
                    Log.v(LOGTAG, "Permission Denied. Status is: " + String.valueOf(permissionStatus));

                }
                RedirectToDisplayInformation();
                return;
            }
        }
    }
}
