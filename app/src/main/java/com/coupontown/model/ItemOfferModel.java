package com.coupontown.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;


public class ItemOfferModel implements Parcelable {

    private String category;

    private String name;

    private String logo;

    private String description;

    private String status;

    private MoreDetails moreDetails;

    private boolean isFav;

    private Integer thumpsup;

    private Integer thumpsdown;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MoreDetails getMoreDetails() {
        return moreDetails;
    }

    public void setMoreDetails(MoreDetails moreDetails) {
        this.moreDetails = moreDetails;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public Integer getThumpsup() {
        return thumpsup;
    }

    public void setThumpsup(Integer thumpsup) {
        this.thumpsup = thumpsup;
    }

    public Integer getThumpsdown() {
        return thumpsdown;
    }

    public void setThumpsdown(Integer thumpsdown) {
        this.thumpsdown = thumpsdown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.category);
        dest.writeString(this.name);
        dest.writeString(this.logo);
        dest.writeString(this.description);
        dest.writeString(this.status);
        dest.writeParcelable(this.moreDetails, flags);
        dest.writeByte(this.isFav ? (byte) 1 : (byte) 0);
        dest.writeValue(this.thumpsup);
        dest.writeValue(this.thumpsdown);
    }

    public ItemOfferModel() {
    }

    protected ItemOfferModel(Parcel in) {
        this.category = in.readString();
        this.name = in.readString();
        this.logo = in.readString();
        this.description = in.readString();
        this.status = in.readString();
        this.moreDetails = in.readParcelable(MoreDetails.class.getClassLoader());
        this.isFav = in.readByte() != 0;
        this.thumpsup = (Integer) in.readValue(Integer.class.getClassLoader());
        this.thumpsdown = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<ItemOfferModel> CREATOR = new Creator<ItemOfferModel>() {
        @Override
        public ItemOfferModel createFromParcel(Parcel source) {
            return new ItemOfferModel(source);
        }

        @Override
        public ItemOfferModel[] newArray(int size) {
            return new ItemOfferModel[size];
        }
    };

    @Override
    public String toString() {
        return "ItemOfferModel{" +
                "category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", moreDetails=" + moreDetails +
                ", isFav=" + isFav +
                ", thumpsup=" + thumpsup +
                ", thumpsdown=" + thumpsdown +
                '}';
    }
}
