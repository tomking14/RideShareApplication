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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Transaction;


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
                        String key = offer.userRequestEmail  + offer.date + offer.departureLocation + offer.dropOffLocation;
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

                        RideRequest request = new RideRequest(userMail, date, departureLocation, dropOffLocation);

                        // Push the offer to the database and retrieve the key
                        DatabaseReference newRef = offersRef.push();
                        newRef.setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Once the data is pushed successfully, fetch the key
                                String firebaseKey = newRef.getKey();
                                // Now use this key to add the request to the container
                                addOfferToContainer(firebaseKey, userMail, date, departureLocation, dropOffLocation);
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
            }
        });



    }


    private void addOfferToContainer(String key,String userRequestEmail, String date, String departureLocation, String dropOffLocation) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_ride_offfer, offerContainer, false);

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
                // You can use date, departureLocation, and dropOffLocation here
                handleAcceptButtonClick(key, userRequestEmail, date, departureLocation, dropOffLocation);            }
        });
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMail != null && userMail.equals(userRequestEmail)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RideOffersActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.request_dialog, null);

                    cardView.setTag(key);

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

                            RideRequest updatedRequest = new RideRequest(userRequestEmail, newDate, newDepartureLocation, newDropOffLocation);

                            // Update Firebase with the new details
                            offersRef.child(key).setValue(updatedRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update the card view or refresh the UI
                                    tvDate.setText("Date: " + newDate);
                                    tvDepartureLocation.setText("Departure: " + newDepartureLocation);
                                    tvDropOffLocation.setText("Drop-off: " + newDropOffLocation);
                                    Toast.makeText(RideOffersActivity.this, "Offer updated successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RideOffersActivity.this, "Failed to update the request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RideOffersActivity.this, "You can only edit your own offers.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMail != null && userMail.equals(userRequestEmail)) {
                    // The user email matches the one on the card, proceed with deletion
                    offersRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            offerContainer.removeView(cardView);
                            Toast.makeText(RideOffersActivity.this, "Offer deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RideOffersActivity.this, "Failed to delete the offer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // The user email does not match the one on the card
                    Toast.makeText(RideOffersActivity.this, "You can only delete your own offers.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvDate.setText("Date: " + date);
        tvDepartureLocation.setText("Departure: " + departureLocation);
        tvDropOffLocation.setText("Drop-off: " + dropOffLocation);

        offerContainer.addView(cardView);


    }
    private void handleAcceptButtonClick(String key,String userRequestEmail ,String date, String departureLocation, String dropOffLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RideOffersActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(dialogView);

        // Fetch and set the text for the TextView
        TextView tvRideRequestDetails = dialogView.findViewById(R.id.tvRideRequestDetails);
        TextView tvRideDate = dialogView.findViewById(R.id.tvRideDate);
        TextView tvRideDeparture = dialogView.findViewById(R.id.tvRideDeparture);
        TextView tvRideDropOff = dialogView.findViewById(R.id.tvRideDropOff);
        tvRideRequestDetails.setText("Are you sure you want to accept the ride request for " + key + "?");
        tvRideDate.setText("Date: " + date);
        tvRideDeparture.setText("Departure: " + departureLocation);
        tvRideDropOff.setText("Drop-off: " + dropOffLocation);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && currentUser.getEmail() != null && userRequestEmail != null && !currentUser.getEmail().equals(userRequestEmail)) {
                    // Prepare to move the accepted offer to the 'accepted_rides' node
                    DatabaseReference acceptedRidesRef = FirebaseDatabase.getInstance().getReference("accepted_rides").child("ride_offers");

                    // Create a hashmap or use a POJO to represent the accepted offer
                    HashMap<String, Object> acceptedOffer = new HashMap<>();
                    acceptedOffer.put("userRequestEmail", userRequestEmail);
                    acceptedOffer.put("date", date);
                    acceptedOffer.put("departureLocation", departureLocation);
                    acceptedOffer.put("dropOffLocation", dropOffLocation);

                    // Add the accepted offer to the 'accepted_rides' node
                    acceptedRidesRef.push().setValue(acceptedOffer).addOnSuccessListener(aVoid -> {
                        // After successfully adding to 'accepted_rides', delete from 'ride_offers'
                        offersRef.child(key).removeValue();
                        // Update points
                        String acceptorEmailKey = currentUser.getEmail().replace(".", ",");
                        String creatorEmailKey = userRequestEmail.replace(".", ",");
                        updateUserPoints(creatorEmailKey, 25); // Add points to offer creator
                        updateUserPoints(acceptorEmailKey, -25); // Subtract points from acceptor
                        Toast.makeText(RideOffersActivity.this, "Offer accepted and moved to 'accepted_rides'.", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(RideOffersActivity.this, "Failed to move offer to 'accepted_rides'.", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Toast.makeText(RideOffersActivity.this, "There was a problem accepting the offer.", Toast.LENGTH_SHORT).show();
                }
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

    private void updateUserPoints(String emailKey, int pointsDelta) {
        DatabaseReference userPointsRef = FirebaseDatabase.getInstance().getReference("users").child(emailKey).child("points");

        userPointsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentPoints = mutableData.getValue(Integer.class);
                if (currentPoints == null) {
                    currentPoints = 0;
                }
                int newPoints = currentPoints + pointsDelta;
                newPoints = Math.max(newPoints, 0); // Prevent negative points
                mutableData.setValue(newPoints);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                Log.d("RideOffersActivity", "updateUserPoints:onComplete:" + databaseError);
            }
        });
    }












    private void fetchAndDisplayOffers() {
        offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offerContainer.removeAllViews(); // Clear existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideOffer offer = snapshot.getValue(RideOffer.class);
                    if (offer != null) {
                        String key = snapshot.getKey();
                        addOfferToContainer(key,offer.userRequestEmail , offer.date, offer.departureLocation, offer.dropOffLocation);
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