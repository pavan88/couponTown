package com.coupontown.utility;

import android.net.Uri;
import com.coupontown.model.ItemOfferModel;
import com.coupontown.model.MoreDetails;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    public final static String android_image_urls[] = {
            "https://api.learn2crack.com/android/images/donut.png",
            "https://api.learn2crack.com/android/images/eclair.png",
            "https://api.learn2crack.com/android/images/froyo.png",
            "https://api.learn2crack.com/android/images/ginger.png",
            "https://api.learn2crack.com/android/images/honey.png",
            "https://api.learn2crack.com/android/images/icecream.png",
            "https://api.learn2crack.com/android/images/jellybean.png",
            "https://api.learn2crack.com/android/images/kitkat.png",
            "https://api.learn2crack.com/android/images/lollipop.png",
            "https://api.learn2crack.com/android/images/marshmallow.png"
    };


    public static List<ItemOfferModel> generateData() {

        List<ItemOfferModel> data = new ArrayList<>();
        data.add(generatePayTime1());
        data.add(generatePayTime());
        data.add(generateflopkartoffer());
        data.add(generatePayTime2());
        data.add(generateflopkartoffer());

        return data;

    }

    private static ItemOfferModel generateflopkartoffer() {
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Big Shopping Days: Upto to 80% Off on Mobiles, Electronics, Furniture & more..");
        itemOfferModel.setCategory("Applicances");
        itemOfferModel.setName("FlopKart");
        itemOfferModel.setMoreDetails(moreDetails());
        itemOfferModel.setStatus("New!!");
        itemOfferModel.setItem_img(Uri.parse(android_image_urls[0]));

        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime() {
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setItem_img(Uri.parse(android_image_urls[1]));
        itemOfferModel.setStatus("Exipring Soon!!");
        itemOfferModel.setMoreDetails(moreDetails());
        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime1() {
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setItem_img(Uri.parse(android_image_urls[2]));
        itemOfferModel.setStatus("Expired!!");
        itemOfferModel.setMoreDetails(moreDetails());
        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime2() {
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setItem_img(Uri.parse(android_image_urls[3]));
        itemOfferModel.setStatus("Closed!!");
        itemOfferModel.setMoreDetails(moreDetails());
        return itemOfferModel;
    }

    private static MoreDetails moreDetails() {
        MoreDetails moreDetails = new MoreDetails();
        moreDetails.setAppurl("net.one97.paytm");
        moreDetails.setComment("This is really Happy appy from me , I used 2 times");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("1. This offer is existing from 1stMarch2019 to 31August2019\n");
        stringBuffer.append("2. This is valid for one user / offer \n");
        stringBuffer.append("3. This Offer is valid till the stock lasts \n");
        stringBuffer.append("4. CouponTown is not responsible for misuse of the offer ");
        moreDetails.setDetail_desc(stringBuffer.toString());
        return  moreDetails;
    }
}
