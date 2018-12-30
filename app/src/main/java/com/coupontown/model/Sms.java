package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Sms implements Parcelable {

    private String from;

    private String body;

    private int number;

    private boolean status;

    private Date smsrecived;

    protected Sms(Parcel in) {
        from = in.readString();
        body = in.readString();
        number = in.readInt();
        status = in.readByte() != 0;
    }

    public static final Creator<Sms> CREATOR = new Creator<Sms>() {
        @Override
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        @Override
        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getSmsrecived() {
        return smsrecived;
    }

    public void setSmsrecived(Date smsrecived) {
        this.smsrecived = smsrecived;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(body);
        dest.writeInt(number);
        dest.writeByte((byte) (status ? 1 : 0));

    }
}
