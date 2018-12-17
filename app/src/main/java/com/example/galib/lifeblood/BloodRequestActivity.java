package com.example.galib.lifeblood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BloodRequestActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private Toolbar bloodRequestToolbar;

    private EditText hospitalName;
    private EditText requestDetails;
    private Spinner requestBloodGroup;
    private Button bloodRequestPostBtn;
    private Button bloodRequestCancelBtn;
    private ProgressBar bloodRequestProgress;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;
    private UserLocation currentUserPosition;
    private ArrayList<UserLocation> donorLocations = new ArrayList<>();
    private ArrayList<Users> userList = new ArrayList<>();

    private static final String TAG = "BloodRequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);

        bloodRequestToolbar = (Toolbar) findViewById(R.id.blood_request_toolbar);
        setSupportActionBar(bloodRequestToolbar);
        getSupportActionBar().setTitle("Add Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        hospitalName = findViewById(R.id.hospital_name);
        requestDetails = findViewById(R.id.blood_request_details);
        bloodRequestPostBtn = findViewById(R.id.blood_request_post_btn);
        bloodRequestCancelBtn = findViewById(R.id.blood_request_cancel_btn);
        bloodRequestProgress = findViewById(R.id.blood_request_progress);


        requestBloodGroup = (Spinner) findViewById(R.id.request_bloodGroup);
        requestBloodGroup.setOnItemSelectedListener(this);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        requestBloodGroup.setAdapter(adapter);
        requestBloodGroup.setPrompt("Blood Group");



        bloodRequestCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(BloodRequestActivity.this, "Request Cancelled", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(BloodRequestActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        bloodRequestPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hospital_name = hospitalName.getText().toString();
                String request_details = requestDetails.getText().toString();
                String request_blood_group = requestBloodGroup.getSelectedItem().toString();


                for(UserLocation userLocation  : donorLocations){
                    if(userLocation.getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                        currentUserPosition = userLocation;
                        // Log.d(TAG, "current user id map: " + userLocation.getUser_id());
                    }
                }

                donorLocations = sortLocations(donorLocations,currentUserPosition.getGeo_point().getLatitude(),currentUserPosition.getGeo_point().getLongitude() );

                if(!TextUtils.isEmpty(hospital_name) && !TextUtils.isEmpty(request_details) && !TextUtils.isEmpty(request_blood_group)){

                    bloodRequestProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("hospital_name", hospital_name);
                    requestMap.put("request_details", request_details);
                    requestMap.put("request_blood_group", request_blood_group);
                    requestMap.put("user_id", current_user_id);
                    requestMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("BloodRequests").add(requestMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()) {

                                inflateUserListFragment();
                                //bloodRequestProgress.setVisibility(View.INVISIBLE);

                            } else {

                            }
                            bloodRequestProgress.setVisibility(View.VISIBLE);
                            //bloodRequestProgress.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    Toast.makeText(BloodRequestActivity.this, "Fill up all the fields", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    private void inflateUserListFragment(){
        hideSoftKeyboard();

        UserListFragment fragment = UserListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("user_list", userList);
        bundle.putParcelableArrayList("user_locations", donorLocations);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, "User List");
        transaction.addToBackStack("User List");
        transaction.commit();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void queryFirebase(String selected_blood_group){


        userList.clear();
        donorLocations.clear();


        firebaseFirestore = FirebaseFirestore.getInstance();


        DocumentReference docRef = firebaseFirestore.collection("Users").document(current_user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Users users = document.toObject(Users.class).withId(document.getId());
                        Log.d(TAG, "document: " + users.getName());
                        getUserLocation(users);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        firebaseFirestore.collection("Users").whereEqualTo("bloodGroup", selected_blood_group)
                .whereEqualTo("isDonor", "yes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    //Toast.makeText(BloodRequestActivity.this, "Error : " + e, Toast.LENGTH_LONG).show();
                }
                else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String id = doc.getDocument().getId();

                            Users users = doc.getDocument().toObject(Users.class).withId(doc.getDocument().getId());
                            userList.add(users);
                            getUserLocation(users);

                        }
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
                        //Log.d(TAG, "userLocation:" + task.getResult().toObject(UserLocation.class));
                        //Log.d(TAG, "userLocation uid:" + task.getResult().toObject(UserLocation.class).userId);
                    }
                }
            }
        });
    }

    public static ArrayList<UserLocation> sortLocations(ArrayList<UserLocation> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<UserLocation>() {
            @Override
            public int compare(UserLocation o, UserLocation o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o.getGeo_point().getLatitude(), o.getGeo_point().getLongitude(), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o2.getGeo_point().getLatitude(), o2.getGeo_point().getLongitude(), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }



    @Override
    public void onItemSelected
            (AdapterView<?> parent, View view, int position, long id) {

        String selected_blood_group = requestBloodGroup.getSelectedItem().toString();
        queryFirebase(selected_blood_group);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
