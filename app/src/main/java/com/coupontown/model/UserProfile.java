package com.coupontown.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

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

    public UserProfile() {
        super();
    }


    public UserProfile(Parcel parcel) {
        full_name = parcel.readString();
        profile_pic = parcel.readParcelable(Uri.class.getClassLoader());
        email = parcel.readString();
        uid = parcel.readString();
        phonenumber = parcel.readString();
        provider = parcel.readString();
        isAuthenticated = parcel.readByte() != 0;
        isVerified = parcel.readByte() != 0;
        multipleAccount = parcel.readByte() != 0;
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

    // @Override
    public int describeContents() {
        return 0;
    }

    // @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(full_name);
        parcel.writeParcelable(profile_pic, i);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeString(phonenumber);
        parcel.writeString(provider);
        parcel.writeByte((byte) (isAuthenticated ? 1 : 0));
        parcel.writeByte((byte) (isVerified ? 1 : 0));
        parcel.writeByte((byte) (multipleAccount ? 1 : 0));
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


    @Override
    public String toString() {
        return "UserProfile{" +
                "full_name='" + full_name + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", profile_pic=" + profile_pic +
                ", phonenumber='" + phonenumber + '\'' +
                ", provider='" + provider + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                ", isVerified=" + isVerified +
                ", lastloggedin='" + lastloggedin + '\'' +
                ", multipleAccount=" + multipleAccount +
                '}';
    }
}
