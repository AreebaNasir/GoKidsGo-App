package com.areeba.hciproject;
import android.net.Uri;

import java.io.Serializable;

public class Contacts implements Serializable
{
    private String firstname,lastname,dob,gender,phno,dp,email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Contacts(String firstname, String lastname, String dob, String gender, String phno, String bio, String dp, String email) {
        this.firstname= firstname;
        this.lastname = lastname;
        this.dob=dob;
        this.gender=gender;
        this.phno=phno;
        this.dp = dp;
        this.email = email;
    }

    public Contacts() {

    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }
}

