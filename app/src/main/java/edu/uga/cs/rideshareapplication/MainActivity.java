package edu.uga.cs.rideshareapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.confirmBtn);
        loginButton.setOnClickListener(new ActivityStarterClass(LoginActivity.class));

        signupButton =  findViewById(R.id.signupBtn);
        signupButton.setOnClickListener(new ActivityStarterClass(SignUpActivity.class));


    }


}