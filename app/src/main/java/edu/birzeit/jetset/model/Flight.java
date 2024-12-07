package edu.birzeit.jetset.model;

public class Flight {
    private int flightId;
    private String flightNumber;
    private String departureCity;
    private String destinationCity;
    private String departureDateTime;
    private String arrivalDateTime;
    private String duration;
    private String aircraftModel;
    private int currentReservations;
    private int maxSeats;
    private int missedFlights;
    private String bookingOpenDate;
    private double priceEconomy;
    private double priceBusiness;
    private double priceExtraBaggage;
    private String isRecurrent;

    public Flight() {
    }

    public Flight(int flightId, String flightNumber, String departureCity, String destinationCity, String departureDateTime, String arrivalDateTime,
                  String duration, String aircraftModel, int currentReservations, int maxSeats, int missedFlights,
                  String bookingOpenDate, double priceEconomy, double priceBusiness, double priceExtraBaggage, String isRecurrent) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.duration = duration;
        this.aircraftModel = aircraftModel;
        this.currentReservations = currentReservations;
        this.maxSeats = maxSeats;
        this.missedFlights = missedFlights;
        this.bookingOpenDate = bookingOpenDate;
        this.priceEconomy = priceEconomy;
        this.priceBusiness = priceBusiness;
        this.priceExtraBaggage = priceExtraBaggage;
        this.isRecurrent = isRecurrent;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(String departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(String arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public int getCurrentReservations() {
        return currentReservations;
    }

    public void setCurrentReservations(int currentReservations) {
        this.currentReservations = currentReservations;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getMissedFlights() {
        return missedFlights;
    }

    public void setMissedFlights(int missedFlights) {
        this.missedFlights = missedFlights;
    }

    public String getBookingOpenDate() {
        return bookingOpenDate;
    }

    public void setBookingOpenDate(String bookingOpenDate) {
        this.bookingOpenDate = bookingOpenDate;
    }

    public double getPriceEconomy() {
        return priceEconomy;
    }

    public void setPriceEconomy(double priceEconomy) {
        this.priceEconomy = priceEconomy;
    }

    public double getPriceBusiness() {
        return priceBusiness;
    }

    public void setPriceBusiness(double priceBusiness) {
        this.priceBusiness = priceBusiness;
    }

    public double getPriceExtraBaggage() {
        return priceExtraBaggage;
    }

    public void setPriceExtraBaggage(double priceExtraBaggage) {
        this.priceExtraBaggage = priceExtraBaggage;
    }

    public String getIsRecurrent() {
        return isRecurrent;
    }

    public void setIsRecurrent(String isRecurrent) {
        this.isRecurrent = isRecurrent;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightNumber='" + flightNumber + '\'' +
                ", departureCity='" + departureCity + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", departureDateTime=" + departureDateTime +
                ", arrivalDateTime=" + arrivalDateTime +
                ", duration='" + duration + '\'' +
                ", aircraftModel='" + aircraftModel + '\'' +
                ", currentReservations=" + currentReservations +
                ", maxSeats=" + maxSeats +
                ", missedFlights=" + missedFlights +
                ", bookingOpenDate=" + bookingOpenDate +
                ", priceEconomy=" + priceEconomy +
                ", priceBusiness=" + priceBusiness +
                ", priceExtraBaggage=" + priceExtraBaggage +
                ", isRecurrent='" + isRecurrent + '\'' +
                '}';
    }
}
