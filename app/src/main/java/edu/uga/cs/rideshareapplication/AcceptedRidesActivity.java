package edu.uga.cs.rideshareapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AcceptedRidesActivity extends AppCompatActivity {

    private ListView acceptedRidesList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listOfRides;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_rides);

        acceptedRidesList = findViewById(R.id.accepted_rides_list);
        listOfRides = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfRides);
        acceptedRidesList.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null && !userEmail.isEmpty()) {
                // Log the email being used for the database query
                Log.d("AcceptedRidesActivity", "User Email: " + userEmail);

                // Fetch accepted ride offers
                databaseReference = FirebaseDatabase.getInstance().getReference("accepted_rides/ride_offers");
                fetchAcceptedRides(databaseReference, "Offer", userEmail);

                // Fetch accepted ride requests
                databaseReference = FirebaseDatabase.getInstance().getReference("accepted_rides/ride_requests");
                fetchAcceptedRides(databaseReference, "Request", userEmail);
            } else {
                Log.e("AcceptedRidesActivity", "User email is null or empty.");
            }
        } else {
            Log.e("AcceptedRidesActivity", "No user is currently logged in.");
        }
    }

    private void fetchAcceptedRides(DatabaseReference ref, String rideType, String userEmail) {
        ref.orderByChild("userRequestEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                        String date = rideSnapshot.child("date").getValue(String.class);
                        String departureLocation = rideSnapshot.child("departureLocation").getValue(String.class);
                        String dropOffLocation = rideSnapshot.child("dropOffLocation").getValue(String.class);

                        String rideDetails = rideType + " - Date: " + date + ", Departure: " + departureLocation + ", Drop-off: " + dropOffLocation;
                        listOfRides.add(rideDetails);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("AcceptedRidesActivity", "No data found for: " + userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AcceptedRidesActivity", "Database error", databaseError.toException());
            }
        });
    }
}
