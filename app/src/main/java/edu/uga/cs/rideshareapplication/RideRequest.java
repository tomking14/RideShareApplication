package edu.uga.cs.rideshareapplication;

public class RideRequest {
    public String userRequestEmail;
    public String date;
    public String departureLocation;
    public String dropOffLocation;

    public RideRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(RideOffer.class)
    }

    public RideRequest(String email,String date, String departureLocation, String dropOffLocation) {
        this.userRequestEmail = email;
        this.date = date;
        this.departureLocation = departureLocation;
        this.dropOffLocation = dropOffLocation;
    }
}

