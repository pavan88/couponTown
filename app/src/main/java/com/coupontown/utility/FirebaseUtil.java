package com.coupontown.utility;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {


    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public static boolean addnewuser(FirebaseUser firebaseUser) {

        return false;
    }

    public static boolean deleteuser(String email) {

        return false;
    }

}
