package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;

public class FacebookResponse implements Serializable {

    private String userId;

    private URL profilepicture;

    private String first_name;

    private String last_name;

    private String email;

    private String gender;

    private String telenumber;

    private String birthday;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public URL getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(URL profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTelenumber() {
        return telenumber;
    }

    public void setTelenumber(String telenumber) {
        this.telenumber = telenumber;
    }

    @Override
    public String toString() {
        return "FacebookResponse{" +
                "userId='" + userId + '\'' +
                ", profilepicture=" + profilepicture +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", telenumber='" + telenumber + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }


}
