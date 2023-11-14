package edu.uga.cs.rideshareapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {
    private Button signOutButton;
    private Button rideRequests;

    private Button rideOffers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        signOutButton = findViewById(R.id.signoutBtn);
        rideOffers = findViewById(R.id.offersBtn);
        rideRequests = findViewById(R.id.requestsBtn);

        rideRequests.setOnClickListener(new ActivityStarterClass(RideRequestsActivity.class));
        rideOffers.setOnClickListener(new ActivityStarterClass(RideOffersActivity.class));

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });
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
