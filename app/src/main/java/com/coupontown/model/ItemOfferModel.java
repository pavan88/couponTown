package com.coupontown.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


public class ItemOfferModel implements Parcelable {

    private String category;

    private String name;

    private String logo;

    private String description;

    private Uri item_img;

    private MoreDetails moreDetails;


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

    public Uri getItem_img() {
        return item_img;
    }

    public void setItem_img(Uri item_img) {
        this.item_img = item_img;
    }

    public MoreDetails getMoreDetails() {
        return moreDetails;
    }

    public void setMoreDetails(MoreDetails moreDetails) {
        this.moreDetails = moreDetails;
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
        dest.writeParcelable(this.item_img, flags);
        dest.writeParcelable(this.moreDetails, flags);
    }

    public ItemOfferModel() {
    }

    protected ItemOfferModel(Parcel in) {
        this.category = in.readString();
        this.name = in.readString();
        this.logo = in.readString();
        this.description = in.readString();
        this.item_img = in.readParcelable(Uri.class.getClassLoader());
        this.moreDetails = in.readParcelable(MoreDetails.class.getClassLoader());
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
                ", item_img=" + item_img +
                ", moreDetails=" + moreDetails +
                '}';
    }
}
