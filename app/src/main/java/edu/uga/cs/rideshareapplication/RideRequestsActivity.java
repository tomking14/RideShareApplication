package edu.uga.cs.rideshareapplication;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RideRequestsActivity extends AppCompatActivity {
    private DatabaseReference requestRef;
    private String userMail;

    private LinearLayout requestContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_request);
        ExtendedFloatingActionButton fabAddOffer = findViewById(R.id.fab_add_request);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            userMail = currentUser.getEmail();
//            Toast.makeText(RideRequestsActivity.this, userMail , Toast.LENGTH_SHORT).show();
            // Now you can use the email in your activity
        } else {
            Toast.makeText(RideRequestsActivity.this, "Uh oh, I couldn't sign you in." , Toast.LENGTH_SHORT).show();
            // No user is signed in
        }

        requestContainer = findViewById(R.id.requestContainer);

        requestRef = FirebaseDatabase.getInstance().getReference("ride_request");

        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, RideOffer> uniqueOffers = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideOffer offer = snapshot.getValue(RideOffer.class);
                    if (offer != null) {
                        String key = offer.userOfferEmail + offer.date + offer.departureLocation + offer.dropOffLocation;
                        if (!uniqueOffers.containsKey(key)) {
                            uniqueOffers.put(key, offer);
                        } else {
                            // This is a duplicate, so remove it from Firebase
                            snapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RideOffersActivity", "Error fetching data", databaseError.toException());
            }
        });


        fabAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the dialog with the layout
                AlertDialog.Builder builder = new AlertDialog.Builder(RideRequestsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.request_dialog, null);
                builder.setView(dialogView);

                // Set up the EditTexts and other views here if needed
                EditText editText1 = dialogView.findViewById(R.id.editText1);
                EditText editText2 = dialogView.findViewById(R.id.editText2);
                EditText editText3 = dialogView.findViewById(R.id.editText3);

                // Set up a button to close the dialog
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = editText1.getText().toString();
                        String departureLocation = editText2.getText().toString();
                        String dropOffLocation = editText3.getText().toString();
                        addRequestToContainer(userMail,date, departureLocation, dropOffLocation);

                        RideRequest request = new RideRequest(userMail,date, departureLocation, dropOffLocation);

                        // Push the offer to the database
                        requestRef.push().setValue(request);


                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



    }


    private void addRequestToContainer(String email, String date, String departureLocation, String dropOffLocation) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_ride_offfer, requestContainer, false);

        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvDepartureLocation = cardView.findViewById(R.id.tvDepartureLocation);
        TextView tvDropOffLocation = cardView.findViewById(R.id.tvDropOffLocation);
        Button acceptButton = cardView.findViewById(R.id.btnAccept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click
                handleAcceptButtonClick(email, date, departureLocation, dropOffLocation);
            }
        });

        tvDate.setText("Date: " + date);
        tvDepartureLocation.setText("Departure: " + departureLocation);
        tvDropOffLocation.setText("Drop-off: " + dropOffLocation);

        requestContainer.addView(cardView);
    }
    private void handleAcceptButtonClick(String email, String date, String departureLocation, String dropOffLocation) {
        Toast.makeText(RideRequestsActivity.this, "user that made request: " + email, Toast.LENGTH_SHORT).show();
    }

    private void fetchAndDisplayOffers() {
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestContainer.removeAllViews(); // Clear existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideRequest request = snapshot.getValue(RideRequest.class);
                    if (request != null) {
                        addRequestToContainer(request.userRequestEmail, request.date, request.departureLocation, request.dropOffLocation);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RideOffersActivity", "Error fetching data", databaseError.toException());
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        fetchAndDisplayOffers();
    }


}