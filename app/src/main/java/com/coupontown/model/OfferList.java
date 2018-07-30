package com.coupontown.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfferList {

        public static HashMap<String, List<String>> getData() {
            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

            List<String> paytmOffers = new ArrayList<String>();
            paytmOffers.add("Electricity");
            paytmOffers.add("Mobile");
            paytmOffers.add("Bus");


            List<String> tezOffers = new ArrayList<String>();
            tezOffers.add("Transfer-Money");
            tezOffers.add("UPI");
            tezOffers.add("Food");
            tezOffers.add("BillPayment");


            List<String> phonePeOffers = new ArrayList<String>();
            phonePeOffers.add("Electricity");
            phonePeOffers.add("Mobile");


            expandableListDetail.put("Paytm Offers", paytmOffers);
            expandableListDetail.put("Tez Offers", tezOffers);
            expandableListDetail.put("PhonePe Offers", phonePeOffers);
            return expandableListDetail;
        }
}
