package edu.uga.cs.rideshareapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomePageActivity extends AppCompatActivity {
    private Button signOutButton;
    private Button rideRequests;

    private Button rideOffers;
    private int userPoints = 0;
    private TextView pointAvail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        signOutButton = findViewById(R.id.signoutBtn);
        rideOffers = findViewById(R.id.offersBtn);
        rideRequests = findViewById(R.id.requestsBtn);
        pointAvail = findViewById(R.id.tvPointsAvail);

        rideRequests.setOnClickListener(new ActivityStarterClass(RideRequestsActivity.class));
        rideOffers.setOnClickListener(new ActivityStarterClass(RideOffersActivity.class));

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("points")) {
                        // User exists and has points, fetch the points
                        Integer points = dataSnapshot.child("points").getValue(Integer.class);
                        // Assuming you have a local variable declared at class level: private int userPoints;
                        userPoints = points != null ? points : 0;  // Assign the points to the local variable

                        pointAvail.setText("Points Available: " + userPoints);
                    } else {

                        Toast.makeText(HomePageActivity.this, "Uh oh, I couldn't find any points." , Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        } else {
            // Handle user not signed in
            Toast.makeText(HomePageActivity.this, "Uh oh, I couldn't sign you in :(" , Toast.LENGTH_SHORT).show();
        }


    }
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        goToMainActivity();
        finish();
        // Update UI to reflect that the user has been signed out
    }
    public void goToMainActivity() {
        Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
