package com.amit.amitvokaltask.model;

import java.util.ArrayList;

/**
 * Created by readyassist on 2/24/18.
 */

public class MainSMSItem {

    private String hours;
    private ArrayList<SmsItem> smsItemArrayList;

    public MainSMSItem(String hours, ArrayList<SmsItem> smsItemArrayList) {
        this.hours = hours;
        this.smsItemArrayList = smsItemArrayList;
    }

    public MainSMSItem() {

    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public ArrayList<SmsItem> getSmsItemArrayList() {
        return smsItemArrayList;
    }

    public void setSmsItemArrayList(ArrayList<SmsItem> smsItemArrayList) {
        this.smsItemArrayList = smsItemArrayList;
    }
}
