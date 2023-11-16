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

public class RideOffersActivity extends AppCompatActivity {
    private DatabaseReference offersRef;
    private LinearLayout offerContainer;
    private String userMail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_offers);

        ExtendedFloatingActionButton fabAddOffer = findViewById(R.id.fab_add_Offer);

        offerContainer = findViewById(R.id.offerContainer);

        offersRef = FirebaseDatabase.getInstance().getReference("ride_offers");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            userMail = currentUser.getEmail();
//            Toast.makeText(RideOffersActivity.this, userMail , Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(RideOffersActivity.this, "Uh oh, I couldn't sign you in." , Toast.LENGTH_SHORT).show();
            // No user is signed in
        }

//delete

       offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(RideOffersActivity.this);
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
                        addOfferToContainer(userMail, date, departureLocation, dropOffLocation);



                        RideOffer offer = new RideOffer( userMail, date, departureLocation, dropOffLocation);

                        // Push the offer to the database
                        offersRef.push().setValue(offer);

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


    private void addOfferToContainer(String email, String date, String departureLocation, String dropOffLocation) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_ride_offfer, offerContainer, false);

        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvDepartureLocation = cardView.findViewById(R.id.tvDepartureLocation);
        TextView tvDropOffLocation = cardView.findViewById(R.id.tvDropOffLocation);
        Button acceptButton = cardView.findViewById(R.id.btnAccept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click
                // You can use date, departureLocation, and dropOffLocation here
                handleAcceptButtonClick(email, date, departureLocation, dropOffLocation);
            }
        });

        tvDate.setText("Date: " + date);
        tvDepartureLocation.setText("Departure: " + departureLocation);
        tvDropOffLocation.setText("Drop-off: " + dropOffLocation);

        offerContainer.addView(cardView);


    }
    private void handleAcceptButtonClick(String email, String date, String departureLocation, String dropOffLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RideOffersActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(dialogView);

        // Fetch and set the text for the TextView
        TextView tvRideRequestDetails = dialogView.findViewById(R.id.tvRideRequestDetails);
        tvRideRequestDetails.setText("Are you sure you want to accept the ride offer for " + email + "?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Confirm action here
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Cancel action here
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchAndDisplayOffers() {
        offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offerContainer.removeAllViews(); // Clear existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideOffer offer = snapshot.getValue(RideOffer.class);
                    if (offer != null) {
                        addOfferToContainer(offer.userOfferEmail, offer.date, offer.departureLocation, offer.dropOffLocation);
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