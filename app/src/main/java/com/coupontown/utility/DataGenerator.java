package com.coupontown.utility;

import com.coupontown.model.ItemOfferModel;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    private final static String android_image_urls[] = {
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


    public static List<ItemOfferModel> generateData(){

        List<ItemOfferModel> data = new ArrayList<>();
        data.add(generatePayTime());
        data.add(generateflopkartoffer());
        data.add(generatePayTime2());
        data.add(generateflopkartoffer());
        return data;

    }

    private static ItemOfferModel generateflopkartoffer(){
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Big Shopping Days: Upto to 80% Off on Mobiles, Electronics, Furniture & more..");
        itemOfferModel.setCategory("Applicances");
        itemOfferModel.setName("FlopKart");

       itemOfferModel.setLogo(android_image_urls[0]);

        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime(){
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setLogo(android_image_urls[1]);
        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime1(){
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setLogo(android_image_urls[2]);
        return itemOfferModel;
    }

    private static ItemOfferModel generatePayTime2(){
        ItemOfferModel itemOfferModel = new ItemOfferModel();
        itemOfferModel.setDescription("Upto Rs 650 offer on Fasttrack railways in UK..");
        itemOfferModel.setCategory("Fashion");
        itemOfferModel.setName("Paytime");
        itemOfferModel.setLogo(android_image_urls[3]);
        return itemOfferModel;
    }
}
