package com.coupontown.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class UserProfile implements Parcelable {

    private String full_name;

    private String phonenumber;

    private Uri profile_pic;

    private String email;

    private String uid;

    private String provider;

    private Boolean isAuthenticated;

    private Boolean isVerified;

    private String lastloggedin;

    private Boolean multipleAccount;

    private String picurlstr;

    private Date createdon;

    private Date modifiedon;

    private Date lastLogin;

    private List<String> favorites;

    public UserProfile() {

    }

    public UserProfile(UserProfileBuilder userProfileBuilder){
        this.full_name = userProfileBuilder.full_name;
        this.phonenumber = userProfileBuilder.phonenumber;
        this.profile_pic = userProfileBuilder.profile_pic;
        this.email = userProfileBuilder.email;
    }

    public UserProfile(Parcel in) {
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

    public UserProfile(String full_name, String phonenumber) {
        this.full_name = full_name;
        this.phonenumber = phonenumber;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(full_name);
        dest.writeString(email);
        dest.writeString(uid);
        dest.writeParcelable(profile_pic, flags);
        dest.writeString(phonenumber);
        dest.writeString(provider);
        dest.writeByte((byte) (isAuthenticated ? 1 : 0));
        dest.writeByte((byte) (isVerified ? 1 : 0));
        dest.writeString(lastloggedin);
        dest.writeByte((byte) (multipleAccount ? 1 : 0));
        dest.writeString(picurlstr);
        dest.writeStringList(favorites);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public Uri getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Uri profile_pic) {
        this.profile_pic = profile_pic;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getLastloggedin() {
        return lastloggedin;
    }

    public void setLastloggedin(String lastloggedin) {
        this.lastloggedin = lastloggedin;
    }

    public boolean isMultipleAccount() {
        return multipleAccount;
    }

    public void setMultipleAccount(boolean multipleAccount) {
        this.multipleAccount = multipleAccount;
    }

    public String getPicurlstr() {
        return picurlstr;
    }

    public void setPicurlstr(String picurlstr) {
        this.picurlstr = picurlstr;
    }

    public Date getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Date createdon) {
        this.createdon = createdon;
    }

    public Date getModifiedon() {
        return modifiedon;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedon = modifiedon;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
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
                ", picurlstr='" + picurlstr + '\'' +
                ", createdon=" + createdon +
                ", modifiedon=" + modifiedon +
                ", lastLogin=" + lastLogin +
                ", favorites=" + favorites +
                '}';
    }


    public static class UserProfileBuilder{

        private String full_name;

        private String phonenumber;

        private Uri profile_pic;

        private String email;

        public UserProfileBuilder(String email) {
            this.email = email;
        }

        public UserProfileBuilder withfullname(String full_name){
            this.full_name = full_name;
            return this;
        }

        public UserProfileBuilder withPhoneNumber(String phoneNumber){
            this.phonenumber = phoneNumber;
            return this;
        }

        public UserProfileBuilder withPicUrl(Uri profile_pic){
            this.profile_pic = profile_pic;
            return this;
        }

        public UserProfile build() {

            return new UserProfile(this);
        }


    }
}
