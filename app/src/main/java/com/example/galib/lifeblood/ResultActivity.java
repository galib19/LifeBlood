package com.example.galib.lifeblood;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView donorList;
    private static final String TAG = "ResultActivity";

    private FirebaseFirestore firebaseFirestore;

    private MapView mMapView;
    private Toolbar donorListToolbar;

    private List<Users> userList;
    private ArrayList<UserLocation> donorLocations = new ArrayList<>();
    private DonorListAdapter donorListAdapter;

    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private UserLocation mUserPosition;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        donorListToolbar = (Toolbar) findViewById(R.id.donor_list_toolbar);
        setSupportActionBar(donorListToolbar);
        getSupportActionBar().setTitle("Nearer Donors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userList = new ArrayList<>();
        donorListAdapter = new DonorListAdapter(donorLocations);

        donorList = (RecyclerView) findViewById(R.id.donor_list);
        donorList.setHasFixedSize(true);
        donorList.setLayoutManager(new LinearLayoutManager(this));
        donorList.setAdapter(donorListAdapter);



        donorLocations = getIntent().getExtras().getParcelableArrayList("userLocations");




        //Log.e(TAG, "saua"+ donorLocations);

        for(UserLocation userLocation  : donorLocations){

            Log.e(TAG, "userLocationFromResult" + userLocation);
        }

//

        userListQuery();

        setUserPosition();

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.donor_map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);



    }

    private void userListQuery(){
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    Toast.makeText(ResultActivity.this, "Error : " + e, Toast.LENGTH_LONG).show();
                }

                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){

                        Users users = doc.getDocument().toObject(Users.class).withId(doc.getDocument().getId());
                        userList.add(users);
                        //getUserLocation(users);

                        donorListAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }


    private void getUserLocation(Users users){
        DocumentReference locationRef = firebaseFirestore
                .collection("User Locations")
                .document(users.userId);
        locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().toObject(UserLocation.class)!=null){
                        donorLocations.add(task.getResult().toObject(UserLocation.class));
                        Log.d(TAG, "userLocation:" + task.getResult().toObject(UserLocation.class));
                    }
                }
            }
        });
    }

    private void addMapMarkers(){}

    private void setCameraView(){

        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - 0.1;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - 0.1;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + 0.1;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() + 0.1;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }

    private void setUserPosition(){
//        for(UserLocation userLocation:donorLocations){
//            Log.e(TAG, "userLocationset:" + userLocation.getUser().userId);
//            Log.e(TAG, "authID" + FirebaseAuth.getInstance().getUid());
//            if (userLocation.getUser().userId.equals(FirebaseAuth.getInstance().getUid())) {
//                mUserPosition = userLocation;
//            }
//        }
        GeoPoint geoPoint = new GeoPoint(23.729098, 90.398321);
        mUserPosition = new UserLocation();
        mUserPosition.setGeo_point(geoPoint);
       // setCameraView();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(23.729098, 90.398321)).title("My Position"));
        map.addMarker(new MarkerOptions().position(new LatLng(23.729098, 90.394321)).title("Asad Galib"));
        map.addMarker(new MarkerOptions().position(new LatLng(23.723098, 90.399321)).title("Babul Mia"));
        map.addMarker(new MarkerOptions().position(new LatLng(23.720098, 90.398421)).title("Khaled Mosharraf"));
        map.addMarker(new MarkerOptions().position(new LatLng(23.729098, 90.391121)).title("Daniel D Costa"));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        mGoogleMap = map;


    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



}

