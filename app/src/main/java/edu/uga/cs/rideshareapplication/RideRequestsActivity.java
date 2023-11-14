package edu.uga.cs.rideshareapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class RideRequestsActivity extends AppCompatActivity {
    private int lastViewId = R.id.fab_add_request;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_request);
        ExtendedFloatingActionButton fabAddRequest = findViewById(R.id.fab_add_request);
        fabAddRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the dialog with the layout
                AlertDialog.Builder builder = new AlertDialog.Builder(RideRequestsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.request_dialog, null);
                builder.setView(dialogView);

                // Set up the EditTexts and other views here if needed
                EditText editTextDate = dialogView.findViewById(R.id.editText1);
                EditText editTextDeparture = dialogView.findViewById(R.id.editText2);
                EditText editTextDropOff = dialogView.findViewById(R.id.editText3);
                // Set up a button to close the dialog
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = editTextDate.getText().toString();
                        String departure = editTextDeparture.getText().toString();
                        String dropOff = editTextDropOff.getText().toString();

                        // Call the function to create a card with the data
//                        createCardWithText(date, departure, dropOff);
                        dialog.dismiss();
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





}
