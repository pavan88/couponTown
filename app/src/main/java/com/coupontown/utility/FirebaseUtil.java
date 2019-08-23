package com.coupontown.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.coupontown.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class FirebaseUtil {

    private static final String FIREBASE = FirebaseUtil.class.getName();
    private static UserProfile userProfile;

    public static UserProfile addnewuser(FirebaseUser firebaseUser) {

        Log.i(FIREBASE, "Registering the user for the firsttime in firebase");
        //create an entry in db for the first time


        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userProfile").child(firebaseUser.getUid());

        String url = "https://firebasestorage.googleapis.com/v0/b/coupontown-bf56f.appspot.com/o/images%2FCzVj7VbDrJaNWIoaCAKf53RIodj2?alt=media&token=ccaf3eb5-08c1-465e-8d16-ff4b64a2a333";


        UserProfile userProfile = mapFirebaseuser(firebaseUser);
        userProfile.setPicurlstr(url);
        databaseReference.setValue(userProfile);
        Log.i(FIREBASE, "****** Profile Information saved Successfully ******");
        return userProfile;
    }

    public static boolean deleteuser(String email) {

        return false;
    }

    public static void updateuser(String fullname, String email, String phone, String profilepicurl) {

        //Now FirebaseUser object is updated, pending is DB
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userProfile").child(firebaseUser.getUid());

        if (profilepicurl != null) {
            databaseReference.child("picurlstr").setValue(profilepicurl);
        }

        if (!fullname.equalsIgnoreCase("")) {
            databaseReference.child("full_name").setValue(fullname);
        }

        if (!phone.equalsIgnoreCase("")) {
            databaseReference.child("phonenumber").setValue(phone);
        }

       /* User user = new User();
        user.setFirebaseUser(firebaseUser);
        databaseReference.setValue(user);*/


        //check user exists.
    }

    public static void sendVerificationEmail(final Context context) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification();
        }
    }

    public static void signout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static FirebaseUser firebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static UserProfile mapFirebaseuser(FirebaseUser firebaseUser) {

        UserProfile userProfile = new UserProfile();


        userProfile.setFull_name(firebaseUser.getDisplayName());

        userProfile.setPhonenumber(firebaseUser.getPhoneNumber());
        userProfile.setEmail(firebaseUser.getEmail());
        if (firebaseUser.getPhotoUrl() != null) {
            userProfile.setPicurlstr(firebaseUser.getPhotoUrl().toString());
        }
        userProfile.setProvider(firebaseUser.getProviderData().get(0).getProviderId());
        userProfile.setLastLogin(new Date());
        userProfile.setUid(firebaseUser.getUid());
        userProfile.setVerified(firebaseUser.isEmailVerified());

        return userProfile;
    }

    public static UserProfile getUserProfile(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("userProfile").child(firebaseUser.getUid());

       ValueEventListener valueEventListener =  new ValueEventListener() {
           @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

        return userProfile;
    }

    public static void updateuser(UserProfile finalProfile) {
        updateuser(finalProfile.getFull_name(), finalProfile.getEmail(), finalProfile.getPhonenumber(), finalProfile.getPicurlstr());
    }
}
