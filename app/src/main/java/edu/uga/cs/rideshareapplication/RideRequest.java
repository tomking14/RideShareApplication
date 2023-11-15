package edu.uga.cs.rideshareapplication;

public class RideRequest {
    public String date;
    public String departureLocation;
    public String dropOffLocation;

    public RideRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(RideOffer.class)
    }

    public RideRequest(String date, String departureLocation, String dropOffLocation) {
        this.date = date;
        this.departureLocation = departureLocation;
        this.dropOffLocation = dropOffLocation;
    }
}

