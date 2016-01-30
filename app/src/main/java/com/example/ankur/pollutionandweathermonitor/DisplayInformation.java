package com.example.ankur.pollutionandweathermonitor;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class DisplayInformation extends AppCompatActivity   implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    /*loginButton is to facilitate facebook logout */
    private LoginButton loginButton;
    /*callbackManager is for the callback functions for facebook logout*/
    private CallbackManager callbackManager;
    private TextView textView;
    private TextView locationView;
    private String firstName;
    /* locationPermissionStatus is to store the permission status of the ACCESS_FINE_LOCATION */
    private String locationPermissionStatus;
    private String LOGTAG = DisplayInformation.class.getSimpleName();
    private AccessTokenTracker accessTokenTracker;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location mLastLocation;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_display_information);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

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
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    Log.v(LOGTAG, "Logged out: Redirecting to facebook");
                    RedirectToMainActivity();
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView5) ;
        locationView = (TextView) findViewById(R.id.location);
        //Get the firstname of the user  logged in through facebook
        //GetFacebookFirstName();
        Intent intent = getIntent();
        //Get the permission status of the ACCESS_FINE_LOCATION
        locationPermissionStatus = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LOCATION_PERMISSION);
        firstName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_FIRSTNAME);
        //Welcome message
        //textView.setTextSize(20);
        textView.setText("Welcome " + firstName + "!");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //The following is the Search Utility for Location Search
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v(LOGTAG, "Places: " + place.getName() + " " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.v(LOGTAG, "An error occurred: " + status);
            }
        });
    }

    private void RedirectToMainActivity() {
        Log.v(LOGTAG, "RedirectToMainActivity called");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();

        //locationView.setText("This is a placeholder for the user's last known location");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(locationPermissionStatus.equals("false")){
            locationView.setText("You have disabled location Services, cant access your location");
            return;
        }
        Log.v(LOGTAG, "Inside onConnectedz: " + String.valueOf(googleApiClient.isConnected()));
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);

            if (mLastLocation != null) {
                Log.v(LOGTAG, String.valueOf(mLastLocation.getLatitude()));
                Log.v(LOGTAG, String.valueOf(mLastLocation.getLongitude()));
                locationView.setText("Your Current location is: " + mLastLocation.toString());
                latitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
                longitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
                loadMap();

            }
            else {
                   //"adb emu geo fix 30.219470 -97.745361" use this command to put a temporary location in the emulator
                    Log.v(LOGTAG, "No last known location, location service will be called");
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }

        }catch (SecurityException e){
            e.printStackTrace();
            Log.v(LOGTAG, "Error in onConnected: " + e.toString());
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(LOGTAG, "onLocationChanged called");
        locationView.setText("Your Current location is: "+ mLastLocation.toString());
        latitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
        longitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
        loadMap();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(LOGTAG, "called onMapReady." );
        mMap = googleMap;
        Log.v(LOGTAG, "Longitude is: " + String.valueOf(longitude) );
        Log.v(LOGTAG, "Latitude  is: " + String.valueOf(latitude) );
        // Add a marker in the current location and move the camera
        LatLng current = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current).title("Marker in the Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 500, null);

    }
    public void loadMap(){
        Log.v(LOGTAG, "called loadMap." );

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}
