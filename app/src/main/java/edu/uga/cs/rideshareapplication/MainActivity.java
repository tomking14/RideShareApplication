package edu.uga.cs.rideshareapplication;

import androidx.appcompat.app.AppCompatActivity;
import edu.uga.cs.rideshareapplication.ActivityStarterClass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new ActivityStarterClass(LoginActivity.class));

        signupButton =  findViewById(R.id.signupBtn);
        signupButton.setOnClickListener(new ActivityStarterClass(SignUpActivity.class));

    }


}