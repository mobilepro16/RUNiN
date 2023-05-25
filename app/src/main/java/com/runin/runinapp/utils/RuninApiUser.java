package com.runin.runinapp.utils;

/**
 * Maps a user to the corresponding API request
 * Created by Samuel Kobelkowsky on 10/17/17.
 */
public class RuninApiUser {

    private String firstName;
    private String lastName;
    private String email;
    private int gender;
    private String facebookId;

    String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
