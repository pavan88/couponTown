package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreDetails implements Parcelable {

    private String detail_desc;

    private String comment;

    private String appurl;

    public String getDetail_desc() {
        return detail_desc;
    }

    public void setDetail_desc(String detail_desc) {
        this.detail_desc = detail_desc;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAppurl() {
        return appurl;
    }

    public void setAppurl(String appurl) {
        this.appurl = appurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.detail_desc);
        dest.writeString(this.comment);
        dest.writeString(this.appurl);
    }

    public MoreDetails() {
    }

    protected MoreDetails(Parcel in) {
        this.detail_desc = in.readString();
        this.comment = in.readString();
        this.appurl = in.readString();
    }

    public static final Parcelable.Creator<MoreDetails> CREATOR = new Parcelable.Creator<MoreDetails>() {
        @Override
        public MoreDetails createFromParcel(Parcel source) {
            return new MoreDetails(source);
        }

        @Override
        public MoreDetails[] newArray(int size) {
            return new MoreDetails[size];
        }
    };

    @Override
    public String toString() {
        return "MoreDetails{" +
                "detail_desc='" + detail_desc + '\'' +
                ", comment='" + comment + '\'' +
                ", appurl='" + appurl + '\'' +
                '}';
    }
}
