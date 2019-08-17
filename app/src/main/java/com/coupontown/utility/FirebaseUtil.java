package com.coupontown.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.coupontown.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtil {


    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private static final String FIREBASE = FirebaseUtil.class.getName();

    public static boolean addnewuser(FirebaseUser firebaseUser) {
        //check user exists.
        //TODO need to optimise the userexists
        if (firebaseUser != null) {
            Log.i(FIREBASE, "Registering the user for the firsttime in firebase");
            //create an entry in db for the first time

            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("user").child(firebaseUser.getUid());

            User user = new User();
            user.setFirebaseUser(firebaseUser);
            databaseReference.setValue(user);
            Log.i(FIREBASE, "****** Profile Information saved Successfully ******");
        }
        return false;
    }

    public static boolean deleteuser(String email) {

        return false;
    }

    public static void updateuser(String fullname, String email, String phone, Uri profilepicurl) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!userexists(firebaseUser)) {
            UserProfileChangeRequest userProfileChangeRequest = profilebuilder(fullname, profilepicurl);

            firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("up", "Username and Profile PIC update successfully");
                    } else {
                        Log.i("up", Log.getStackTraceString(task.getException()));
                    }
                }
            });


            //Current email and new email is not same
            if (email != null && !firebaseUser.getEmail().equalsIgnoreCase(email)) {

                firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail(null);
                            Log.i("upemail", "Email update successfully");
                        } else {
                            Log.i("upemail", Log.getStackTraceString(task.getException()));
                        }
                    }
                });
            }


        }
        //check user exists.
    }

    private static boolean userexists(@NonNull FirebaseUser firebaseUser) {
        final boolean exists = false;
        FirebaseDatabase.getInstance().getReference("users").
                orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return false;
    }


    private static UserProfileChangeRequest profilebuilder(String name, Uri uri) {

        UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest
                .Builder();
        if (name != null) {
            userProfileChangeRequest.setDisplayName(name);
        }
        if (uri != null) {
            userProfileChangeRequest.setPhotoUri(uri);
        }
        return userProfileChangeRequest.build();

    }

    public static void sendVerificationEmail(final Context context) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Toast.makeText(context, "Sent Email Verification Link!", Toast.LENGTH_LONG).show();
                        Log.i(FIREBASE, "sendVerificationEmail: onComplete =>" + task.isSuccessful());
                    } else {
                       // Toast.makeText(context, "Failed::Sent Email Verification Email!", Toast.LENGTH_LONG).show();
                        Log.i(FIREBASE, "failed sendVerificationEmail: onComplete =>" + task.isSuccessful());
                    }
                }
            });
        }
    }

    public static void signout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static FirebaseUser firebaseUser() {

        return FirebaseAuth.getInstance().getCurrentUser();

    }

}
