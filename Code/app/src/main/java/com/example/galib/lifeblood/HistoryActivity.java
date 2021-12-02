package com.example.galib.lifeblood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity{

    private Toolbar historyToolbar;

    private RecyclerView historyRecyclerView;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private HistoryAdapter historyRecyclerAdapter;
    private ArrayList<BloodRequest> bloodRequests= new ArrayList<>();

    private String current_user_id;
    private ArrayList<Users> userList = new ArrayList<>();

    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(historyToolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("BloodRequests").whereEqualTo("user_id", current_user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    //Toast.makeText(HistoryActivity.this, "Error : " + e, Toast.LENGTH_LONG).show();
                }
                else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String id = doc.getDocument().getId();

                            BloodRequest bloodRequest = doc.getDocument().toObject(BloodRequest.class);
                            bloodRequests.add(bloodRequest);
                            Log.d(TAG, "history:" + bloodRequest);

                        }
                    }
                }
            }
        });

        historyRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);

        historyRecyclerView();


    }

    private void historyRecyclerView() {
        historyRecyclerAdapter = new HistoryAdapter(bloodRequests);
        historyRecyclerView.setAdapter(historyRecyclerAdapter);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
