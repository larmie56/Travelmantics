package com.example.travelmantics;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static final int RC_SIGN_IN = 123;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    public static ArrayList<TravelDeal> mDeals;
    private static ListActivity caller;
    public static boolean isAdmin;


    private FirebaseUtil() {}

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        //Open a reference to the Firebase database
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(caller.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void checkAdmin(String uid) {
        //set the authorization level of the admin.
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                Log.d("Admin", "isAdmin == true");
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //add child event listener to the reference to the Firebase storage for the admin.
        ref.addChildEventListener(listener);
    }

    public static void attachListener() {
        //add authentication state listener to the Firebase database authentication object.
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    public static void detachListener() {
        //remove authentication state listener from the Firebase database authentication object.
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    public static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.icons8_traveler_48)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void connectStorage() {
        //connect to the Firebase storage.
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_pictures");
    }
}
