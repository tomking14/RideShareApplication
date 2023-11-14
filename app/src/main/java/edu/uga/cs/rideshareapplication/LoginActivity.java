package edu.uga.cs.rideshareapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    public static final String TAG = "SuperApp";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivty);

        loginButton = findViewById(R.id.loginBtn);
//        loginButton.setOnClickListener(new ActivityStarterClass(HomePageActivity.class));
        loginButton = findViewById(R.id.loginBtn);
        TextInputEditText loginUsernameEditText = findViewById(R.id.login_username);
        TextInputEditText loginPasswordEditText = findViewById(R.id.login_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user inputs
                String username = loginUsernameEditText.getText().toString();
                String password = loginPasswordEditText.getText().toString();

                // Authenticate with Firebase
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                    // Use ActivityStarterClass to start HomePageActivity
                                    ActivityStarterClass activityStarter = new ActivityStarterClass(HomePageActivity.class);
                                    activityStarter.onClick(v); // You can pass the button itself as the view
                                } else {
                                    // Sign in fails
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



    }

}
