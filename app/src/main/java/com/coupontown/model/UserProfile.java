package com.coupontown.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class UserProfile implements Parcelable {

    private String full_name;

    private String email;

    private String uid;

    private Uri profile_pic;

    private String phonenumber;

    private String provider;

    private boolean isAuthenticated;

    private boolean isVerified;

    private String lastloggedin;

    private boolean multipleAccount;

    private String picurlstr;

    private Date createdon;

    private Date modifiedon;

    private List<String> favorites;



    //TODO need to add created/modified => date/time
    public UserProfile() {
        super();
    }

    protected UserProfile(Parcel in) {
        full_name = in.readString();
        email = in.readString();
        uid = in.readString();
        profile_pic = in.readParcelable(Uri.class.getClassLoader());
        phonenumber = in.readString();
        provider = in.readString();
        isAuthenticated = in.readByte() != 0;
        isVerified = in.readByte() != 0;
        lastloggedin = in.readString();
        multipleAccount = in.readByte() != 0;
        picurlstr = in.readString();
        favorites = in.createStringArrayList();
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }


    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Uri getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Uri profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isVerified(boolean emailVerified) {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isMultipleAccount() {
        return multipleAccount;
    }

    public void setMultipleAccount(boolean multipleAccount) {
        this.multipleAccount = multipleAccount;
    }

    public String getLastloggedin() {
        return lastloggedin;
    }

    public void setLastloggedin(String lastloggedin) {
        this.lastloggedin = lastloggedin;
    }

    public String getPicurlstr() {
        return picurlstr;
    }

    public void setPicurlstr(String picurlstr) {
        this.picurlstr = picurlstr;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(full_name);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeParcelable(profile_pic, i);
        parcel.writeString(phonenumber);
        parcel.writeString(provider);
        parcel.writeByte((byte) (isAuthenticated ? 1 : 0));
        parcel.writeByte((byte) (isVerified ? 1 : 0));
        parcel.writeString(lastloggedin);
        parcel.writeByte((byte) (multipleAccount ? 1 : 0));
        parcel.writeString(picurlstr);
        parcel.writeStringList(favorites);
    }
}
