package edu.uga.cs.rideshareapplication;

public class RideOffer {

    public String userOfferEmail;
    public String date;
    public String departureLocation;
    public String dropOffLocation;


    public RideOffer() {
        // Default constructor required for calls to DataSnapshot.getValue(RideOffer.class)
    }

    public RideOffer(String email, String date, String departureLocation, String dropOffLocation) {
        this.userOfferEmail = email;
        this.date = date;
        this.departureLocation = departureLocation;
        this.dropOffLocation = dropOffLocation;
    }



}


