package com.example.galib.lifeblood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class UserListFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "UserListFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    //widgets
    private RecyclerView donorListRecyclerView;
    private MapView mMapView;

    private Toolbar donorListToolbar;

    //vars
    private ArrayList<Users> donorList = new ArrayList<>();
    private ArrayList<UserLocation> donorLocations = new ArrayList<>();
    private ArrayList<UserLocation> deleteCurrentUser = new ArrayList<>();
    private DonorListAdapter donorListRecyclerAdapter;
    private GoogleMap googleMap;
    private LatLngBounds mapBoundary;
    private UserLocation currentUserPosition;

    private FirebaseFirestore firebaseFirestore;



    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        firebaseFirestore = firebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        if(donorLocations.size()==0) {
            if (getArguments() != null) {
                final ArrayList<Users> users = getArguments().getParcelableArrayList("user_list");
                donorList.addAll(users);

                final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList("user_locations");
                donorLocations.addAll(locations);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        donorListToolbar =  (Toolbar) view.findViewById(R.id.donor_list_toolbar);
        donorListToolbar.setTitle("<-- Nearer Donors List");

        donorListToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(regIntent);
            }
        });

        donorListRecyclerView = view.findViewById(R.id.user_list_recycler_view);
        mMapView = view.findViewById(R.id.user_list_map);

        if (donorLocations!=null){

            setUserPosition();
        }

        donorLocations.removeAll(deleteCurrentUser);

        initUserListRecyclerView();
        initGoogleMap(savedInstanceState);
        return view;
    }

    private void setCameraView(){

        double bottomBoundary = currentUserPosition.getGeo_point().getLatitude() - 0.1;
        double leftBoundary = currentUserPosition.getGeo_point().getLongitude() - 0.1;
        double topBoundary = currentUserPosition.getGeo_point().getLatitude() + 0.1;
        double rightBoundary = currentUserPosition.getGeo_point().getLongitude() + 0.1;

        mapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0));

    }


    private void setUserPosition(){

        for(UserLocation userLocation  : donorLocations){
            if(userLocation.getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                currentUserPosition = userLocation;
                deleteCurrentUser.add(userLocation);
                Log.d(TAG, "current user id map: " + userLocation.getUser_id());
            }
        }

    }

    private void initGoogleMap(Bundle savedInstanceState){

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void initUserListRecyclerView() {
        donorListRecyclerAdapter = new DonorListAdapter(donorLocations);
        donorListRecyclerView.setAdapter(donorListRecyclerAdapter);
        donorListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        googleMap = map;
        if(donorLocations != null) {
            setCameraView();
            for (UserLocation userLocation : donorLocations) {
                map.addMarker(new MarkerOptions().position(new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude())).title(userLocation.getUser().getName()));
            }
        }

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}

