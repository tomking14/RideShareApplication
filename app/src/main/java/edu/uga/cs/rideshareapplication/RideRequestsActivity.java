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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
                        addRequestToContainer(null,userMail,date, departureLocation, dropOffLocation);

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


    private void addRequestToContainer(String key, String email, String date, String departureLocation, String dropOffLocation) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_ride_offfer, requestContainer, false);

        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvDepartureLocation = cardView.findViewById(R.id.tvDepartureLocation);
        TextView tvDropOffLocation = cardView.findViewById(R.id.tvDropOffLocation);
        Button acceptButton = cardView.findViewById(R.id.btnAccept);
        Button modifyButton = cardView.findViewById(R.id.btnModify);
        Button deleteBtn = cardView.findViewById(R.id.btnDelete);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click
                handleAcceptButtonClick(email, date, departureLocation, dropOffLocation);
            }
        });
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMail != null && userMail.equals(email)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RideRequestsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.request_dialog, null);
                    builder.setView(dialogView);

                    EditText editText1 = dialogView.findViewById(R.id.editText1);
                    EditText editText2 = dialogView.findViewById(R.id.editText2);
                    EditText editText3 = dialogView.findViewById(R.id.editText3);

                    // Pre-fill the dialog with existing request data
                    editText1.setText(date);
                    editText2.setText(departureLocation);
                    editText3.setText(dropOffLocation);

                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newDate = editText1.getText().toString();
                            String newDepartureLocation = editText2.getText().toString();
                            String newDropOffLocation = editText3.getText().toString();

                            RideRequest updatedRequest = new RideRequest(email, newDate, newDepartureLocation, newDropOffLocation);

                            // Update Firebase with the new details
                            requestRef.child(key).setValue(updatedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update the card view or refresh the UI
                                    tvDate.setText("Date: " + newDate);
                                    tvDepartureLocation.setText("Departure: " + newDepartureLocation);
                                    tvDropOffLocation.setText("Drop-off: " + newDropOffLocation);
                                    Toast.makeText(RideRequestsActivity.this, "Request updated successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RideRequestsActivity.this, "Failed to update the request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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
                } else {
                    Toast.makeText(RideRequestsActivity.this, "You can only edit your own requests.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMail != null && userMail.equals(email)) {
                    // The user email matches the one on the card, proceed with deletion
                    requestRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            requestContainer.removeView(cardView);
                            Toast.makeText(RideRequestsActivity.this, "Request deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RideRequestsActivity.this, "Failed to delete the request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // The user email does not match the one on the card
                    Toast.makeText(RideRequestsActivity.this, "You can only delete your own requests.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvDate.setText("Date: " + date);
        tvDepartureLocation.setText("Departure: " + departureLocation);
        tvDropOffLocation.setText("Drop-off: " + dropOffLocation);

        requestContainer.addView(cardView);
    }
    private void handleAcceptButtonClick(String email, String date, String departureLocation, String dropOffLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RideRequestsActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(dialogView);

        // Fetch and set the text for the TextView
        TextView tvRideRequestDetails = dialogView.findViewById(R.id.tvRideRequestDetails);
        TextView tvRideDate = dialogView.findViewById(R.id.tvRideDate);
        TextView tvRideDeparture = dialogView.findViewById(R.id.tvRideDeparture);
        TextView tvRideDropOff = dialogView.findViewById(R.id.tvRideDropOff);
        tvRideRequestDetails.setText("Are you sure you want to accept the ride request for " + email + "?");
        tvRideDate.setText("Date: " + date);
        tvRideDeparture.setText("Departure: " + departureLocation);
        tvRideDropOff.setText("Drop-off: " + dropOffLocation);
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
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestContainer.removeAllViews(); // Clear existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideRequest request = snapshot.getValue(RideRequest.class);
                    if (request != null) {
                        String key = snapshot.getKey();
                        addRequestToContainer(key, request.userRequestEmail, request.date, request.departureLocation, request.dropOffLocation);
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