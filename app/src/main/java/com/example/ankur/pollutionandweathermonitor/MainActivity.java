package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
import android.os.Bundle;
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
    private String firstname;
    private Profile profile;
    AccessToken accessToken;
    private String LOGTAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the facebook SDK for login
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        //Check if there is an active facebook session
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
                    firstname = profile.getFirstName();
                    Log.v(LOGTAG, "There is an active session. The logged in user is  " + firstname);
                    RedirectToDisplayInformation();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        else {
            Log.v(LOGTAG, "No Active sessions found");
        }

        //create the facebook login Button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        try{
            loginButton.setReadPermissions("user_friends");

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Toast.makeText(getApplicationContext(), "Fb Login Success", Toast.LENGTH_LONG);
                    Log.v(LOGTAG, "successfully connected to facebook");
                    //On Successful login, call the function RedirectToDisplayInformation
                    RedirectToDisplayInformation();
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(getApplicationContext(), "Fb on cancel", Toast.LENGTH_LONG);
                    Log.v(LOGTAG, " connection to facebook cancelled");

                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
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
    //After the successful login, redirect to the DisplayInformation Activity
    //Currently there is nothing send in the intent
    //TODO: Get the username or first name of the user from the facebook Login and send that to the Activity
    protected void RedirectToDisplayInformation(){
        Log.v(LOGTAG, "successfully called the function RedirectToDisplayInformation");
        Intent intent = new Intent(this, DisplayInformation.class);
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
}
