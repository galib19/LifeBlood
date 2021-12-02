package com.example.galib.lifeblood;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BloodRequest{

    public String hospital_name, request_details, request_blood_group, user_id;
    public @ServerTimestamp
    Date timestamp;

    public BloodRequest(){

    }

    public BloodRequest(String hospital_name, String request_blood_group, String request_details, String user_id, Date timestamp) {
        this.hospital_name = hospital_name;
        this.request_blood_group = request_blood_group;
        this.request_details =request_details;
        this.user_id =user_id;
        this.timestamp=timestamp;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getRequest_details() {
        return request_details;
    }

    public void setRequest_details(String request_details) {
        this.request_details = request_details;
    }

    public String getRequest_blood_group() {
        return request_blood_group;
    }

    public void setRequest_blood_group(String request_blood_group) {
        this.request_blood_group = request_blood_group;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
