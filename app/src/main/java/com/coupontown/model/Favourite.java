package com.coupontown.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Favourite implements Parcelable {

    List<ItemOfferModel> itemOfferModels;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.itemOfferModels);
    }

    public Favourite() {
    }

    protected Favourite(Parcel in) {
        this.itemOfferModels = in.createTypedArrayList(ItemOfferModel.CREATOR);
    }

    public static final Parcelable.Creator<Favourite> CREATOR = new Parcelable.Creator<Favourite>() {
        @Override
        public Favourite createFromParcel(Parcel source) {
            return new Favourite(source);
        }

        @Override
        public Favourite[] newArray(int size) {
            return new Favourite[size];
        }
    };

    public List<ItemOfferModel> getItemOfferModels() {
        return itemOfferModels;
    }

    public void setItemOfferModels(List<ItemOfferModel> itemOfferModels) {
        this.itemOfferModels = itemOfferModels;
    }
}
