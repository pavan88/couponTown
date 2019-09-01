package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreDetails implements Parcelable {

    private String detail_desc;

    private String comment;

    private String appurl;

    private Boolean isDeal = Boolean.FALSE;

    private Boolean isCouponCode = Boolean.FALSE;

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

    public Boolean getDeal() {
        return isDeal;
    }

    public void setDeal(Boolean deal) {
        isDeal = deal;
    }

    public Boolean getCouponCode() {
        return isCouponCode;
    }

    public void setCouponCode(Boolean couponCode) {
        isCouponCode = couponCode;
    }

    @Override
    public String toString() {
        return "MoreDetails{" +
                "detail_desc='" + detail_desc + '\'' +
                ", comment='" + comment + '\'' +
                ", appurl='" + appurl + '\'' +
                ", isDeal=" + isDeal +
                ", isCouponCode=" + isCouponCode +
                '}';
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
        dest.writeByte(this.isDeal ? (byte) 1 : (byte) 0);
        dest.writeValue(this.isCouponCode);
    }

    public MoreDetails() {
    }

    protected MoreDetails(Parcel in) {
        this.detail_desc = in.readString();
        this.comment = in.readString();
        this.appurl = in.readString();
        this.isDeal = in.readByte() != 0;
        this.isCouponCode = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<MoreDetails> CREATOR = new Creator<MoreDetails>() {
        @Override
        public MoreDetails createFromParcel(Parcel source) {
            return new MoreDetails(source);
        }

        @Override
        public MoreDetails[] newArray(int size) {
            return new MoreDetails[size];
        }
    };
}
