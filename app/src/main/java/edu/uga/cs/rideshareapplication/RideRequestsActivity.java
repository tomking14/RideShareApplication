package edu.uga.cs.rideshareapplication;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class RideRequestsActivity extends AppCompatActivity {
    private LinearLayout requestContainer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_request);
        ExtendedFloatingActionButton fabAddOffer = findViewById(R.id.fab_add_request);

        requestContainer = findViewById(R.id.requestContainer);

        fabAddOffer.setOnClickListener(new View.OnClickListener() {
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
                        String date = editText1.getText().toString();
                        String departureLocation = editText2.getText().toString();
                        String dropOffLocation = editText3.getText().toString();
                        addOfferToContainer(date, departureLocation, dropOffLocation);

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


    private void addOfferToContainer(String date, String departureLocation, String dropOffLocation) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_ride_offfer, requestContainer, false);

        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvDepartureLocation = cardView.findViewById(R.id.tvDepartureLocation);
        TextView tvDropOffLocation = cardView.findViewById(R.id.tvDropOffLocation);

        tvDate.setText(date);
        tvDepartureLocation.setText(departureLocation);
        tvDropOffLocation.setText(dropOffLocation);

        requestContainer.addView(cardView);
    }




}