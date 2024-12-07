package edu.birzeit.jetset.model;

public class Passenger extends User {
    private String passportNumber;
    private String passportIssuePlace;
    private String passportIssueDate;
    private String passportExpiryDate;
    private String dateOfBirth;
    private String foodPreference;
    private String nationality;

    public Passenger() {
        super();
    }

    public Passenger(String email, String phoneNumber, String firstName, String lastName, String password,
                     String passportNumber, String passportIssueDate, String passportIssuePlace, String passportExpiryDate,
                     String foodPreference, String dateOfBirth, String nationality) {

        super(email, phoneNumber, firstName, lastName, password);
        this.passportNumber = passportNumber;
        this.passportIssueDate = passportIssueDate;
        this.passportIssuePlace = passportIssuePlace;
        this.passportExpiryDate = passportExpiryDate;
        this.foodPreference = foodPreference;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(String passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public String getPassportIssuePlace() {
        return passportIssuePlace;
    }

    public void setPassportIssuePlace(String passportIssuePlace) {
        this.passportIssuePlace = passportIssuePlace;
    }

    public String getPassportExpiryDate() {
        return passportExpiryDate;
    }

    public void setPassportExpiryDate(String passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}

