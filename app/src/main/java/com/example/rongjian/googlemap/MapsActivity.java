/**
 *Licensed under the Creative Commons Attribution 3.0 License, and code samples are licensed under the Apache 2.0 License.
 *For details, see our Site Policies. Java is a registered trademark of Oracle and/or its affiliates.
 *Google official API
 */

package com.example.rongjian.googlemap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rongjian.googlemap.models.DIYplaces;
import com.example.rongjian.googlemap.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnPoiClickListener {

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    //    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(34.4135054, -119.8895361), new LatLng(34.4419993, -119.8186309));

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, textReg;
    private TextView tvDistanceDuration;
    private PlaceAutoCompleteAdapter mplaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private Marker dMarker1; private Marker poiMarker;
    private Marker pMarker;  private Marker tMarker;
    private Marker lMarker;
    private Button textclear;

    private Polyline newpolyline;

    private SharedPreferences SearchTxtPrefer;
    private SharedPreferences.Editor Peditor;
    public String mtext = null;

    ArrayList<LatLng> listPoints;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
        Log.d(TAG, "mtext" + mtext);

        if (mtext != null) {
            moveCamera(new LatLng(34.4157673, -119.8418880), 15, mtext);
            Log.d(TAG, "TextReg Read: " + mtext);
        }else{
            getDeviceLocation();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (newpolyline != null && dMarker1 != null) {
                    newpolyline.remove();
                    dMarker1.remove();
                    tvDistanceDuration.setText("");
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {


                if (listPoints.size() == 1){
                    listPoints.clear();
                    if (dMarker1!=null) {
                        dMarker1.remove();
                    }
                }

                listPoints.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    dMarker1 = mMap.addMarker(markerOptions);

                }

                //TODO: request get direction code
                if (listPoints.size() == 1){
                    //create the URL
                    try{
                        if(mLocationPermissionsGranted){

                            final Task location = mFusedLocationProviderClient.getLastLocation();
                            location.addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful() && task.getResult() != null){
                                        Log.d(TAG, "onComplete: found location!");
                                        Location currentLocation = (Location) task.getResult();
                                        String url = getRequestUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), listPoints.get(0));
                                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                        taskRequestDirections.execute(url);
                                    }else{
                                        Log.d(TAG, "onComplete: current location is null");
                                        Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }catch (SecurityException e){
                        Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
                    }

                }

            }
        });
        mMap.setOnPoiClickListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mtext = SearchTxtPrefer.getString("searchtxt", null);

        Log.d(TAG, "enter onResume" + mtext);
        if (mtext != null) {
            geoLocate(mtext);
            Log.d(TAG, "TextReg Read: " + mtext);
        }

        Peditor.putString("searchtxt", null);
        Peditor.commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.ic_info);
        textReg = findViewById(R.id.ic_textreg);
        textclear = findViewById(R.id.ic_clear);
        tvDistanceDuration = findViewById(R.id.tv_distance_time);

        SearchTxtPrefer = PreferenceManager.getDefaultSharedPreferences(this);
        Peditor = SearchTxtPrefer.edit();

        textReg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), TextReg.class);
                startActivity(startIntent);
            }
        });

        getLocationPermission();

        listPoints = new ArrayList<>();

    }

    private void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||event.getAction() == event.ACTION_DOWN
                        ||event.getAction() == event.KEYCODE_ENTER
                        ||actionId == EditorInfo.IME_ACTION_SEND){
                    geoLocate(null);
                    geoLocate(mSearchText.getText().toString());
                }
                return false;
            }
        });


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        textclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mplaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mplaceAutoCompleteAdapter);

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }
            }
        });

            hideSoftKeyboard();
    }


    private void geoLocate(String a){
        if (a == null){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString, 1);
            Log.d(TAG, "geoLocate: geolocating from googlemap" + list);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException" + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    15,
                    address.getAddressLine(0));

            mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));

            hideSoftKeyboard();
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

        }
        }
        else{
            Log.d(TAG, "enter searching");
            int list1;
            DIYplaces diy = new DIYplaces();
            Log.d(TAG, "value of input a: " + a);
            list1 = diy.getList(a);

            Log.d(TAG, "list1 value: " + list1);
            if (list1 != 0){

                moveCamera( new LatLng(diy.getLat(list1),diy.getLng(list1)), 15, a);
                mSearchText.dismissDropDown();

                Log.d(TAG, "find find Larry " + a);
                Log.d(TAG, "find Mackbook Pro: ");
            }
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    15,
                                    "Current Location");

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.412560, -119.842233))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("MSI Analytical Lab"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.413832, -119.847530))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Orfalea Center for Global and International Studies"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.421968, -119.853461))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Public Safety"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.412546, -119.850868))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Studio"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.414102, -119.843942 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Institute for Terahertz Science and Technology"));


                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.411750, -119.843239 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Biological Sciences Administration"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.420277, -119.852416 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Facilities Management"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.419734, -119.854578 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Harder South"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.423130, -119.856859 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("Mail Services"));

                            mMap.addMarker(new MarkerOptions().position(new LatLng(34.414172, -119.844450 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
                                    .title("El Centro"));

                            pMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(34.423107, -119.856354 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parkinglot))
                                    .title("Parking Lot 37"));

                            pMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(34.413505, -119.852605 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parkinglot))
                                    .title("Parking Lot 22"));

                            pMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(34.417891, -119.846858 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parkinglot))
                                    .title("Parking Lot 16"));

                            pMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(34.417529, -119.847675 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parkinglot))
                                    .title("Parking Lot 18"));

                            pMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(34.414722, -119.851313 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parkinglot))
                                    .title("Parking Lot 27"));

                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.414213, -119.843936 )) //
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Physics Trailer 2"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.414079, -119.843939 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Physics Trailer 1"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.413732, -119.842408 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 698"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.413722, -119.842530 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 384"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.413712, -119.842653 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 699"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.413716, -119.842735 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 697"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.413725, -119.842853 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 380"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.416178, -119.843536 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 936"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.416070, -119.843526 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 935"));
                            tMarker =  mMap.addMarker(new MarkerOptions().position(new LatLng(34.415982, -119.843526 ))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trailer))
                                    .title("Trailer 232"));

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (mMarker != null) {
            mMarker.remove();
        }
        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress();

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);

            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }
        }else{
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (mMarker != null) {
            mMarker.remove();
        }
        if (!title.equals("Current Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMarker = mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }



    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        Log.d(TAG, "hidekeyboard");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

       /*
        --------------------------- google places API autocomplete and move camera -----------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mplaceAutoCompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), 15, mPlace);

            places.release();
        }
    };

    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + ","+origin.longitude;
        String str_dest = "destination=" + dest.latitude + ","+dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String duration = "arrival_time";
        String param = str_org + "&" + str_dest + "&" +sensor+"&" +mode + "&" + duration;
        String output = "json";
        String url = "http://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return  responseString;
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        if (poiMarker != null){
            poiMarker.remove();
        }
        poiMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(pointOfInterest.latLng.latitude, pointOfInterest.latLng.longitude))
        .title(pointOfInterest.name));
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e){
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser directionsParser = new DirectionsJSONParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points;
            PolylineOptions polylineOptions = null;

            String distance = "";
            String duration = "";

            for (int i = 0; i < lists.size(); i++){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++){
                    HashMap<String, String> point = path.get(j);
                    if(j == 0){
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1){
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(8);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

            if (polylineOptions != null){
                if (newpolyline != null){
                    newpolyline.remove();
                }
                newpolyline = mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "direction not found", Toast.LENGTH_SHORT).show();
            }
        }
    }


}











