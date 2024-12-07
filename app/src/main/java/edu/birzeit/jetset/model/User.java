package edu.birzeit.jetset.model;

public abstract class User {
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String hashedPassword;

    public User(){
        this.email = "";
        this.phoneNumber = "";
        this.firstName = "";
        this.lastName = "";
        this.hashedPassword = "";
    }

    public User(String email, String phoneNumber, String firstName, String lastName, String hashedPassword) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hashedPassword = hashedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

}

