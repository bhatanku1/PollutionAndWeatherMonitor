package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class DisplayInformation extends AppCompatActivity {
    /*loginButton is to facilitate facebook logout */
    private LoginButton loginButton;
    /*callbackManager is for the callback functions for facebook logout*/
    private CallbackManager callbackManager;
    private TextView textView;
    private String firstName;
    /* locationPermissionStatus is to store the permission status of the ACCESS_FINE_LOCATION */
    private String locationPermissionStatus;
    private String LOGTAG = DisplayInformation.class.getSimpleName();
    /*profile object contains facebook details like the firstname of the logged in facebook user */
    private Profile profile;
    private ProfileTracker mProfileTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_display_information);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        try{
            loginButton.setReadPermissions("user_friends");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v(LOGTAG, "successfully connected to facebook");
                }
                @Override
                public void onCancel() {
                    Log.v(LOGTAG, " connection to facebook cancelled");
                }
                @Override
                public void onError(FacebookException exception) {
                    Log.v(LOGTAG, "Error on  connection to facebook");
                }
            });
        }catch (Exception e){
            Log.v(LOGTAG, "Error in the loginButton facebook");
            e.printStackTrace();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView5) ;



        //Get the firstname of the user  logged in through facebook
        //GetFacebookFirstName();
        Intent intent = getIntent();
        //FacebookLogin facebookLogin = new FacebookLogin();
        //facebookLogin.execute();
        //Get the permission status of the ACCESS_FINE_LOCATION
        locationPermissionStatus = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LOCATION_PERMISSION);
        firstName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_FIRSTNAME);
        //Welcome message
        //textView.setTextSize(20);
        textView.setText("Welcome " + firstName + "!" + " Your current location is 20 15 and the permission for Location is " + locationPermissionStatus);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.v(LOGTAG, "onStart called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private class FacebookLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {


                profile = Profile.getCurrentProfile();

                if (profile != null) {
                    firstName = profile.getFirstName();
                    Log.v(LOGTAG, "There is an active session. The logged in user is  " + firstName);
                    Log.v(LOGTAG, "Profile is " + profile.toString());
                }
                else{
                    Log.v(LOGTAG, "Profile is null");
                     mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            mProfileTracker.stopTracking();
                            Log.v(LOGTAG, "New profile is: " + currentProfile.getFirstName());
                            firstName = currentProfile.getFirstName();
                        }
                    };
                    mProfileTracker.startTracking();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return firstName;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v(LOGTAG, "Firstname is: " + result);





        }
    }
}
