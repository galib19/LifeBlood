package com.example.galib.lifeblood;

import android.os.Parcel;
import android.os.Parcelable;

public class Users extends UserId implements Parcelable {

    String name, phoneNo, image, bloodGroup, isDonor;

    public Users(){

    }

    public Users(String name, String phoneNo, String image, String bloodGroup, String isDonor) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.image = image;
        this.bloodGroup = bloodGroup;
        this.isDonor = isDonor;

    }

    protected Users(Parcel in) {
        name = in.readString();
        phoneNo = in.readString();
        image = in.readString();
        bloodGroup = in.readString();
        isDonor  = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getIsDonor() {
        return isDonor;
    }

    public void setIsDonor(String isDonor) {
        this.isDonor = isDonor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNo);
        dest.writeString(image);
        dest.writeString(bloodGroup);
        dest.writeString(isDonor);
    }
}
