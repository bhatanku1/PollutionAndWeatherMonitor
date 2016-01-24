package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class DisplayInformation extends AppCompatActivity {
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView textView;
    private String firstName;
    private String locationPermissionStation;
    private String LOGTAG = DisplayInformation.class.getSimpleName();
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

        textView = (TextView) findViewById(R.id.firstname);
        //Get the firstname of the user  logged in through facebook
        Intent intent = getIntent();
        firstName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_FIRSTNAME);
        locationPermissionStation = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LOCATION_PERMISSION);
        //Welcome message
        textView.setTextSize(20);
        textView.setText("Welcome " + firstName + "!" + " Your current location is 20 15.");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
