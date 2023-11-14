package edu.uga.cs.rideshareapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class RideRequestsActivity extends AppCompatActivity {
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
                EditText editText1 = dialogView.findViewById(R.id.editText1);
                EditText editText2 = dialogView.findViewById(R.id.editText2);
                EditText editText3 = dialogView.findViewById(R.id.editText3);

                // Set up a button to close the dialog
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the confirmation action here
                        // For example, retrieve and use the data from the EditTexts
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
