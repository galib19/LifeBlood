package com.example.galib.lifeblood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DonorDeactivationActivity extends AppCompatActivity {

    private Toolbar deactivationToolbar;

    private Button deactivationBtn;
    private Button reactivationBtn;
    private ProgressBar deactivationProgress;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private String isDonor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_deactivation);

        deactivationToolbar = (Toolbar) findViewById(R.id.donor_deactivation_toolbar);
        setSupportActionBar(deactivationToolbar);
        getSupportActionBar().setTitle("Donorship Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deactivationBtn = findViewById(R.id.donor_deactivation_btn);
        reactivationBtn = findViewById(R.id.donor_reactivation_btn);

        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        checkDonorship();

    }

    private void checkDonorship(){
        final DocumentReference docRef = firebaseFirestore.collection("Users").document(current_user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Users user = document.toObject(Users.class).withId(document.getId());
                        isDonor = user.getIsDonor();

                        if(isDonor.equals("yes")){
                            deactivationBtn.setVisibility(View.VISIBLE);
                            Log.d("asd", "isDonor" + isDonor);
                            deactivationBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    docRef
                                            .update("isDonor", "no")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    deactivationBtn.setVisibility(View.INVISIBLE);
                                                    reactivationBtn.setVisibility(View.VISIBLE);
                                                    Toast.makeText(DonorDeactivationActivity.this, "Donorship Deactivated", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(DonorDeactivationActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                    });
                                }
                            });
                        } else {
                            reactivationBtn.setVisibility(View.VISIBLE);
                            reactivationBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    docRef
                                            .update("isDonor", "yes")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    deactivationBtn.setVisibility(View.VISIBLE);
                                                    reactivationBtn.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(DonorDeactivationActivity.this, "Donorship Reactivated", Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(DonorDeactivationActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }

                    } else {
                    }
                } else {
                }
            }
        });
    }
}
