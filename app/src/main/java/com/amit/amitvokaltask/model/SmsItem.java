package com.amit.amitvokaltask.model;

/**
 * Created by readyassist on 2/24/18.
 */

public class SmsItem {

    private String date,number,message;

    public SmsItem(String date, String number, String message) {
        this.date = date;
        this.number = number;
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
