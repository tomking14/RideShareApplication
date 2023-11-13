package edu.uga.cs.rideshareapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivty);

        loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new ActivityStarterClass(HomePageActivity.class));

    }

}
