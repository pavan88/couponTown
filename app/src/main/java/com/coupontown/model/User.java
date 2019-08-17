package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.auth.FirebaseUser;

public class User implements Parcelable {

    private FirebaseUser firebaseUser;

    private Favourite favourite;

    private String mobilenumber;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.firebaseUser, flags);
        dest.writeParcelable(this.favourite, flags);
        dest.writeString(this.mobilenumber);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.firebaseUser = in.readParcelable(FirebaseUser.class.getClassLoader());
        this.favourite = in.readParcelable(Favourite.class.getClassLoader());
        this.mobilenumber = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public Favourite getFavourite() {
        return favourite;
    }

    public void setFavourite(Favourite favourite) {
        this.favourite = favourite;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }
}
