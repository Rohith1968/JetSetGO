package edu.birzeit.jetset.model;

public class Reservation {
    private int reservationId;
    private String flightId;
    private String passengerEmail;
    private String flightClass;
    private String numOfExtraBags;
    private String foodPreference;
    private double totalPrice;

    public Reservation() {
    }

    public Reservation(int reservationId, String flightId, String passengerEmail, String flightClass, String numOfExtraBags) {
        this.reservationId = reservationId;
        this.flightId = flightId;
        this.passengerEmail = passengerEmail;
        this.flightClass = flightClass;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getNumOfExtraBags() {
        return numOfExtraBags;
    }

    public void setNumOfExtraBags(String numOfExtraBags) {
        this.numOfExtraBags = numOfExtraBags;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(String flightClass) {
        this.flightClass = flightClass;
    }
}
