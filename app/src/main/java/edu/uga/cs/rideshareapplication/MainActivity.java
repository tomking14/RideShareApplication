package edu.uga.cs.rideshareapplication;

import androidx.appcompat.app.AppCompatActivity;

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
        loginButton.setOnClickListener(new OverviewButtonClickListener(LoginActivity.class));

        signupButton =  findViewById(R.id.signupBtn);
        signupButton.setOnClickListener(new OverviewButtonClickListener(SignUpActivity.class));

    }

    private class OverviewButtonClickListener implements View.OnClickListener {
        private Class<?> activityToStart;

        public OverviewButtonClickListener(Class<?> activityToStart) {
            this.activityToStart = activityToStart;
        }

        @Override
        public void onClick(View view) {
            try {
                Intent intent = new Intent(view.getContext(), activityToStart);
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(view.getContext(), "Uh oh, something went wrong...", Toast.LENGTH_SHORT).show();
            }
        }
    }

}