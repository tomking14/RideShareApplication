package edu.uga.cs.rideshareapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            // If landscape orientation, use a specific layout
            setContentView(R.layout.activity_main_horizontal);
        } else {
            // If portrait orientation, use the default layout
            setContentView(R.layout.activity_main);
        }

        loginButton = findViewById(R.id.confirmBtn);
        loginButton.setOnClickListener(new ActivityStarterClass(LoginActivity.class));

        signupButton =  findViewById(R.id.signupBtn);
        signupButton.setOnClickListener(new ActivityStarterClass(SignUpActivity.class));



    }



}