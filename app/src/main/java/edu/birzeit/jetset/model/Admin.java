package edu.birzeit.jetset.model;

public class Admin extends User {

    public Admin() {
    }

    public Admin(String email, String phoneNumber, String firstName, String lastName, String password) {
        super(email, phoneNumber, firstName, lastName, password);
    }

}

